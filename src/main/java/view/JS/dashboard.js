// Variables globales
let carte = null;
let trajetsFlottantControl;
let sitesFlottantImpactesControl;
let marqueurs = [];
let lignes = [];
let siteMarkers = [];
let noeudMarkers = [];
let tronconLines = [];
let trajetLines = {};
let sitesImpactes = [];
let donneesGlobales = null;

const COULEURS_SITES = {
  'depot': '#e53e3e',
  'collecte': '#38a169', 
  'entrepot': '#2b6cb0',
  'default': '#999999'
};

// Estado de visibilidad
const visibilityState = {
  entrepot: true,
  collecte: true,
  depot: true,
  noeuds: true,
  troncons: true,
  trajets: true
};


/* //! ----------------- UTILIDADES / INIT ----------------- */
function chargerComposantPrincipal(url) {
  console.log('Chargement du composant:', url);
  const main = document.getElementById('main-content');
  if (!main) {
    console.error('Element #main-content introuvable');
    return;
  }
  // main.innerHTML = '<p>Chargement en cours...</p>';

  fetch(url)
    .then(res => {
      if (!res.ok) throw new Error('Erreur de chargement: ' + url + ' (' + res.status + ')');
      return res.text();
    })
    .then(html => {
      main.innerHTML = html;
      if (url.includes('Map.html')) {
        requestAnimationFrame(() => {
          // Si le map est présent initialiser direcetmenet
          if (document.getElementById('map')) {
            console.log("L 53 DASH.JS");
            initialiserCarte();
            // assignationLivraison();
            return;
          }
          // fallback après un court délai
          console.warn('#map introuvable au premier passage, tentative de secours...');
          // setTimeout(() => {
          //   if (document.getElementById('map')) {
          //     initialiserCarte();
          //     assignationLivraison();
          //   } else {
          //     // DEBUG
          //     console.error('Élément #map introuvable après fallback — vérifie ton Map.html (doit contenir <div id="map">) et l\'insertion du composant.');
          //     main.innerHTML = '<p style="color: #e74c3c;">Élément #map introuvable. Vérifiez Map.html.</p>';
          //   }
          // }, 250); 
        });
      }
    })
    .catch(err => {
      console.error("Erreur lors du chargement du composant:", err);
      main.innerHTML = '<p style="color: #e74c3c;">Erreur lors du chargement du composant</p>';
    });
}

function computeSiteRadius(map) {
  const zoom = map && map.getZoom ? map.getZoom() : 13;
  return Math.max(4, Math.min(12, Math.round(zoom * 1.2)));
}

/* //! ----------------- MAP INITIALIZATION ----------------- */

function initialiserCarte() {
  console.log('Initialisation de la carte...');

  nettoyerCarte();

  const elementCarte = document.getElementById('map');

  if (!elementCarte) {
    console.error('Élément #map non trouvé dans le DOM');
    const main = document.getElementById('main-content');
    if (main) main.innerHTML = '<p style="color: #e74c3c;">Élément #map introuvable</p>';
    return;
  }

  carte = L.map('map', {
    center: [45.7578137, 4.8320114],
    zoom: 15,
    zoomControl: true,
    preferCanvas: true
  });

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap',
    maxZoom: 19
  }).addTo(carte);

  // --- Contrôle Undo/Redo ---
  const UndoRedoControl = L.Control.extend({
    options: {
      position: 'topleft' 
    },

    onAdd: function () {
      const container = L.DomUtil.create('div', 'leaflet-control-undoRedo leaflet-bar leaflet-control');

      const undoBtn = L.DomUtil.create('a', 'undo-btn', container);
      undoBtn.href = '#';
      undoBtn.title = 'Annuler';
      undoBtn.innerHTML = `<img src="/images/undo.svg" style="width:18px;height:18px;margin:6px;" />`

      const redoBtn = L.DomUtil.create('a', 'redo-btn', container);
      redoBtn.href = '#';
      redoBtn.title = 'Rétablir';
      redoBtn.innerHTML = '<img src="/images/redo.svg" style="width:18px;height:18px;margin:6px;" />';

      // Empêche la propagation des clics pour ne pas déclencher le zoom/pan de Leaflet
      L.DomEvent.disableClickPropagation(container);

      // Ajouter les événements
      L.DomEvent.on(undoBtn, 'click', L.DomEvent.stop)
                .on(undoBtn, 'click', () => undoAction());

      L.DomEvent.on(redoBtn, 'click', L.DomEvent.stop)
                .on(redoBtn, 'click', () => redoAction());

      return container;
    }
  });
  new UndoRedoControl().addTo(carte);

  // --- Créer le panneau flottant des trajets (unique) ---
  L.Control.TrajetsFlottant = L.Control.extend({
    onAdd: function(map) {
      const container = L.DomUtil.create('div', 'control-trajets');

      // En-tête
      const header = L.DomUtil.create('div', 'trajets-header', container);
      header.innerHTML = 'Gestion des trajets';

      // Corps
      const body = L.DomUtil.create('div', 'trajets-body', container);
      container._body = body;

      L.DomEvent.disableClickPropagation(container);
      L.DomEvent.disableScrollPropagation(container);

      return container;
    },
    onRemove: function(map) {}
  });

  L.control.trajetsFlottant = function(opts) {
    return new L.Control.TrajetsFlottant(opts);
  };

  trajetsFlottantControl = L.control.trajetsFlottant({ position: 'topright' });
  trajetsFlottantControl.addTo(carte);
  
  // --- Créer le panneau flottant pour les sites impactes (unique) ---
  L.Control.SitesImpactesFlottant = L.Control.extend({
    onAdd: function(map) {
      const container = L.DomUtil.create('div', 'control-sites-impactes');

      // En-tête
      const header = L.DomUtil.create('div', 'sites-impactes-header', container);
      header.innerHTML = 'Sites impactes';

      // Corps
      const body = L.DomUtil.create('div', 'sites-impactes-body', container);
      container._body = body;

      L.DomEvent.disableClickPropagation(container);
      L.DomEvent.disableScrollPropagation(container);

      return container;
    },
    onRemove: function(map) {}
  });

  L.control.sitesImpactesFlottant = function(opts) {
    return new L.Control.SitesImpactesFlottant(opts);
  };

  sitesImpactesFlottantControl = L.control.sitesImpactesFlottant({ position: 'topright' });
  sitesImpactesFlottantControl.addTo(carte);
  

  // --- Charger les données ---
  fetch("/api/carte")
    .then(res => {
      if (!res.ok) throw new Error('Erreur API: ' + res.status);
      return res.json();
    })
    .then(donnees => {
      console.log('Données reçues:', donnees);
      donneesGlobales = donnees;
      afficherDonneesSurCarte(donnees);
      configurerControlesVisibilite();
      mettreAJourTrajetsFlottant();
      mettreAJourSitesImpactesFlottant();
       if (document.getElementById('form-livreurs')) {
        assignationLivraison();
      }
    })
    .catch(err => {
      console.error('Erreur lors du chargement des données de la carte:', err);
      alert('Impossible de charger les données de la carte. Vérifiez la console pour plus de détails.');
    });
}

