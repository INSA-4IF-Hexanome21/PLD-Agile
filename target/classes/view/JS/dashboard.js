
// Variable globale pour la carte Leaflet
let carte = null;
let marqueurs = [];
let lignes = [];

// Configuración de colores para cada tipo de site
const COULEURS_SITES = {
  'entrepot': '#16697A',     // Azul oscuro - Almacén
  'livraison': '#82C0CC',    // Azul claro - Entrega
  'collecte': '#FFA62B',     // Naranja - Recolección
  'default': '#667eea'       // Morado - Por defecto
};


/**
 * Charge un composant dans le contenu principal
 * @param {string} url - L'URL du composant à charger
 */
function chargerComposantPrincipal(url) {
  console.log('Chargement du composant:', url);
  
  // Afficher un message de chargement avec animation
  document.getElementById('main-content').innerHTML = '<p>Chargement en cours...</p>';
  
  fetch(url)
    .then(res => {
      if (!res.ok) throw new Error('Erreur de chargement: ' + url);
      return res.text();
    })
    .then(html => {
      document.getElementById('main-content').innerHTML = html;
      
      // Initialiser la carte si on charge PickupDelivery
      if (url.includes('PickupDelivery.html')) {
        setTimeout(initialiserCarte, 100); // Attendre que le DOM soit mis à jour
      }
    })
    .catch(err => {
      console.error("Erreur lors du chargement du composant:", err);
      document.getElementById('main-content').innerHTML = 
        '<p style="color: #e74c3c;">Erreur lors du chargement du composant</p>';
    });
}

/**
 * Initialise la carte Leaflet avec les données du backend
 */
function initialiserCarte() {
  console.log('Initialisation de la carte...');
  
  // Détruire la carte existante si elle existe
  if (carte !== null) {
    carte.remove();
    marqueurs = [];
    lignes = [];
  }

  const elementCarte = document.getElementById('map');
  if (!elementCarte) {
    console.error('Élément #map non trouvé dans le DOM');
    return;
  }

  // Créer la carte centrée sur Lyon
  carte = L.map('map', {
    center: [45.7578137, 4.8320114],
    zoom: 15,
    zoomControl: true,
    preferCanvas: true // Meilleure performance pour beaucoup d'éléments
  });

  // Ajouter la couche OpenStreetMap
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    maxZoom: 19,
  }).addTo(carte);

  console.log('Récupération des données depuis /api/carte...');

  // Charger les données depuis le backend
  fetch("/api/carte")
    .then(res => {
      if (!res.ok) throw new Error('Erreur API: ' + res.status);
      return res.json();
    })
    .then(donnees => {
      console.log('Données reçues:', donnees);
      afficherDonneesSurCarte(donnees);
    })
    .catch(err => {
      console.error('Erreur lors du chargement des données de la carte:', err);
      alert('Impossible de charger les données de la carte. Vérifiez la console pour plus de détails.');
    });
}

/**
 * Obtient la couleur selon le type de site
 * @param {string} type - Le type de site
 * @returns {string} Le code couleur hexadécimal
 */
function obtenirCouleurSite(type) {
  return COULEURS_SITES[type] || COULEURS_SITES['default'];
}

/**
 * Obtient le nom lisible du type de site
 * @param {string} type - Le type de site
 * @returns {string} Le nom en français
 */
