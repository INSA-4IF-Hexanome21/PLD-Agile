// upload.js - Gestion de chargement de fichiers avec validation d'état

$(document).ready(function() {
    console.log('🔵 upload.js chargé');
    
    // Initialiser l'interface selon l'état actuel
    updateUIBasedOnState();
    
    // S'abonner aux changements d'état
    if (window.appController) {
        window.appController.addStateListener(updateUIBasedOnState);
    }
    
    // Gestionnaire de changement de fichier
    $(document).on('change', 'input[type=file]', function(e) {
        var fileName = this.files && this.files.length ? this.files[0].name : '';
        var $dummy = $(this).siblings('.file-dummy');
        var $fileArea = $(this).closest('.file-area');
        var uploadType = $fileArea.data('upload-type'); // 'plan' ou 'demande'
        
        if (fileName) {
            console.log('📁 Fichier sélectionné:', fileName, '- Type:', uploadType);
            
            // Validation: fichier XML
            if (!fileName.toLowerCase().endsWith('.xml')) {
                alert('Veuillez sélectionner un fichier XML');
                this.value = '';
                return;
            }
            
            // Validation: état de l'application
            if (uploadType === 'demande') {
                if (!window.appController.canPerformAction('loadLivraison')) {
                    alert('⚠️ Veuillez d\'abord charger un plan de distribution!');
                    this.value = '';
                    return;
                }
            }
            
            $dummy.find('.default').hide();
            $dummy.find('.selected').text(fileName).show();
            
            // Télécharger le fichier automatiquement
            subirArchivo(this.files[0], uploadType);
            setTimeout(initialiserCarte, 1000);
        } else {
            $dummy.find('.selected').hide();
            $dummy.find('.default').show();
        }
    });
    
    // Support drag & drop
    $(document).on('dragover', '.file-area', function(e) {
        e.preventDefault();
        e.stopPropagation();
        
        // Vérifier si l'action est permise
        var uploadType = $(this).data('upload-type');
        if (uploadType === 'demande' && !window.appController.canPerformAction('loadLivraison')) {
            $(this).addClass('drag-disabled');
            return;
        }
        
        $(this).addClass('dragover');
    });
    
    $(document).on('dragleave', '.file-area', function(e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).removeClass('dragover drag-disabled');
    });
    
    $(document).on('drop', '.file-area', function(e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).removeClass('dragover drag-disabled');
        
        // Vérifier si l'action est permise
        var uploadType = $(this).data('upload-type');
        if (uploadType === 'demande' && !window.appController.canPerformAction('loadLivraison')) {
            alert('⚠️ Veuillez d\'abord charger un plan de distribution!');
            return;
        }
        
        var files = e.originalEvent.dataTransfer.files;
        if (files.length > 0) {
            var $input = $(this).find('input[type=file]');
            $input[0].files = files;
            $input.trigger('change');
        }
    });
    
    // Bouton de retour
    $(document).on('click', '#btn-retour-carte', function() {
        console.log('🔙 Retour à la carte');
        $('#btn-map').trigger('click');
    });
});

/**
 * Met à jour l'interface selon l'état de l'application
 */
function updateUIBasedOnState() {
    if (!window.appController) return;
    
    const state = window.appController.getStateInfo();
    console.log('🎨 Mise à jour UI selon état:', state);
    
    // Activer/désactiver la zone de demande
    const $demandeArea = $('.file-area[data-upload-type="demande"]');
    const $demandeInput = $demandeArea.find('input[type=file]');
    const $demandeDummy = $demandeArea.find('.file-dummy');
    
    // Retirer message précédent
    $demandeArea.find('.info-message').remove();
    
    if (state.canLoadLivraison) {
        $demandeArea.removeClass('disabled');
        $demandeInput.prop('disabled', false);
        $demandeDummy.css('opacity', '1');
        $demandeDummy.css('cursor', 'pointer');
    } else {
        $demandeArea.addClass('disabled');
        $demandeInput.prop('disabled', true);
        $demandeDummy.css('opacity', '0.5');
        $demandeDummy.css('cursor', 'not-allowed');
        
        // Ajouter un message informatif
        $demandeArea.append(
            '<div class="info-message" style="margin-top:10px; padding:10px; background:#fff3cd; border-left:4px solid #ffc107; color:#856404; font-size:0.9em; border-radius:4px;">' +
            '⚠️ Veuillez d\'abord charger un plan de distribution</div>'
        );
    }

    //Choisir les livreurs
    if(state.canCalculate){
        

    }
    
    // Afficher les indicateurs de statut
    if (state.carteChargee) {
        $('#status-plan').removeClass('loading error').addClass('success')
            .html('✅ Plan de distribution chargé').show();
    } else {
        $('#status-demande').hide();
    }
    
    if (state.livraisonChargee) {
        $('#status-demande').removeClass('loading error').addClass('success')
            .html('✅ Demandes de livraison chargées').show();
    }
}