function normaliserTypeSite(type) {
  const rawType = (type || '').toString().toLowerCase();
  if (rawType === 'livraison' || rawType === 'depot') return 'depot';
  if (rawType === 'collecte' || rawType === 'pick-up') return 'collecte';
  if (rawType === 'entrepot' || rawType === 'warehouse') return 'entrepot';
  return 'default';
}

/* //! ----------------- RENDER DATOS EN MAPA ----------------- */

function afficherDonneesSurCarte(donnees) {
  if (!carte) {
    console.error('Carte no inicializada');
    return;
  }

  ensureSitePane();

  // 1) Tronçons (fondo)
  if (donnees.troncons && donnees.troncons.length > 0) {
    console.log('Affichage de', donnees.troncons.length, 'tronçons');
    donnees.troncons.forEach(troncon => {
      const depart = donnees.noeuds && donnees.noeuds.find(n => n.id === troncon.from);
      const arrivee = donnees.noeuds && donnees.noeuds.find(n => n.id === troncon.to);
      if (depart && arrivee) {
        const ligne = L.polyline(
          [[depart.lat, depart.lng], [arrivee.lat, arrivee.lng]],
          { color: '#667eea', weight: 3, opacity: 0.4, smoothFactor: 1 }
        ).addTo(carte);
        ligne.bindPopup(`<strong>Tronçon</strong><br>De: ${troncon.from}<br>À: ${troncon.to}`);
        tronconLines.push(ligne);
      }
    });
  }

  // 2) Nœuds (medio)
  if (donnees.noeuds && donnees.noeuds.length > 0) {
    console.log('Affichage de', donnees.noeuds.length, 'nœuds');
    donnees.noeuds.forEach(noeud => {
      const icone = L.divIcon({
        className: 'marqueur-personnalise',
        html: `<div class="marqueur-noeud" style="background:#16697A;width:10px;height:10px;border-radius:50%;border:2px solid white;"></div>`,
        iconSize: [10,10], iconAnchor: [5,5]
      });
      const marker = L.marker([noeud.lat, noeud.lng], { icon: icone, zIndexOffset: 100 }).addTo(carte);
      marker.bindPopup(`<strong>Nœud ${noeud.id}</strong><br>Lat: ${noeud.lat.toFixed(6)}<br>Lng: ${noeud.lng.toFixed(6)}`);
      noeudMarkers.push(marker);
    });
  }

// 3) Sites (encima)
  console.log('Sites reçus:', donnees.sites);
  if (donnees.sites && donnees.sites.length > 0) {
    // Limpiar markers previos
    siteMarkers.forEach(m => { try { carte.removeLayer(m); } catch (e) {} });
    siteMarkers.length = 0;
    
    const initialRadius = computeSiteRadius(carte);
    
    donnees.sites.forEach(site => {
      const rawType = (site.type || '').toString().toLowerCase();
      let normalizedType = rawType;
      if (rawType === 'livraison' || rawType === 'depot') normalizedType = 'depot';
      else if (rawType === 'collecte' || rawType === 'pick-up') normalizedType = 'collecte';
      else if (rawType === 'entrepot' || rawType === 'warehouse') normalizedType = 'entrepot';
      
      const color = COULEURS_SITES[normalizedType] || COULEURS_SITES['default'];
      
      if (site.lat != null && site.lng != null) {
        const marker = creerMarqueurSite(site, normalizedType, color, initialRadius);
        marker.options.numLivraison = site.numLivraison;
        marker.addTo(carte);
        siteMarkers.push(marker);
      }
    });

    // 5.sites impactes 
    console.log('Sites impactés:', donnees.sitesImpactes);
    if (donnees.sitesImpactes) {
      donnees.sitesImpactes.forEach( s => {
      sitesImpactes.push(s);
      });
      mettreAJourSitesImpactesFlottant();
    }

    // 4.trajets (si hay)
    console.log('Trajets reçus:', donnees.trajets);
    if (donnees.trajets) {
      //desactiver noeuds et troncons affichage
      visibilityState.troncons = false;
      visibilityState.noeuds = false;
      configurerControlesVisibilite();
      for (const key in donnees.trajets) {
        const color = getRandomHexColor();
        trajetLines[key] = [];
        donnees.trajets[key].forEach((trajet) => {

          const depart = donnees.noeuds && donnees.noeuds.find(n => n.id === trajet.from);
          const arrivee = donnees.noeuds && donnees.noeuds.find(n => n.id === trajet.to);
          if (depart && arrivee) {
            const ligne = L.polyline(
              [[depart.lat, depart.lng], [arrivee.lat, arrivee.lng]],
              { color: color, weight: 3, opacity: 0.8, smoothFactor: 1 }
            ).addTo(carte);
            
            // Ajouter le décorateur pour les flèches
            const decorator = L.polylineDecorator(ligne, {
              patterns: [
                {
                  offset: '50%', // Position de la flèche (milieu de la ligne)
                  repeat: 0, // Ne pas répéter la flèche
                  symbol: L.Symbol.arrowHead({
                    pixelSize: 15, // Taille de la flèche en pixels
                    polygon: false,
                    pathOptions: {
                      stroke: true,
                      color: color,
                      weight: 1
                    }
                  })
                }
              ]
            }).addTo(carte);

            ligne.bindPopup(`<strong>Trajet</strong><br>De: ${trajet.from}<br>À: ${trajet.to}`);
            trajetLines[key].push(ligne);
            trajetLines[key].push(decorator); 
          }
        })
      }
    }
    // resize al zoom (solo una vez)
    if (!carte._siteZoomHandlerAdded) {
      configurerZoomSites();
      carte._siteZoomHandlerAdded = true;
    }

    attachSiteHoverHandlers();
    updateVisibility();
  }

  try {
    if (donnees.noeuds && donnees.noeuds.length > 0) {
      const limites = L.latLngBounds(donnees.noeuds.map(n => [n.lat, n.lng]));
      carte.fitBounds(limites, { padding: [50, 50] });
    }
  } catch (e) {}
}

