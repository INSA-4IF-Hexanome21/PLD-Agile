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
                alert('❌ Veuillez sélectionner un fichier XML');
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
        $('#btn-mapa').trigger('click');
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
    
    // Afficher les indicateurs de statut
    if (state.carteChargee) {
        $('#status-plan').removeClass('loading error').addClass('success')
            .html('✅ Plan de distribution chargé').show();
    } else {
        $('#status-plan').hide();
    }
    
    if (state.livraisonChargee) {
        $('#status-demande').removeClass('loading error').addClass('success')
            .html('✅ Demandes de livraison chargées').show();
    } else {
        $('#status-demande').hide();
    }
}

/**
 * Télécharge un fichier vers le serveur
 * @param {File} file - Fichier à télécharger
 * @param {string} uploadType - Type: 'plan' ou 'demande'
 */
function subirArchivo(file, uploadType) {
    if (!file) {
        console.error('❌ Pas de fichier à télécharger');
        return;
    }
    
    // Déterminer l'endpoint selon le type
    var endpoint = uploadType === 'plan' ? '/api/upload/plan' : '/api/upload/demande';
    var statusId = uploadType === 'plan' ? '#status-plan' : '#status-demande';
    
    console.log('📤 Début téléchargement:', file.name, file.size, 'octets', '→', endpoint);
    
    // Afficher l'état de chargement
    $(statusId).removeClass('success error').addClass('loading')
        .text('⏳ Chargement en cours...').show();
    
    // Lire le fichier comme ArrayBuffer
    var reader = new FileReader();
    
    reader.onload = function(e) {
        var arrayBuffer = e.target.result;
        console.log('✅ Fichier lu:', arrayBuffer.byteLength, 'octets');
        
        // Envoyer directement l'ArrayBuffer
        fetch(endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/xml',
                'X-File-Name': encodeURIComponent(file.name)
            },
            body: arrayBuffer
        })
        .then(response => {
            console.log('📥 Réponse du serveur:', response.status);
            if (!response.ok) {
                throw new Error('Erreur serveur: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            console.log('✅ Fichier téléchargé et traité:', data);
            
            $(statusId).removeClass('loading error').addClass('success')
                .text('✅ ' + file.name + ' chargé avec succès!');
            
            // Notifier le contrôleur du succès
            if (window.appController) {
                try {
                    if (uploadType === 'plan') {
                        window.appController.onCarteLoaded();
                        // Afficher message encourageant à charger la demande
                        setTimeout(() => {
                            if (!window.appController.getStateInfo().livraisonChargee) {
                            }
                        }, 500);
                    } else if (uploadType === 'demande') {
                        window.appController.onLivraisonLoaded();
                    }
                } catch (err) {
                    console.error('❌ Erreur contrôleur:', err);
                    alert('⚠️ ' + err.message);
                    // Réinitialiser le status en cas d'erreur
                    $(statusId).removeClass('loading success').addClass('error')
                        .text('❌ ' + err.message);
                    return;
                }
            }
        })
        .catch(err => {
            console.error('❌ Erreur lors du téléchargement:', err);
            $(statusId).removeClass('loading success').addClass('error')
                .text('❌ Erreur: ' + err.message);
        });
    };
    
    reader.onerror = function(e) {
        console.error('❌ Erreur lors de la lecture du fichier:', e);
        $(statusId).removeClass('loading success').addClass('error')
            .text('❌ Erreur lors de la lecture du fichier');
    };
    
    reader.readAsArrayBuffer(file);
}