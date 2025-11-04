let livraisonsData = [];
let assignationsState = {}; // { livreurId: [livraisonId, ...] }

/**
 * Initialise le système d'assignation
 */
function assignationLivraison() {
  console.log('🚚 Setup assignation livreurs...');
  
  const form = document.getElementById('form-livreurs');
  if (!form) {
    console.warn('form-livreurs no encontrado');
    return;
  }

  form.addEventListener('submit', (e) => {
    e.preventDefault();
    const nb = parseInt(document.getElementById('nbLivreur').value) || 1;
    genererZonesLivreurs(nb);
  });

  if (donneesGlobales && donneesGlobales.sites) {
    extraerYMostrarLivraisons(donneesGlobales.sites);
  } else {
    console.warn('donneesGlobales.sites no disponible aún');
  }
}

function envoyerAssignations() {
  fetch('/api/assignations', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(assignationsState)
  })
  .then(res => res.json())
  .then(data => {
    console.log('✅ Assignations envoyées:', data);
  })
  .catch(err => {
    console.error('❌ Erreur lors de l’envoi des assignations:', err);
  });
}

/**
 * Extrae livraisons de los sites y las agrupa
 */
function extraerYMostrarLivraisons(sites) {
  
  document.getElementById('envoyer-assignations').addEventListener('click', () => {
  envoyerAssignations();
});

  console.log('📦 Extrayendo livraisons de sites...', sites);
  
  const livraisonsMap = new Map();
  
  sites.forEach(site => {
    const numLiv = site.numLivraison;
    if (!numLiv) return;
    
    if (!livraisonsMap.has(numLiv)) {
      livraisonsMap.set(numLiv, { 
        id: numLiv, 
        collecte: null, 
        depot: null 
      });
    }
    
    const liv = livraisonsMap.get(numLiv);
    if (site.type === 'collecte') liv.collecte = site;
    if (site.type === 'depot') liv.depot = site;
  });
  
  livraisonsData = Array.from(livraisonsMap.values())
    .filter(liv => liv.collecte && liv.depot);
  
  console.log('✅ Livraisons encontradas:', livraisonsData);
  mostrarLivraisonsDisponibles(livraisonsData);
}

/**
 * Muestra las livraisons disponibles en el pool
 */
function mostrarLivraisonsDisponibles(livraisons) {
  const container = document.getElementById('livraisons-list');
  if (!container) {
    console.warn('livraisons-list no encontrado');
    return;
  }

  container.innerHTML = '';
  
  // Filtrar solo las NO asignadas
  const assignedIds = new Set();
  Object.values(assignationsState).forEach(arr => arr.forEach(id => assignedIds.add(id)));
  
  const disponibles = livraisons.filter(liv => !assignedIds.has(liv.id));
  
  if (disponibles.length === 0) {
    container.innerHTML = '<p class="empty-state">Toutes les livraisons sont assignées ✓</p>';
    return;
  }
  
  disponibles.forEach(liv => {
    const el = crearElementoLivraison(liv);
    container.appendChild(el);
  });
}

/**
 * Crea un elemento visual para una livraison
 */
function crearElementoLivraison(livraison) {
  const div = document.createElement('div');
  div.className = 'livraison-card';
  div.draggable = true;
  div.dataset.livraisonId = livraison.id;
  
  const collecteLat = livraison.collecte.lat.toFixed(4);
  const collecteLng = livraison.collecte.lng.toFixed(4);
  const depotLat = livraison.depot.lat.toFixed(4);
  const depotLng = livraison.depot.lng.toFixed(4);
  
  div.innerHTML = `
    <div class="livraison-header">
      <strong class="livraison-numero">L${livraison.id}</strong>
    </div>
    <div class="livraison-details">
      <div class="livraison-point collecte-point">
        <span class="point-icon">📍</span>
        <span class="point-coords">${collecteLat}, ${collecteLng}</span>
      </div>
      <div class="livraison-point depot-point">
        <span class="point-icon">📦</span>
        <span class="point-coords">${depotLat}, ${depotLng}</span>
      </div>
    </div>
  `;
  
  div.addEventListener('dragstart', (e) => {
    e.dataTransfer.effectAllowed = 'move';
    e.dataTransfer.setData('text/plain', livraison.id);
    div.classList.add('dragging');
  });
  
  div.addEventListener('dragend', () => {
    div.classList.remove('dragging');
  });
  
  return div;
}

/**
 * Genera las zonas de drop para N livreurs (preservando asignaciones)
 */