function attachSiteHoverHandlers() {
  if (!Array.isArray(siteMarkers) || !carte) return;

  siteMarkers.forEach(marker => {
    if (marker._siteHandlersAttached) return;
    marker._siteHandlersAttached = true;

    marker.on('click', () => {
      const numLivraison = marker.options.numLivraison;
      if (numLivraison == null) return;

      const jumeau = siteMarkers.find(m => m !== marker && m.options.numLivraison === numLivraison);
      if (!jumeau) return;

      const originalColor = jumeau.options.fillColor || '#3388ff';
      const originalWeight = jumeau.options.weight || 2;

      // Cambio instantáneo de color y grosor
      jumeau.setStyle({
        color: '#ff6600',
        fillColor: '#ff6600',
        weight: 4
      });

      // Regreso inmediato al estado original (sin animación ni delay largo)
      setTimeout(() => {
        jumeau.setStyle({
          color: '#ffffff',
          fillColor: originalColor,
          weight: originalWeight
        });
      }, 250);
    });
  });
}


function attachSiteHoverHandlers() {
  if (!Array.isArray(siteMarkers) || !carte) return;

  siteMarkers.forEach(marker => {
    if (marker._siteHandlersAttached) return;
    marker._siteHandlersAttached = true;

    marker.on('click', () => {
      const numLivraison = marker.options.numLivraison;
      if (numLivraison == null) return;

      const jumeau = siteMarkers.find(m => m !== marker && m.options.numLivraison === numLivraison);
      if (!jumeau) return;

      const originalColor = jumeau.options.fillColor || '#3388ff';
      const originalWeight = jumeau.options.weight || 2;

      // Cambio instantáneo de color y grosor
      jumeau.setStyle({
        color: '#ff6600',
        fillColor: '#ff6600',
        weight: 4
      });

      // Regreso inmediato al estado original (sin animación ni delay largo)
      setTimeout(() => {
        jumeau.setStyle({
          color: '#ffffff',
          fillColor: originalColor,
          weight: originalWeight
        });
      }, 250);
    });
  });
}