//Helper pour mettre à jour les états lors des erreurs d'upload
function miseAJourEtat (uploadType){
    if (window.appController) {
                try {
                    if (uploadType === 'plan') {
                        window.appController.onInitial();
                    } else if (uploadType === 'demande') {
                        window.appController.onCarteLoaded();
                    }
                } catch (err) {
                    console.error('Erreur côté contrôleur :', err);
                    $(statusId).removeClass('loading success').addClass('error')
                        .text('' + (err.message || 'Erreur interne'));
                }
            }
}

// Helper pour afficher des erreurs d'upload
function afficherErreurUpload(statusId, httpStatus, serverBody) {
    var messageAmical = 'Erreur : ';
    if (httpStatus === 500) {
        messageAmical += "Le fichier n'est pas lisible ou n'a pas le bon format";
    } else if (serverBody && serverBody.message) {
        messageAmical += serverBody.message;
    } else {
        messageAmical += 'Erreur serveur (' + httpStatus + ')';
    }

    $(statusId).removeClass('loading success').addClass('error').text(messageAmical);
}

/**
 * Téléverse un fichier vers le serveur
 * @param {File} file - Fichier à téléverser
 * @param {string} uploadType - 'plan' ou 'demande'
 */
function subirArchivo(file, uploadType) {
    if (!file) {
        console.error('Aucun fichier à téléverser');
        return;
    }
    
    var endpoint = uploadType === 'plan' ? '/api/upload/plan' : '/api/upload/demande';
    var statusId = uploadType === 'plan' ? '#status-plan' : '#status-demande';
    
    console.log('📤 Début du téléversement :', file.name, file.size, 'octets →', endpoint);
    
    // État visuel : chargement
    $(statusId).removeClass('success error').addClass('loading')
        .text('⏳ Chargement en cours...').show();
    
    var reader = new FileReader();
    
    reader.onload = function(e) {
        var arrayBuffer = e.target.result;
        console.log('✅ Fichier lu en mémoire :', arrayBuffer.byteLength, 'octets');
        
        fetch(endpoint, {
            method: 'POST',
            headers: {
                // On précise le type pour info ; l'envoi est un ArrayBuffer
                'Content-Type': 'application/xml',
                'X-File-Name': encodeURIComponent(file.name)
            },
            body: arrayBuffer
        })
        .then(response => {
            if (!response.ok) {
                // Tenter de parser un JSON d'erreur si présent, sinon renvoyer texte brut
                return response.text().then(text => {
                    let parsed = null;
                    try { parsed = JSON.parse(text); } catch (e) { parsed = null; }
                    return Promise.reject({ status: response.status, body: parsed });
                });
            }
            // OK -> JSON normal attendu
            return response.json();
        })
        .then(data => {
            console.log('✅ Téléversement traité par le serveur :', data);
            
            $(statusId).removeClass('loading error').addClass('success')
                .text('✅ ' + file.name + ' chargé avec succès !');
            
            // Notifier le contrôleur local
            if (window.appController) {
                try {
                    if (uploadType === 'plan') {
                        window.appController.onCarteLoaded();
                    } else if (uploadType === 'demande') {
                        window.appController.onLivraisonLoaded();
                    }
                } catch (err) {
                    console.error('Erreur côté contrôleur :', err);
                    $(statusId).removeClass('loading success').addClass('error')
                        .text('' + (err.message || 'Erreur interne'));
                }
            }
        })
        .catch(err => {
            console.error('Erreur lors du téléversement :', err);
            // err peut être un Error ou l'objet {status, body}
            if (err && typeof err === 'object' && 'status' in err) {
                afficherErreurUpload(statusId, err.status, err.body);
            } else {
                $(statusId).removeClass('loading success').addClass('error')
                    .text('Erreur : ' + (err.message || String(err)));
            }
            miseAJourEtat(uploadType);
        });
    };
    
    reader.onerror = function(e) {
        console.error('Erreur de lecture du fichier :', e);
        $(statusId).removeClass('loading success').addClass('error')
            .text('Erreur lors de la lecture du fichier');
    };
    
    reader.readAsArrayBuffer(file);
}