function obtenirNomTypeSite(type) {
  const noms = {
    'entrepot': 'Entrepôt',
    'livraison': 'Livraison',
    'collecte': 'Collecte'
  };
  return noms[type] || type.charAt(0).toUpperCase() + type.slice(1);
}
function afficherDonneesSurCarte(donnees) {
  // 1) Tronçons (igual)
  if (donnees.troncons && donnees.troncons.length > 0) {
    console.log('Affichage de', donnees.troncons.length, 'tronçons');
    donnees.troncons.forEach(troncon => {
      const depart = donnees.noeuds.find(n => n.id === troncon.from);
      const arrivee = donnees.noeuds.find(n => n.id === troncon.to);
      if (depart && arrivee) {
        const ligne = L.polyline(
          [[depart.lat, depart.lng], [arrivee.lat, arrivee.lng]], 
          { color: '#667eea', weight: 3, opacity: 0.6, smoothFactor: 1 }
        ).addTo(carte);
        ligne.bindPopup(`
          <strong>Tronçon</strong><br>
          De: ${troncon.from}<br>
          À: ${troncon.to}<br>
          Longueur: ${troncon.longueur ? troncon.longueur.toFixed(2) + 'm' : 'N/A'}
        `);
        lignes.push(ligne);
      }
    });
  }
  

  // 2) Nœuds (dibujamos antes de los sites para que sites queden encima)
  if (donnees.noeuds && donnees.noeuds.length > 0) {
    console.log('Affichage de', donnees.noeuds.length, 'nœuds');
    const iconePersonnalisee = L.divIcon({
      className: 'marqueur-personnalise',
      html: '<div class="marqueur-noeud" style="background: #16697A; width: 12px; height: 12px; border-radius: 50%; border: 3px solid white; box-shadow: 0 2px 8px rgba(22, 105, 122, 0.4);"></div>',
      iconSize: [12, 12],
      iconAnchor: [6, 6]
    });

    donnees.noeuds.forEach(noeud => {
      const marqueur = L.marker([noeud.lat, noeud.lng], { icon: iconePersonnalisee });
      marqueur.addTo(carte);
      marqueur.bindPopup(`
        <div style="font-family: 'Segoe UI', sans-serif;">
          <strong style="color: #667eea; font-size: 1.1em;">Nœud ${noeud.id}</strong><br>
          <span style="color: #666;">Latitude: ${noeud.lat.toFixed(6)}</span><br>
          <span style="color: #666;">Longitude: ${noeud.lng.toFixed(6)}</span>
        </div>
      `);
      marqueurs.push(marqueur);
    });

    // Ajustar la vista incluyendo todos los nœuds (si quieres incluir sites también, ver nota)
    const limites = L.latLngBounds(donnees.noeuds.map(n => [n.lat, n.lng]));
    carte.fitBounds(limites, { padding: [50, 50] });
  } else {
    console.warn('Aucun nœud à afficher');
  }

 // fuera de la función (en el scope donde tienes `carte`, `lignes`, `marqueurs`), añade:
const siteMarkers = []; // almacenar marcadores de sites para poder actualizarlos al hacer zoom

function computeSiteRadius(map) {
  // calcula un radio legible a partir del zoom actual (ajusta multiplicador si quieres más/menos grandes)
  const zoom = map.getZoom ? map.getZoom() : 13;
  return Math.max(10, Math.round(zoom * 1.6)); // mínimo 10px; escala con zoom
}

/* reemplaza la sección // 3) Sites — dibujamos AL FINAL para que se vean por encima
   por el siguiente bloque completo */
console.log('Sites reçus:', donnees.sites);
const typeToColor = {
  depot: '#2b6cb0',
  pickup: '#38a169',
  delivery: '#e53e3e',
  livraison: '#e53e3e', // mapping para tu API (fr)
  collecte: '#38a169'   // mapping para tu API (fr)
};

if (donnees.sites && donnees.sites.length > 0) {
  // limpiar markers previos de sites si es necesario
  siteMarkers.forEach(m => {
    try { carte.removeLayer(m); } catch (e) { /* ignore */ }
  });
  siteMarkers.length = 0;

  // radio inicial según zoom actual
  const initialRadius = computeSiteRadius(carte);

  donnees.sites.forEach(site => {
    const type = (site.type || '').toString().toLowerCase();
    const color = typeToColor[type] || '#999999';

    if (site.lat != null && site.lng != null) {
      const marker = L.circleMarker([site.lat, site.lng], {
        radius: initialRadius,      // radius mayor para que se vean
        fillOpacity: 1,
        color: '#ffffff',           // borde blanco para contraste
        weight: 2,
        fillColor: color,
        pane: 'markerPane'          // asegúrate que estén en un pane visible por encima
      }).addTo(carte);

      // Popup con información y tooltip compacto con id
      marker.bindPopup(`<strong>${type} ${site.id}</strong><br>Lat: ${site.lat.toFixed(6)}<br>Lng: ${site.lng.toFixed(6)}`);
      marker.bindTooltip(`${site.id}`, { permanent: false, direction: 'top', offset: [0, -initialRadius - 6] });

      // que el site siempre quede encima
      if (marker.bringToFront) marker.bringToFront();

      siteMarkers.push(marker);
    } else {
      console.warn('Site sans coordonnées:', site);
    }
  });

  // actualizar tamaño de los siteMarkers cuando se hace zoom para que sigan siendo visibles
  // (Si ya añades este listener en otro lugar, evita doble-binding: aquí lo dejamos idempotente)
  if (!carte._siteZoomHandlerAdded) {
    carte.on('zoomend', () => {
      const newRadius = computeSiteRadius(carte);
      siteMarkers.forEach(m => {
        if (m && m.setRadius) {
          m.setRadius(newRadius);
          // mover tooltip un poco arriba (offset) si existe
          const tip = m.getTooltip && m.getTooltip();
          if (tip && tip._container) {
            // forzar rebind del tooltip para reposicionarlo
            m.unbindTooltip();
            m.bindTooltip(m.options && m.options.tooltipContent ? m.options.tooltipContent : (m.getPopup() ? m.getPopup().getContent() : ''), {
              permanent: false,
              direction: 'top',
              offset: [0, -newRadius - 6]
            });
          }
        }
      });
    });
    carte._siteZoomHandlerAdded = true;
  }
} else {
  console.log('Aucun site à afficher');
}
}