function configurerZoomSites() {
  carte.on('zoomend', () => {
    const newR = computeSiteRadius(carte);
    siteMarkers.forEach(m => {
      try {
        if (m && m.setRadius) m.setRadius(newR);
        const tip = m.getTooltip && m.getTooltip();
        if (tip) {
          const content = tip.getContent ? tip.getContent() : (m.options && m.options.siteId ? m.options.siteId : '');
          m.unbindTooltip();
          m.bindTooltip(content, { permanent: false, direction: 'top', offset: [0, -newR - 6] });
        }
      } catch (e) {}
    });
  });
}

function creerMarqueurSite(site, type, color, radius) {
  const marker = L.circleMarker([site.lat, site.lng], {
    radius: radius,
    fillOpacity: 1,
    color: '#ffffff',
    weight: 2,
    fillColor: color,
    pane: 'sitePane'
  });


  // actualizar tooltips/labels y radios al cambiar el zoom (se añade solo una vez)
  if (carte && !carte._siteLabelZoomHandlerAdded) {
    carte.on('zoomend', () => {
      const newR = computeSiteRadius(carte);
      siteMarkers.forEach(m => {
        try {
          if (m && m.setRadius) m.setRadius(newR);
          bindAppropriateTooltip(m);
        } catch (e) {}
      });
    });
    carte._siteLabelZoomHandlerAdded = true;
  }





  // label fijo debajo del círculo mostrando el "ordre de visite"
  const labelHtml = `<div style="
    display:inline-block;
    background:rgba(255,255,255,0.92);
    padding:2px 6px;
    border-radius:4px;
    border:1px solid rgba(0,0,0,0.08);
    font-size:12px;
    color:#222;
    box-shadow:0 1px 2px rgba(0,0,0,0.06);
    white-space:nowrap;
  ">${site.numPassage??''}</div>`;

  // label fijo debajo del círculo mostrando el "num livraison"
  const labelHtmlNum = `<div style="
    display:inline-block;
    background:rgba(255,255,255,0.92);
    padding:2px 6px;
    border-radius:4px;
    border:1px solid rgba(0,0,0,0.08);
    font-size:12px;
    color:#222;
    box-shadow:0 1px 2px rgba(0,0,0,0.06);
    white-space:nowrap;
  ">${site.numLivraison??''}</div>`;

  // Nota: si hay MUCHOS puntos, lo más efectivo es usar clustering (leaflet.markercluster)
  // y/o técnicas de gestión de etiquetas (labelgun, avoidance plugins) para evitar solapamientos.

  const hasArrival = !(site.arrivee == null || site.arrivee === '');
  let labelIcon;

  if (!hasArrival) {
    const _siteTypeLower = (site.type || '').toString().toLowerCase();
    if (_siteTypeLower !== 'entrepot') {
      labelIcon = L.divIcon({
        className: 'site-order-label',
        html: labelHtmlNum,
        iconSize: null,
        iconAnchor: [0, -radius - 8]
      });
    } else {
      labelIcon = L.divIcon({
        className: 'site-order-label-hidden',
        html: '',
        iconSize: [0, 0],
        iconAnchor: [0, 0]
      });
    } 
  }
  else {
    const _siteTypeLower = (site.type || '').toString().toLowerCase();
    if (_siteTypeLower !== 'entrepot') {
      labelIcon = L.divIcon({
        className: 'site-order-label',
        html: labelHtml,
        iconSize: null,
        iconAnchor: [0, -radius - 8]
      });
    } else {
      labelIcon = L.divIcon({
        className: 'site-order-label-hidden',
        html: '',
        iconSize: [0, 0],
        iconAnchor: [0, 0]
      });
    }
  }

  const labelMarker = L.marker([site.lat, site.lng], {
    icon: labelIcon,
    interactive: false,
    pane: 'sitePane',
    zIndexOffset: 999
  });

  // attach label to the circle marker so we can manage it together
  marker._orderLabel = labelMarker;

  // when the circle is added/removed from the map, add/remove the label as well
  marker.on('add', () => { try { if (carte && !carte.hasLayer(labelMarker)) carte.addLayer(labelMarker); } catch (e) {} });
  marker.on('remove', () => { try { if (carte && carte.hasLayer(labelMarker)) carte.removeLayer(labelMarker); } catch (e) {} });

  // if the circle is already on the map, ensure the label is added immediately
  try { if (carte && carte.hasLayer(marker) && !carte.hasLayer(labelMarker)) carte.addLayer(labelMarker); } catch (e) {}

  marker.options.siteType = type;
  marker.options.siteId = site.id;
  if(site.type === 'entrepot'){
    if(site.heures.length == 0){
      marker.bindTooltip(`${site.id}`, { permanent: false, direction: 'top', offset: [0, -radius - 6] });
      var html = `<strong style="color:${color}">${type} ${site.id}</strong>
        <br>Heure de départ: 08:00`
      marker.bindPopup(html);
    }
    else{
      marker.bindTooltip(`${site.id}`, { permanent: false, direction: 'top', offset: [0, -radius - 6] });
      var html = `<strong style="color:${color}">${type} ${site.id}</strong>
        <br>Heure de départ: 08:00
        <br>Heures arrivées : `
      var i = 1;
      site.heures.forEach(function (heure){
        console.log(heure);
        html += `<br>-Trajet ${i} : ${heure}`;
        i+=1;
      });
      marker.bindPopup(html);
    }
  }

  else if ((site.arrivee == null || site.arrivee === '')) {
  
    marker.bindTooltip(`${site.id}`, { permanent: false, direction: 'top', offset: [0, -radius - 6] });
    marker.bindPopup(`<strong style="color:${color}">${type} ${site.id}</strong>
      <br>Heure d'arrivée: Pas encore calculée
      <br>Heure de départ: Pas encore calculée
      `);
  } 
  else  {
    marker.bindTooltip(`${site.id}`, { permanent: false, direction: 'top', offset: [0, -radius - 6] });
    marker.bindPopup(`<strong style="color:${color}">${type} ${site.id}</strong>
      <br>Heure d'arrivée: ${site.arrivee}
      <br>Heure de départ: ${site.depart} 
      <div class="delete-control">
        <button id="delete-btn" onclick="deleteSite(${site.id}, '${type}', ${site.numLivraison})" title="Supprimer">
          <img src="/images/delete.svg" style="color:#e74c3c;width:18px;height:18px;"></img>
        </button>
      </div>
      `);
  }
  
  marker.on('click', () => {
    try {
      if (marker.openPopup) marker.openPopup();
    } catch (e) {}
  });

  marker.on('mouseover', () => { try { if (marker.openTooltip) marker.openTooltip(); } catch (e) {} });
  marker.on('mouseout', () => { try { if (marker.closeTooltip) marker.closeTooltip(); } catch (e) {} });

  return marker;
}

