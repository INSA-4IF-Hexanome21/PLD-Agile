// Variables globales
let carte = null;
let marqueurs = [];
let lignes = [];
let siteMarkers = [];
let noeudMarkers = [];
let tronconLines = [];
let trajetLines = [];
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
  troncons: true
};

/* //! ----------------- UTILIDADES / INIT ----------------- */

function chargerComposantPrincipal(url) {
  console.log('Chargement du composant:', url);
  const main = document.getElementById('main-content');
  if (!main) return;
  main.innerHTML = '<p>Chargement en cours...</p>';

  fetch(url)
    .then(res => {
      if (!res.ok) throw new Error('Erreur de chargement: ' + url);
      return res.text();
    })
    .then(html => {
      main.innerHTML = html;
      if (url.includes('Map.html')) {
        setTimeout(initialiserCarte, 100);
      }
      // Si c'est Import.html, mettre à jour l'UI selon l'état
      if (url.includes('Import.html') && window.appController) {
        setTimeout(updateUIBasedOnState, 100);
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
    document.getElementById('main-content').innerHTML = '<p style="color: #e74c3c;">Élément #map introuvable</p>';
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

    // 4.trajets (si hay)
    console.log('Trajets reçus:', donnees.trajets);
    if (donnees.trajets && donnees.trajets.length > 0) {
      donnees.trajets.forEach((trajet) => {
        const depart = donnees.noeuds && donnees.noeuds.find(n => n.id === trajet.from);
        const arrivee = donnees.noeuds && donnees.noeuds.find(n => n.id === trajet.to);
        if (depart && arrivee) {
          const ligne = L.polyline(
            [[depart.lat, depart.lng], [arrivee.lat, arrivee.lng]],
            { color: '#3ce861ff', weight: 3, opacity: 0.8, smoothFactor: 1 }
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
                    color: '#268b3cff',
                    weight: 1
                  }
                })
              }
            ]
          }).addTo(carte);

          ligne.bindPopup(`<strong>Trajet</strong><br>De: ${trajet.from}<br>À: ${trajet.to}`);
          trajetLines.push(ligne);
          trajetLines.push(decorator); 
        }
      })
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

  // Nota: si hay MUCHOS puntos, lo más efectivo es usar clustering (leaflet.markercluster)
  // y/o técnicas de gestión de etiquetas (labelgun, avoidance plugins) para evitar solapamientos.

  const _siteTypeLower = (site.type || '').toString().toLowerCase();
  let labelIcon;
  if (_siteTypeLower !== 'entrepot') {
    labelIcon = L.divIcon({
      className: 'site-order-label',
      html: labelHtml,
      iconSize: null,
      // iconAnchor Y negative to shift the label downward relative to the marker point
      iconAnchor: [0, -radius - 8]
    });
  } else {
    // no visible label for entrepot: use an empty/invisible divIcon so we don't render anything
    labelIcon = L.divIcon({
      className: 'site-order-label-hidden',
      html: '',
      iconSize: [0, 0],
      iconAnchor: [0, 0]
    });
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

  if (site.type === 'entrepot') {

    marker.bindTooltip(`${site.id}`, { permanent: false, direction: 'top', offset: [0, -radius - 6] });
    marker.bindPopup(`<strong style="color:${color}">${type} ${site.id}</strong>
      <br>Heure d'arrivée: ${site.arrivee}
      <br>Heure de départ: 8:00
      `);
    
  } else  {
    marker.bindTooltip(`${site.id}`, { permanent: false, direction: 'top', offset: [0, -radius - 6] });
    marker.bindPopup(`<strong style="color:${color}">${type} ${site.id}</strong>
      <br>Heure d'arrivée: ${site.arrivee}
      <br>Heure de départ: ${site.depart} 
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
}

function configurerControlesVisibilite() {
  // FR: On attend des checkboxes avec ids toggle-entrepot, toggle-collecte, toggle-depot
  ['entrepot', 'collecte', 'depot'].forEach(type => {
    const cb = document.getElementById(`toggle-${type}`);
    if (cb) {
      cb.checked = visibilityState[type];
      cb.addEventListener('change', (e) => {
        visibilityState[type] = e.target.checked;
        updateVisibility();
      });
    }
  });

  // FR: Toggle pour les nœuds
  const tNoeuds = document.getElementById('toggle-noeuds');
  if (tNoeuds) {
    tNoeuds.checked = visibilityState.noeuds;
    tNoeuds.addEventListener('change', (e) => {
      visibilityState.noeuds = e.target.checked;
      updateVisibility();
    });
  }

  // FR: Toggle pour les tronçons
  const tTron = document.getElementById('toggle-troncons');
  if (tTron) {
    tTron.checked = visibilityState.troncons;
    tTron.addEventListener('change', (e) => {
      visibilityState.troncons = e.target.checked;
      updateVisibility();
    });
  }

  // FR: Bouton pour recentrer la vue (utilise donneesGlobales)
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

  // FR: Bouton bascule « tout afficher / tout cacher »
  const btnToggleAll = document.getElementById('btn-toggle-all');
  if (btnToggleAll) {
    btnToggleAll.addEventListener('click', () => {
      const all = ['entrepot','collecte','depot','noeuds','troncons'].every(k => visibilityState[k] === true);
      const newState = !all;
      Object.keys(visibilityState).forEach(k => visibilityState[k] = newState);
      // FR: mettre à jour l'état visuel des checkboxes si elles existent
      ['entrepot','collecte','depot','noeuds','troncons'].forEach(k => {
        const el = document.getElementById(`toggle-${k}`);
        if (el) el.checked = visibilityState[k];
      });
      updateVisibility();
    });
  }
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
                .text('✅ ' + file.name + ' chargé avec succès!');
            
            // Notifier le contrôleur du succès
            if (window.appController) {
                try {
                      window.appController.onLivraisonCalculated();
                      // Afficher message et proposer d'aller à la carte
                      setTimeout(() => {
                          if (confirm('✅ Livraison Calculé! Voulez-vous voir la carte?')) {
                              $('#btn-mapa').trigger('click');
                          }
                      }, 500);
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

function nettoyerCarte() {
  if (carte !== null) {
    try { carte.off(); carte.remove(); } catch (e) { console.warn('Erreur nettoyage carte:', e); }
  }
  marqueurs = [];
  lignes = [];
  siteMarkers = [];
  noeudMarkers = [];
  tronconLines = [];
  donneesGlobales = null;
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
    
    document.getElementById('btn-mapa')?.addEventListener('click', () => {
      document.querySelectorAll('.sidebar-nav').forEach(b => b.classList.remove('active'));
      document.getElementById('btn-mapa')?.classList.add('active');
      document.getElementById('btn-calcul')?.classList.add('active');
      chargerComposantPrincipal('/components/Map.html');
    });
    
    document.getElementById('btn-filtros')?.addEventListener('click', () => {
      document.querySelectorAll('.sidebar-nav').forEach(b => b.classList.remove('active'));
      document.getElementById('btn-filtros')?.classList.add('active');
      document.getElementById('btn-calcul')?.classList.add('active');
      chargerComposantPrincipal('/components/Import.html');
    });
    
    document.getElementById('btn-estadisticas')?.addEventListener('click', () => {
      document.querySelectorAll('.sidebar-nav').forEach(b => b.classList.remove('active'));
      document.getElementById('btn-estadisticas')?.classList.add('active');
      document.getElementById('btn-calcul')?.classList.add('active');
      document.getElementById('main-content').innerHTML = `<div style="padding:2rem;"><h2>Statistiques</h2><p>Fonctionnalité en construction…</p></div>`;
    });

    document.getElementById('btn-calcul')?.addEventListener('click', () => {
      lancerCalcul();
      chargerComposantPrincipal('/components/Map.html');
    });

    chargerComposantPrincipal('/components/Map.html');
  })
  .catch(err => {
    console.error("Erreur lors du chargement du sidebar:", err);
    const sidebar = document.getElementById('sidebar');
    if (sidebar) sidebar.innerHTML = '<p style="color:#e74c3c;">Erreur de chargement</p>';
    chargerComposantPrincipal('/components/Map.html');
  });