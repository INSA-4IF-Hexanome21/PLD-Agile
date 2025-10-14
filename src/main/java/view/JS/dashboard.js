// Variable globale pour la carte Leaflet
let carte = null;
let marqueurs = [];
let lignes = [];

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
 * Affiche les nœuds et tronçons sur la carte
 * @param {Object} donnees - Les données contenant noeuds et troncons
 */
function afficherDonneesSurCarte(donnees) {
  // Ajouter les tronçons d'abord (pour qu'ils soient sous les marqueurs)
  if (donnees.troncons && donnees.troncons.length > 0) {
    console.log('Affichage de', donnees.troncons.length, 'tronçons');
    
    donnees.troncons.forEach(troncon => {
      const depart = donnees.noeuds.find(n => n.id === troncon.from);
      const arrivee = donnees.noeuds.find(n => n.id === troncon.to);
      
      if (depart && arrivee) {
        const ligne = L.polyline(
          [[depart.lat, depart.lng], [arrivee.lat, arrivee.lng]], 
          { 
            color: '#667eea',
            weight: 3,
            opacity: 0.6,
            smoothFactor: 1
          }
        ).addTo(carte);
        
        // Ajouter un popup au survol
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

  // Ajouter les nœuds avec des marqueurs personnalisés
  if (donnees.noeuds && donnees.noeuds.length > 0) {
    console.log('Affichage de', donnees.noeuds.length, 'nœuds');
    
    // Créer une icône personnalisée avec la palette SPIE
    const iconePersonnalisee = L.divIcon({
      className: 'marqueur-personnalise',
      html: '<div class="marqueur-noeud" style="background: #16697A; width: 12px; height: 12px; border-radius: 50%; border: 3px solid white; box-shadow: 0 2px 8px rgba(22, 105, 122, 0.4);"></div>',
      iconSize: [12, 12],
      iconAnchor: [6, 6]
    });
    
    donnees.noeuds.forEach(noeud => {
      const marqueur = L.marker([noeud.lat, noeud.lng], {
        icon: iconePersonnalisee
      }).addTo(carte);
      
      // Popup avec informations du nœud
      marqueur.bindPopup(`
        <div style="font-family: 'Segoe UI', sans-serif;">
          <strong style="color: #667eea; font-size: 1.1em;">Nœud ${noeud.id}</strong><br>
          <span style="color: #666;">Latitude: ${noeud.lat.toFixed(6)}</span><br>
          <span style="color: #666;">Longitude: ${noeud.lng.toFixed(6)}</span>
        </div>
      `);
      
      marqueurs.push(marqueur);
    });

    // Ajuster la vue pour inclure tous les nœuds
    const limites = L.latLngBounds(donnees.noeuds.map(n => [n.lat, n.lng]));
    carte.fitBounds(limites, { padding: [50, 50] });
    
    console.log('Carte initialisée avec succès');
  } else {
    console.warn('Aucun nœud à afficher');
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