/* //! ----------------- PANE / HOVER / DIM ----------------- */
function getRandomHexColor() {
  return `#${Math.floor(Math.random() * 16777215).toString(16).padStart(6, '0')}`;
}

function ensureSitePane() {
  if (!carte) return;
  if (!carte.getPane('sitePane')) {
    const p = carte.createPane('sitePane');
    p.style.zIndex = 750;
    p.style.pointerEvents = 'auto';
  }
}

function dimExcept(activeMarker) {
  tronconLines.forEach(l => { try { if (l.setStyle) l.setStyle({ opacity: 0.12 }); } catch (e) {} });
  noeudMarkers.forEach(m => { try { if (m.setOpacity) m.setOpacity(0.2); } catch (e) {} });
  siteMarkers.forEach(s => {
    try {
      if (s === activeMarker) {
        if (s.setStyle) s.setStyle({ fillOpacity: 1, opacity: 1 });
      } else {
        if (s.setStyle) s.setStyle({ fillOpacity: 0.35, opacity: 0.4 });
      }
    } catch (e) {}
  });
  const mapEl = document.querySelector('.leaflet-container');
  if (mapEl) mapEl.classList.add('map-dim');
}

function undimAll() {
  tronconLines.forEach(l => { try { if (l.setStyle) l.setStyle({ opacity: 0.4 }); } catch (e) {} });
  noeudMarkers.forEach(m => { try { if (m.setOpacity) m.setOpacity(1.0); } catch (e) {} });
  siteMarkers.forEach(s => { try { if (s.setStyle) s.setStyle({ fillOpacity: 1, opacity: 1 }); } catch (e) {} });
  const mapEl = document.querySelector('.leaflet-container');
  if (mapEl) mapEl.classList.remove('map-dim');
}

/* //! ----------------- VISIBILITY CONTROLS ----------------- */
function updateVisibility() {
  // FR: Met à jour la visibilité des marqueurs selon visibilityState
  siteMarkers.forEach(marker => {
    const type = marker.options.siteType || 'default';

    // FR: Faire correspondre les types normalisés attendus (incluant 'depot')
    const key = (type === 'entrepot') ? 'entrepot'
              : (type === 'collecte') ? 'collecte'
              : (type === 'depot') ? 'depot'
              : 'default';

    if (visibilityState[key]) {
      if (!carte.hasLayer(marker)) marker.addTo(carte);
    } else {
      if (carte.hasLayer(marker)) carte.removeLayer(marker);
    }
  });

  // FR: Gestion de la visibilité des nœuds
  noeudMarkers.forEach(marker => {
    if (visibilityState.noeuds) {
      if (!carte.hasLayer(marker)) marker.addTo(carte);
    } else {
      if (carte.hasLayer(marker)) carte.removeLayer(marker);
    }
  });

  // FR: Gestion de la visibilité des tronçons
  tronconLines.forEach(line => {
    if (visibilityState.troncons) {
      if (!carte.hasLayer(line)) line.addTo(carte);
    } else {
      if (carte.hasLayer(line)) carte.removeLayer(line);
    }
  });

  // FR: Gestion de la visibilité des trajets
  for (const key in trajetLines) {
    const trajet = trajetLines[key]; 
    trajet.forEach((t) => {
      if (visibilityState.trajets) {
        if (!carte.hasLayer(t)) t.addTo(carte);
      } else {
        if (carte.hasLayer(t)) carte.removeLayer(t);
      }
    })
  };
}

