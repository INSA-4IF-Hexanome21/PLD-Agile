// upload.js - Gestión de carga de archivos

$(document).ready(function() {
    console.log('🔵 upload.js cargado');
    
    // Actualizar el nombre del archivo en la interfaz
    $(document).on('change', 'input[type=file]', function(e) {
        var fileName = this.files && this.files.length ? this.files[0].name : '';
        var $dummy = $(this).siblings('.file-dummy');
        
        if (fileName) {
            console.log('📁 Archivo seleccionado:', fileName);
            $dummy.find('.default').hide();
            $dummy.find('.selected').text(fileName).show();
            
            // Subir el archivo automáticamente
            subirArchivo(this.files[0]);
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
});

function subirArchivo(file) {
    if (!file) {
        console.error('❌ No hay archivo para subir');
        return;
    }
    
    console.log('📤 Iniciando subida:', file.name, file.size, 'bytes');
    
    // Leer el archivo como ArrayBuffer
    var reader = new FileReader();
    
    reader.onload = function(e) {
        var arrayBuffer = e.target.result;
        console.log('✅ Archivo leído:', arrayBuffer.byteLength, 'bytes');
        
        // Enviar directamente el ArrayBuffer (como espera el servidor Java)
        fetch('/api/upload', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/octet-stream',
                'X-File-Name': encodeURIComponent(file.name)
            },
            body: arrayBuffer
        })
        .then(response => {
            console.log('📥 Respuesta del servidor:', response.status);
            return response.json();
        })
        .then(data => {
            console.log('✅ Archivo subido correctamente:', data);
            alert('✅ Archivo "' + file.name + '" subido correctamente');
        })
        .catch(err => {
            console.error('❌ Error al subir archivo:', err);
            alert('❌ Error al subir el archivo: ' + err.message);
        });
    };
    
    reader.onerror = function(e) {
        console.error('❌ Error al leer el archivo:', e);
        alert('❌ Error al leer el archivo');
    };
    
    reader.readAsArrayBuffer(file);
}