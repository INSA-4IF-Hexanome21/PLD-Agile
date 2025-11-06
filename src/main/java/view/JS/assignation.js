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
// var NB_CLICK = 0; //Variable permettant de savoir si l'évènement click a déjà été défini
var CLICK_DEF = false;
function extraerYMostrarLivraisons(sites) {
  console.log("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
  if(!CLICK_DEF){
    document.getElementById('btn-calcul-tournee').addEventListener('click', () => {
      // if(NB_CLICK%3 === 0){
      if(toutesLivraisonsAssignees()){
        envoyerAssignations();
        lancerCalcul();
        chargerComposantPrincipal('/components/Map.html');
      }
      
        // console.warn('click : ', NB_CLICK);
      // }
      // ++NB_CLICK;
    });
    CLICK_DEF = true;
  }
  

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
  
  // Fusionner avec l’état enregistré
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
    console.log(e);
    e.preventDefault();
    dropzone.classList.remove('dragover');
    
    const livraisonId = parseInt(e.dataTransfer.getData('text/plain'));
    const draggedEl = document.querySelector(`[data-livraison-id="${livraisonId}"]`);
    
    if (draggedEl) {
      console.log(draggedEl);
      // Quitar empty state si existe
      const emptyState = dropzone.querySelector('.empty-state');
      if (emptyState) emptyState.remove();
      
      // Mover el elemento
      dropzone.appendChild(draggedEl);

      //On supprime l'assignation faîtes éventuellement à d'autres livreurs
      Object.keys(assignationsState).forEach(function(key) {
        if(assignationsState[key].includes(livraisonId)){
          const index = assignationsState[key].indexOf(livraisonId);
          assignationsState[key].splice(index,1);
        }
      });
      
      // Actualizar estado
      if (!assignationsState[numero]) assignationsState[numero] = [];
      if (!assignationsState[numero].includes(livraisonId)) {
        assignationsState[numero].push(livraisonId);
      }

      
      
      // Actualizar contador
      // actualizarContadorLivreur(numero);
      actualiserAllConteneurs()
      
      // Actualizar pool
      mostrarLivraisonsDisponibles(livraisonsData);
      
      // Enviar al backend
      //assignerLivraisonAuLivreur(livraisonId, numero);
    }
  });
  
  return div;
}

function actualiserAllConteneurs(){
  var zones = document.querySelectorAll(`.livreur-zone`);
  zones.forEach(function(zone){
    actualizarContadorLivreur(zone.attributes[1].value)
  });
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

/**
 * Vérifie si toutes les livraisons sont déjà assignées...
 */
function toutesLivraisonsAssignees() {
  if (!livraisonsData || livraisonsData.length === 0) return false;

  // Construire set de livraisons assignees
  const assignedIds = new Set();
  Object.values(assignationsState).forEach(arr => arr.forEach(id => assignedIds.add(id))); 
  const allAssigned = livraisonsData.every(liv => assignedIds.has(liv.id)); // Verifier sils sont tous dans le set

  console.log('🔎 Toutes les livraisons assignées ?', allAssigned);
  return allAssigned;
}

/**
 * Réinitialise complètement le système d’assignation
 */
function resetAssignations(nouvellesDonneesSites = null) {
  CLICK_DEF = false;
  console.log('🔄 Réinitialisation des assignations...');

  // Reset des variables globales
  livraisonsData = [];
  assignationsState = {};
  CLICK = 0;

  // Vider les conteneurs
  const poolContainer = document.getElementById('livraisons-list');
  const livreursContainer = document.getElementById('livreurs-zones');
  if (poolContainer) poolContainer.innerHTML = '';
  if (livreursContainer) livreursContainer.innerHTML = '';

  // Supprimer anciens écouteurs éventuels sur le bouton
  const envoyerBtn = document.getElementById('btn-calcul-tournee');
  if (envoyerBtn) {
    const newBtn = envoyerBtn.cloneNode(true);
    envoyerBtn.parentNode.replaceChild(newBtn, envoyerBtn);
  }

  // Si il y'a nouevaus données → reconstruire l'interface
  const sites = nouvellesDonneesSites || (donneesGlobales && donneesGlobales.sites);
  if (sites) {
    console.log('🆕 Chargement de nouvelles livraisons...');
    extraerYMostrarLivraisons(sites);
  } else {
    console.warn('Aucune donnée de sites disponible pour le reset.');
  }
}