function configurerControlesVisibilite() {
  // Synchroniser les checkboxes principales
  ['entrepot','collecte','depot','noeuds','troncons','trajets'].forEach(type => {
    const cb = document.getElementById(`toggle-${type}`);
    if (cb) {
      cb.checked = visibilityState[type];
      cb.addEventListener('change', e => {
        visibilityState[type] = e.target.checked;
        updateVisibility();
        // Si c'est le toggle trajets, mettre à jour le panneau flottant
        if (type === 'trajets') {
          mettreAJourTrajetsFlottant();
        }
      });
    }
  });

  // Mettre à jour le contenu du panneau flottant
  mettreAJourTrajetsFlottant();

  // Bouton pour recentrer la vue
  const btnReset = document.getElementById('btn-reset-view');
  if (btnReset) {
    btnReset.addEventListener('click', () => {
      if (!donneesGlobales || (!donneesGlobales.noeuds && !donneesGlobales.sites)) return;
      const pts = [];
      if (donneesGlobales.noeuds) pts.push(...donneesGlobales.noeuds.map(n => [n.lat, n.lng]));
      if (donneesGlobales.sites) pts.push(...donneesGlobales.sites.filter(s => s.lat && s.lng).map(s => [s.lat, s.lng]));
      if (pts.length) {
        const bounds = L.latLngBounds(pts);
        carte.fitBounds(bounds, { padding: [40,40] });
      }
    });
  }

  // Bouton bascule « tout afficher / tout cacher »
  const btnToggleAll = document.getElementById('btn-toggle-all');
  if (btnToggleAll) {
    btnToggleAll.addEventListener('click', () => {
      const all = ['entrepot','collecte','depot','noeuds','troncons','trajets'].every(k => visibilityState[k] === true);
      const newState = !all;
      Object.keys(visibilityState).forEach(k => visibilityState[k] = newState);
      ['entrepot','collecte','depot','noeuds','troncons','trajets'].forEach(k => {
        const el = document.getElementById(`toggle-${k}`);
        if (el) el.checked = visibilityState[k];
      });
      updateVisibility();
      mettreAJourTrajetsFlottant();
    });
  }
}

// Fonction auxiliaire pour mettre à jour le panneau flottant des trajets
function mettreAJourSitesImpactesFlottant() {
  if (!sitesImpactesFlottantControl) return;
  const container = sitesImpactesFlottantControl.getContainer();
  if (!container) return;

  let body = container.querySelector('.sites-impactes-body');
  if (!body) {
    body = L.DomUtil.create('div', 'sites-impactes-body', container);
  }
  body.innerHTML = '';

  if (!sitesImpactes || Object.keys(sitesImpactes).length === 0) {
    const empty = L.DomUtil.create('div', 'empty-body', body);
    empty.innerHTML = ' Aucun sites impactes. ';
    return;
  }

  const oldList = container.querySelector('.sites-impactes-list');  
  if (oldList) oldList.remove();

  const listContainer = L.DomUtil.create('ul', 'sites-impactes-list', body);
  Object.keys(sitesImpactes).forEach( (id) => {
    const s = sitesImpactes[id];
    const sign = s.delay > 0 ? '+' : '';
    const item = L.DomUtil.create('li', '', listContainer);
    item.innerHTML = `Site n°${s.id} : ${sign}${s.delay} min`;
  });

}

// Fonction auxiliaire pour mettre à jour le panneau flottant des trajets
function mettreAJourTrajetsFlottant() {
  if (!trajetsFlottantControl) return;
  const container = trajetsFlottantControl.getContainer();
  if (!container) return;

  let body = container.querySelector('.trajets-body');
  if (!body) {
    body = L.DomUtil.create('div', 'trajets-body', container);
  }
  body.innerHTML = '';

  if (!trajetLines || Object.keys(trajetLines).length === 0) {
    const empty = L.DomUtil.create('div', 'empty-body', body);
    empty.innerHTML = ' Aucun trajet disponible. ';
    return;
  }

  const controlContainer = L.DomUtil.create('div', 'trajets-list', body);
  Object.keys(trajetLines).forEach((key, i) => {
    const trajet = trajetLines[key];
    const item = L.DomUtil.create('div', 'control-row trajet-item', controlContainer);
    const label = L.DomUtil.create('label', '', item);
    label.setAttribute('for', `trajet-${i}`);

    const checkbox = L.DomUtil.create('input', '', label);
    checkbox.type = 'checkbox';
    checkbox.id = `trajet-${i}`;
    checkbox.checked = true;
    checkbox.disabled = !visibilityState.trajets;

    label.appendChild(document.createTextNode(` Trajet ${parseInt(key,10)+1}`));

    checkbox.addEventListener('change', e => {
      trajet.forEach(t => {
        if (e.target.checked) t.addTo(carte);
        else carte.removeLayer(t);
      });
    });
  });
}

/**
 * Lance le calcul
 */
