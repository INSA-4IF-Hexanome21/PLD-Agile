// upload.js - Gestión de carga de archivos

$(document).ready(function() {
    console.log('🔵 upload.js cargado');
    
    // Actualizar el nombre del archivo en la interfaz
    $(document).on('change', 'input[type=file]', function(e) {
        var fileName = this.files && this.files.length ? this.files[0].name : '';
        var $dummy = $(this).siblings('.file-dummy');
        var $fileArea = $(this).closest('.file-area');
        var uploadType = $fileArea.data('upload-type'); // 'plan' o 'demande'
        
        if (fileName) {
            console.log('📁 Archivo seleccionado:', fileName, '- Tipo:', uploadType);
            
            // Validar que sea XML
            if (!fileName.toLowerCase().endsWith('.xml')) {
                alert('❌ Veuillez sélectionner un fichier XML');
                this.value = '';
                return;
            }
            
            $dummy.find('.default').hide();
            $dummy.find('.selected').text(fileName).show();
            
            // Subir el archivo automáticamente
            subirArchivo(this.files[0], uploadType);
        } else {
            $dummy.find('.selected').hide();
            $dummy.find('.default').show();
        }
    });
    
    // Soporte para drag & drop
    $(document).on('dragover', '.file-area', function(e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).addClass('dragover');
    });
    
    $(document).on('dragleave', '.file-area', function(e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).removeClass('dragover');
    });
    
    $(document).on('drop', '.file-area', function(e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).removeClass('dragover');
        
        var files = e.originalEvent.dataTransfer.files;
        if (files.length > 0) {
            var $input = $(this).find('input[type=file]');
            $input[0].files = files;
            $input.trigger('change');
        }
    });
    
    // Botón de retour
    $(document).on('click', '#btn-retour-carte', function() {
        console.log('🔙 Retour à la carte');
        $('#btn-mapa').trigger('click');
    });
});

function subirArchivo(file, uploadType) {
    if (!file) {
        console.error('❌ No hay archivo para subir');
        return;
    }
    
    // Determinar el endpoint según el tipo
    var endpoint = uploadType === 'plan' ? '/api/upload/plan' : '/api/upload/demande';
    var statusId = uploadType === 'plan' ? '#status-plan' : '#status-demande';
    
    console.log('📤 Iniciando subida:', file.name, file.size, 'bytes', '→', endpoint);
    
    // Mostrar estado de carga
    $(statusId).removeClass('success error').addClass('loading')
        .text('⏳ Chargement en cours...').show();
    
    // Leer el archivo como ArrayBuffer
    var reader = new FileReader();
    
    reader.onload = function(e) {
        var arrayBuffer = e.target.result;
        console.log('✅ Archivo leído:', arrayBuffer.byteLength, 'bytes');
        
        // Enviar directamente el ArrayBuffer
        fetch(endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/xml',
                'X-File-Name': encodeURIComponent(file.name)
            },
            body: arrayBuffer
        })
        .then(response => {
            console.log('📥 Respuesta del servidor:', response.status);
            if (!response.ok) {
                throw new Error('Erreur serveur: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            console.log('✅ Archivo subido y procesado:', data);
            
            $(statusId).removeClass('loading error').addClass('success')
                .text('✅ ' + file.name + ' chargé avec succès!');
            
            // Si es un plan, recargar el mapa automáticamente
            if (uploadType === 'plan') {
                setTimeout(function() {
                    console.log('🔄 Recargando mapa...');
                    $('#btn-mapa').trigger('click');
                }, 1500);
            }
        })
        .catch(err => {
            console.error('❌ Error al subir archivo:', err);
            $(statusId).removeClass('loading success').addClass('error')
                .text('❌ Erreur: ' + err.message);
        });
    };
    
    reader.onerror = function(e) {
        console.error('❌ Error al leer el archivo:', e);
        $(statusId).removeClass('loading success').addClass('error')
            .text('❌ Erreur lors de la lecture du fichier');
    };
    
    reader.readAsArrayBuffer(file);
}