function genererZonesLivreurs(nombre) {
  const container = document.getElementById('livreurs-zones');
  if (!container) return;
  
  // Guardar estado actual antes de limpiar
  const estadoActual = {};
  container.querySelectorAll('.livreur-zone').forEach(zone => {
    const livreurId = parseInt(zone.dataset.livreurId);
    const cards = Array.from(zone.querySelectorAll('.livraison-card'));
    estadoActual[livreurId] = cards.map(c => parseInt(c.dataset.livraisonId));
  });
  
  // Merge con el estado guardado
  Object.keys(estadoActual).forEach(key => {
    if (!assignationsState[key]) assignationsState[key] = [];
    estadoActual[key].forEach(id => {
      if (!assignationsState[key].includes(id)) {
        assignationsState[key].push(id);
      }
    });
  });
  
  container.innerHTML = '';
  
  for (let i = 1; i <= nombre; i++) {
    const zone = crearZoneLivreur(i);
    container.appendChild(zone);
    
    // Restaurar livraisons asignadas
    if (assignationsState[i] && assignationsState[i].length > 0) {
      const dropzone = zone.querySelector('.dropzone-content');
      const emptyState = dropzone.querySelector('.empty-state');
      if (emptyState) emptyState.remove();
      
      assignationsState[i].forEach(livraisonId => {
        const livraison = livraisonsData.find(l => l.id === livraisonId);
        if (livraison) {
          const el = crearElementoLivraison(livraison);
          dropzone.appendChild(el);
        }
      });
    }
  }
  
  // Actualizar pool (quitar las asignadas)
  mostrarLivraisonsDisponibles(livraisonsData);
}

/**
 * Crea una zona de drop para un livreur
 */
function crearZoneLivreur(numero) {
  const div = document.createElement('div');
  div.className = 'livreur-zone';
  div.dataset.livreurId = numero;
  
  div.innerHTML = `
    <div class="livreur-header">
      <h4>Livreur ${numero}</h4>
      <span class="livraison-count">0</span>
    </div>
    <div class="dropzone-content" data-livreur="${numero}">
      <p class="empty-state">Glissez les livraisons ici</p>
    </div>
  `;
  
  const dropzone = div.querySelector('.dropzone-content');
  
  dropzone.addEventListener('dragover', (e) => {
    e.preventDefault();
    dropzone.classList.add('dragover');
  });
  
  dropzone.addEventListener('dragleave', () => {
    dropzone.classList.remove('dragover');
  });
  
  dropzone.addEventListener('drop', (e) => {
    e.preventDefault();
    dropzone.classList.remove('dragover');
    
    const livraisonId = parseInt(e.dataTransfer.getData('text/plain'));
    const draggedEl = document.querySelector(`[data-livraison-id="${livraisonId}"]`);
    
    if (draggedEl) {
      // Quitar empty state si existe
      const emptyState = dropzone.querySelector('.empty-state');
      if (emptyState) emptyState.remove();
      
      // Mover el elemento
      dropzone.appendChild(draggedEl);
      
      // Actualizar estado
      if (!assignationsState[numero]) assignationsState[numero] = [];
      if (!assignationsState[numero].includes(livraisonId)) {
        assignationsState[numero].push(livraisonId);
      }
      
      // Actualizar contador
      actualizarContadorLivreur(numero);
      
      // Actualizar pool
      mostrarLivraisonsDisponibles(livraisonsData);
      
      // Enviar al backend
      //assignerLivraisonAuLivreur(livraisonId, numero);
    }
  });
  
  return div;
}

/**
 * Actualiza el contador de livraisons de un livreur
 */
function actualizarContadorLivreur(livreurId) {
  const zone = document.querySelector(`.livreur-zone[data-livreur-id="${livreurId}"]`);
  if (!zone) return;
  
  const count = zone.querySelectorAll('.livraison-card').length;
  const badge = zone.querySelector('.livraison-count');
  if (badge) {
    badge.textContent = count;
    badge.style.display = count > 0 ? 'inline-block' : 'none';
  }
}

/**
 * Envia la asignación al backend
 */
function assignerLivraisonAuLivreur(livraisonId, livreurId) {
  console.log(`📮 Assignant L${livraisonId} → Livreur ${livreurId}`);
  
  fetch('/api/assigner', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ livraisonId, livreurId })
  })
  .then(res => res.json())
  .then(data => console.log('✅ Assigné:', data))
  .catch(err => console.error('❌ Erreur assignation:', err));
}

/**
 * Permite devolver una livraison al pool
 */
function devolverAlPool(livraisonId) {
  // Buscar en qué zona está
  for (let livreurId in assignationsState) {
    const index = assignationsState[livreurId].indexOf(livraisonId);
    if (index > -1) {
      assignationsState[livreurId].splice(index, 1);
      actualizarContadorLivreur(parseInt(livreurId));
      break;
    }
  }
  
  mostrarLivraisonsDisponibles(livraisonsData);
}