function lancerCalcul() {
   console.log('Calcul lancé');
    // Déterminer l'endpoint selon le type
    var endpoint = '/api/calcul';
    var statusId = '#status-calcul';
    
    console.log('📤 Début du calcul:', endpoint);
    
    // Afficher l'état de chargement
    $(statusId).removeClass('success error').addClass('loading')
        .text('⏳ Chargement en cours...').show();
    
    // Lire le fichier comme ArrayBuffer
    var reader = new FileReader();
  
        // Envoyer directement l'ArrayBuffer
        fetch(endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/xml',
            },
        })
        .then(response => {
            console.log('📥 Réponse du serveur:', response.status);
            if (!response.ok) {
                throw new Error('Erreur serveur: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            console.log('✅ Calcul effectué', data);
            
            $(statusId).removeClass('loading error').addClass('success')
                .text('✅ Tournée chargée avec succès!');
            
            // Notifier le contrôleur du succès
            if (window.appController) {
                try {
                      window.appController.onLivraisonCalculated();
                      // Afficher message et proposer d'aller à la carte
                      // setTimeout(() => {
                      //     if (confirm('✅ Livraison Calculé! Voulez-vous voir la carte?')) {
                      //         $('#btn-map').trigger('click');
                      //     }
                      // }, 500);
                      $('#btn-map').trigger('click');
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

function lancerTelechargement() {  
  console.log("Téléchargement roadmap");
  var trajetsAffiches = getTrajetAffiches();
  var params = "?"
  if (trajetsAffiches.length === 0) return;
  if (trajetsAffiches.length === 1) {
    params += `file=${trajetsAffiches}.txt`;
  } 
  else {
    params += "files=";
    var firstT = true;
    trajetsAffiches.forEach(t => {
      params += t + ".txt";
      if (firstT) {
        params += ',';
        firstT = false;
      }
    });
  } 

  const endpoint = `/api/roadmap${params}`; 
  fetch(endpoint)
        .then(res => {
            if (!res.ok) throw new Error("Erreur serveur");
            // Récupérer le nom du fichier depuis les headers
            const disposition = res.headers.get("Content-Disposition");
            let filename = "roadmap"; // valeur par défaut
            
            if (disposition && disposition.includes("filename=")) {
              filename = disposition.split("filename=")[1].replace(/["']/g, "");
            } else {
              // Si pas de nom dans les headers, deviner le type à partir du mime
              const contentType = res.headers.get("Content-Type");
              if (contentType === "application/zip") filename += ".zip";
              else if (contentType === "text/plain") filename += ".txt";
              else filename += ".dat"; // fallback
            }

            return res.blob().then(blob => ({ blob, filename }));
        })
        .then(blob => {
            const url = URL.createObjectURL(blob.blob);
            const a = document.createElement("a");
            a.href = url;
            //a.download = `Bobard_Bobert.txt`; // nom du fichier
            a.download = blob.filename;
            a.click();
            a.remove();
            URL.revokeObjectURL(url); // nettoyer
        })
        .catch(err => {
            console.error("❌ Erreur téléchargement:", err);
            alert("Erreur lors du téléchargement: " + err.message);
        });

}

function getTrajetAffiches() {
  var lTrajAff = new Array();
  var cond = document.getElementById('toggle-trajets').checked;
  document.querySelectorAll('[id^="trajet-"]').forEach(input => {
    if (cond && input.checked) lTrajAff.push(input.id);
  });

  return lTrajAff ;
}

function deleteSite(idSite, typeSite, numLivraison) {
  fetch('api/deleteSite', {
    method: 'POST' ,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify( {"idSite":idSite, "typeSite": typeSite, "numLivraison": numLivraison} )
  })
    .then(res => res.json())
    .then(initialiserCarte())
    .catch(err => console.error(err));
}

function undoAction() {
  fetch('/api/undoAction', { 
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    }
  })
    .then(response => {
      if (!response.ok) {
        return response.json().then(err => {
          throw new Error(err.error || 'Erreur serveur');
        });
      }
      return response.json();
    })
    .then(() => {
      console.log('Undo réussi');
      initialiserCarte();
    })
    .catch(err => {
      console.error('Erreur lors du undo:', err);
      alert('Impossible d\'annuler l\'action : ' + err.message);
    });
}

function redoAction() {
  fetch('/api/redoAction', { 
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    }
  })
    .then(response => {
      if (!response.ok) {
        return response.json().then(err => {
          throw new Error(err.error || 'Erreur serveur');
        });
      }
      return response.json();
    })
    .then(() => {
      console.log('Redo réussi');
      initialiserCarte();
    })
    .catch(err => {
      console.error('Erreur lors du redo:', err);
      alert('Impossible d\'annuler l\'action : ' + err.message);
    });
}

function nettoyerCarte() {
  if (carte !== null) {
    try { carte.off(); carte.remove(); } catch (e) { console.warn('Erreur nettoyage carte:', e); }
  }
  marqueurs = [];
  lignes = [];
  siteMarkers = [];
  noeudMarkers = [];
  tronconLines = [];
  trajetLines = {};
  sitesImpactes = [];
  donneesGlobales = null;
}

function attachDropHandlers() {
  $('.file-area').each(function() {
  $(this).data('dragCounter', 0);
});

function clearAllDragStates() {
  $('.file-area').removeClass('dragover drag-disabled');
  $('.file-area').each(function(){ $(this).data('dragCounter', 0); });
}

$(document).on('dragenter', function(e) {
  e.preventDefault();
});

$(document).on('dragover', function(e) {
  e.preventDefault();
  var x = e.originalEvent.clientX;
  var y = e.originalEvent.clientY;
  if (x === 0 && y === 0) return;

  var el = document.elementFromPoint(x, y);
  var $target = $(el).closest('.file-area');

  if (!$target || $target.length === 0) {
    clearAllDragStates();
    return;
  }

  $('.file-area').not($target).removeClass('dragover').each(function(){ 
    if ($(this).hasClass('disabled')) {
      $(this).addClass('drag-disabled');
    } else {
      $(this).removeClass('drag-disabled');
    }
  });

  if ($target.hasClass('disabled')) {
    $target.removeClass('dragover').addClass('drag-disabled');
  } else {
    $target.removeClass('drag-disabled').addClass('dragover');
  }
});

$(document).on('dragenter', function(e) {
  e.preventDefault();
  var x = e.originalEvent.clientX;
  var y = e.originalEvent.clientY;
  var el = document.elementFromPoint(x, y);
  var $a = $(el).closest('.file-area');
  if ($a && $a.length) {
    var c = $a.data('dragCounter') || 0;
    $a.data('dragCounter', c + 1);
  }
});

$(document).on('dragleave', function(e) {
  e.preventDefault();
  if (e.originalEvent.clientX <= 0 && e.originalEvent.clientY <= 0) {
    clearAllDragStates();
    return;
  }
  var x = e.originalEvent.clientX;
  var y = e.originalEvent.clientY;
  var el = document.elementFromPoint(x, y);
  var $a = $(el).closest('.file-area');

  $('.file-area').each(function() {
    var c = $(this).data('dragCounter') || 0;
    if (c > 0) {
      $(this).data('dragCounter', c - 1);
      if (c - 1 <= 0) {
        $(this).removeClass('dragover drag-disabled');
      }
    }
  });
});

$(document).on('drop', function(e) {
  e.preventDefault();
  var x = e.originalEvent.clientX;
  var y = e.originalEvent.clientY;
  var el = document.elementFromPoint(x, y);
  var $target = $(el).closest('.file-area');

  clearAllDragStates();

  if (!$target || $target.length === 0) return;

  if ($target.hasClass('disabled')) {
    alert("⚠️ Veuillez d'abord charger un plan de distribution !");
    return;
  }

  var files = e.originalEvent.dataTransfer.files;
  if (files && files.length > 0) {
    var $input = $target.find('input[type=file]');
    if ($input && $input.length) {
      try {
        const dt = new DataTransfer();
        for (let i = 0; i < files.length; i++) dt.items.add(files[i]);
        $input[0].files = dt.files;
      } catch (err) {
        $input[0].files = files;
      }
      $input.trigger('change');
    }
  }
});
}

/* //! ----------------- CARGA SIDEBAR + INICIO ----------------- */

fetch('/components/Sidebar.html')
  .then(res => {
    if (!res.ok) throw new Error('Erreur lors du chargement du sidebar');
    return res.text();
  })
  .then(html => {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) sidebar.innerHTML = html;
        
    // if (typeof updateUIBasedOnState === 'function') {
    //   console.log("APPEL DANS LE TYPEOF")
    //   setTimeout(updateUIBasedOnState, 50);
    // }

    document.getElementById('btn-map')?.addEventListener('click', () => {
      document.querySelectorAll('.sidebar-nav').forEach(b => b.classList.remove('active'));
      document.getElementById('btn-map')?.classList.add('active');
      // document.getElementById('btn-calcul')?.classList.add('active');
      // chargerComposantPrincipal('/components/Map.html');
    });
    
    document.getElementById('btn-filtros')?.addEventListener('click', () => {
      
      document.querySelectorAll('.sidebar-nav').forEach(b => b.classList.remove('active'));
      document.getElementById('btn-filtros')?.classList.add('active');
      // document.getElementById('btn-calcul')?.classList.add('active');
      chargerComposantPrincipal('/components/Import.html');
    });
    
    document.getElementById('btn-estadisticas')?.addEventListener('click', () => {
      document.querySelectorAll('.sidebar-nav').forEach(b => b.classList.remove('active'));
      document.getElementById('btn-estadisticas')?.classList.add('active');
      // document.getElementById('btn-calcul')?.classList.add('active');
      document.getElementById('main-content').innerHTML = `<div style="padding:2rem;"><h2>Statistiques</h2><p>Fonctionnalité en construction…</p></div>`;
    });

    // document.getElementById('btn-calcul')?.addEventListener('click', () => {
    //   lancerCalcul();
    //   chargerComposantPrincipal('/components/Map.html');
    // });

    document.getElementById('btn-roadmap')?.addEventListener('click', () => {
      lancerTelechargement();
    });

    document.getElementById('btn-roadmap')?.addEventListener('click', () => {
      lancerTelechargement();
    });
    chargerComposantPrincipal('/components/Map.html');
    attachDropHandlers();

  })
  .catch(err => {
    console.error("Erreur lors du chargement du sidebar:", err);
    const sidebar = document.getElementById('sidebar');
    if (sidebar) sidebar.innerHTML = '<p style="color:#e74c3c;">Erreur de chargement</p>';
    chargerComposantPrincipal('/components/Map.html');
  });

  window.addEventListener("load", () => {
    fetch("/api/resetCarte", { method: "POST" })
      .then(() => initialiserCarte());
});