/**
 * Configure les événements du sidebar
 * TODO ICI on doit faire le commande pattern.
 */
function configurerSidebar() {
  const btnCarte = document.getElementById('btn-mapa');
  const btnFiltres = document.getElementById('btn-filtros');
  const btnStatistiques = document.getElementById('btn-estadisticas');

  if (btnCarte) {
    btnCarte.addEventListener('click', () => {
      chargerComposantPrincipal('/components/PickupDelivery.html');
    });
  }
  
  if (btnFiltres) {
    btnFiltres.addEventListener('click', () => {
      document.getElementById('main-content').innerHTML = `
        <div style="text-align: center; padding: 3rem;">
          <h2 style="color: #667eea; margin-bottom: 1rem;">Filtres</h2>
          <p style="color: #666;">Fonctionnalité en construction...</p>
        </div>
      `;
    });
  }
  
  if (btnStatistiques) {
    btnStatistiques.addEventListener('click', () => {
      document.getElementById('main-content').innerHTML = `
        <div style="text-align: center; padding: 3rem;">
          <h2 style="color: #667eea; margin-bottom: 1rem;">Statistiques</h2>
          <p style="color: #666;">Fonctionnalité en construction...</p>
        </div>
      `;
    });
  }
}

// Initialisation au chargement de la page
console.log('Chargement du sidebar...');

fetch('/components/Sidebar.html')
  .then(res => {
    if (!res.ok) throw new Error('Erreur lors du chargement du sidebar');
    return res.text();
  })
  .then(html => {
    document.getElementById('sidebar').innerHTML = html;
    configurerSidebar();
    
    // Charger la carte par défaut
    chargerComposantPrincipal('/components/PickupDelivery.html');
  })
  .catch(err => {
    console.error("Erreur lors du chargement du sidebar:", err);
    document.getElementById('sidebar').innerHTML = 
      '<p style="color: #e74c3c;">Erreur de chargement</p>';
  });