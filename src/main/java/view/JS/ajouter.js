// --- ajouter.js ---
// Gestion de l'ajout interactif de livraisons

let modeAjoutActif = false;
let etapeAjout = 0;
let selectionData = {
  collecteNoeud: null,
  collecteSitePrecedent: null,
  depotNoeud: null,
  depotSitePrecedent: null
};

// Overlay d'instructions sur la carte
let instructionOverlay = null;
let highlightedMarkers = []; // Pour trackear markers resaltados

const ETAPES = [
  { 
    numero: 1, 
    texte: "Sélectionnez un nœud pour ajouter une collecte", 
    type: 'noeud',
    cible: 'collecteNoeud',
    icon: '📍'
  },
  { 
    numero: 2, 
    texte: "Sélectionnez le site qui précède la nouvelle collecte", 
    type: 'site',
    cible: 'collecteSitePrecedent',
    icon: '🟢'
  },
  { 
    numero: 3, 
    texte: "Sélectionnez un nœud pour ajouter un dépôt", 
    type: 'noeud',
    cible: 'depotNoeud',
    icon: '📍'
  },
  { 
    numero: 4, 
    texte: "Sélectionnez le site qui précède le nouveau dépôt", 
    type: 'site',
    cible: 'depotSitePrecedent',
    icon: '🔴'
  }
];

/**
 * Crée l'overlay d'instructions sur la carte
 */
function creerInstructionOverlay() {
  if (instructionOverlay) return;
  
  instructionOverlay = L.control({ position: 'topleft' });
  
  instructionOverlay.onAdd = function() {
    const div = L.DomUtil.create('div', 'instruction-overlay');
    div.innerHTML = `
      <div class="instruction-header">
        <strong>✍🏻 Mode Ajout Livraison</strong>
        <button id="annuler-ajout" class="btn-cancel">✖</button>
      </div>
      <div class="instruction-body">
        <div class="etape-numero">
          <span id="etape-icon">📍</span>
          Étape <span id="etape-num">1</span>/4
        </div>
        <p id="instruction-texte">Sélectionnez un nœud pour ajouter une collecte</p>
        <div class="progress-bar">
          <div id="progress-fill" class="progress-fill" style="width: 25%"></div>
        </div>
      </div>
      <div class="selections-recap">
        <div id="recap-content"><small>Aucune sélection</small></div>
      </div>
    `;
    
    L.DomEvent.disableClickPropagation(div);
    L.DomEvent.disableScrollPropagation(div);
    
    return div;
  };
  
  instructionOverlay.addTo(carte);
  
  // Attacher l'événement d'annulation
  setTimeout(() => {
    const btnCancel = document.getElementById('annuler-ajout');
    if (btnCancel) {
      btnCancel.addEventListener('click', annulerAjout);
      console.log('✅ Bouton annuler attaché');
    }
  }, 100);
}

/**
 * Met à jour l'overlay avec l'étape actuelle
 */
function mettreAJourInstructions() {
  if (!instructionOverlay) return;
  
  const etape = ETAPES[etapeAjout];
  const numEl = document.getElementById('etape-num');
  const texteEl = document.getElementById('instruction-texte');
  const iconEl = document.getElementById('etape-icon');
  const progressEl = document.getElementById('progress-fill');
  const recapEl = document.getElementById('recap-content');
  
  if (numEl) numEl.textContent = etape.numero;
  if (texteEl) texteEl.textContent = etape.texte;
  if (iconEl) iconEl.textContent = etape.icon;
  if (progressEl) progressEl.style.width = (((etapeAjout + 1) / 4) * 100) + '%';
  
  // Mise à jour du récapitulatif
  if (recapEl) {
    let html = '<small><strong>Sélections:</strong></small><ul class="recap-list">';
    if (selectionData.collecteNoeud) 
      html += `<li>✅ Nœud collecte: <code>${selectionData.collecteNoeud.options.siteId || 'N/A'}</code></li>`;
    if (selectionData.collecteSitePrecedent) 
      html += `<li>✅ Site précédent collecte: <code>${selectionData.collecteSitePrecedent.options.siteId || 'N/A'}</code></li>`;
    if (selectionData.depotNoeud) 
      html += `<li>✅ Nœud dépôt: <code>${selectionData.depotNoeud.options.siteId || 'N/A'}</code></li>`;
    if (selectionData.depotSitePrecedent) 
      html += `<li>✅ Site précédent dépôt: <code>${selectionData.depotSitePrecedent.options.siteId || 'N/A'}</code></li>`;
    html += '</ul>';
    recapEl.innerHTML = html;
  }
}


function highlightAvailableMarkers() {
  clearHighlights();
  
  const etape = ETAPES[etapeAjout];
  const targetMarkers = etape.type === 'noeud' ? noeudMarkers : siteMarkers;
  
  if (!Array.isArray(targetMarkers)) return;
  
  console.log(`🎨 Highlighting ${targetMarkers.length} ${etape.type}s`);
  
  targetMarkers.forEach(marker => {
    if (etape.type === 'noeud') {
      // NO cambiar el icono, solo modificar el estilo del elemento existente
      try {
        const iconElement = marker.getElement();
        if (iconElement) {
          const noeudDiv = iconElement.querySelector('.marqueur-noeud');
          if (noeudDiv) {
            // Guardar estilo original
            marker._originalStyle = {
              background: noeudDiv.style.background,
              boxShadow: noeudDiv.style.boxShadow,
              width: noeudDiv.style.width,
              height: noeudDiv.style.height
            };
            
            // Aplicar highlight sin cambiar tamaño
            noeudDiv.style.background = '#FFA62B';
            noeudDiv.style.boxShadow = '0 0 15px #FFA62B, 0 0 25px #FFA62B';
            noeudDiv.classList.add('marqueur-noeud-highlight');
            
            // Asegurar que sea clickeable
            marker.options.interactive = true;
            marker.options.bubblingMouseEvents = false;
            
            // Reattach handlers
            marker.off('click');
            marker.off('mousedown');
            marker.on('click', function(e) {
              L.DomEvent.stopPropagation(e);
              L.DomEvent.preventDefault(e);
              console.log('🟡 Nœud HIGHLIGHT clicked:', marker.options.siteId);
              if (modeAjoutActif) {
                gererClicMarqueur(marker, 'noeud');
              }
            });
            marker.on('mousedown', function(e) {
              L.DomEvent.stopPropagation(e);
              L.DomEvent.preventDefault(e);
            });
          }
        }
      } catch (e) {
        console.warn('Erreur highlight noeud:', e);
      }
    } else {
      // Highlight sites
      try {
        if (marker.setStyle) {
          const originalColor = marker.options.fillColor;
          marker._originalFillColor = originalColor;
          marker.setStyle({
            weight: 3,
            color: '#FFA62B',
            fillOpacity: 0.8
          });
          marker.setZIndexOffset(5000);
        }
      } catch (e) {
        console.warn('Erreur highlight site:', e);
      }
    }
    highlightedMarkers.push(marker);
  });
  
  console.log(`✅ ${highlightedMarkers.length} markers highlighted`);
}
/**
 * Limpia los highlights
 */
function clearHighlights() {
  highlightedMarkers.forEach(marker => {
    try {
      if (marker.setStyle && marker._originalFillColor) {

        marker.setStyle({
          weight: 2,
          color: '#ffffff',
          fillOpacity: 1
        });
        marker.setZIndexOffset(0);
      } else {

        const icon = marker.getIcon();
        if (icon && icon.options && icon.options.html) {
          const restoredHtml = `<div class="marqueur-noeud" style="
            background:#16697A;
            width:10px;
            height:10px;
            border-radius:50%;
            border:2px solid white;
            pointer-events:auto;
            cursor:pointer;
          "></div>`;
          
          marker.setIcon(L.divIcon({
            className: 'marqueur-personnalise',
            html: restoredHtml,
            iconSize: [14, 14],
            iconAnchor: [7, 7]
          }));
          
          marker.setZIndexOffset(100);
        }
      }
    } catch (e) {
      console.warn('Error clearing highlight:', e);
    }
  });
  highlightedMarkers = [];
}
/**
 * Gère le clic sur un marqueur pendant le mode ajout
 */
function gererClicMarqueur(marker, type) {
  console.log('🎯 gererClicMarqueur appelé:', { 
    type, 
    modeActif: modeAjoutActif, 
    etapeActuelle: etapeAjout,
    markerId: marker.options.siteId 
  });
  
  if (!modeAjoutActif) {
    console.warn('⚠️ Mode ajout non actif!');
    return;
  }
  
  const etape = ETAPES[etapeAjout];
  
  console.log('🖱️ Click detectado:', { type, etapeType: etape.type, marker });
  
  
  // Vérifier que le type correspond à l'étape
  if (etape.type !== type) {
    const expected = etape.type === 'noeud' ? 'un nœud' : 'un site';
    alert(`⚠️ Vous devez sélectionner ${expected} pour cette étape !`);
    return;
  }
  
  // Enregistrer la sélection avec toutes les infos nécessaires
  const siteInfo = {
    marker: marker,
    siteId: marker.options.siteId,
    lat: marker.getLatLng ? marker.getLatLng().lat : null,
    lng: marker.getLatLng ? marker.getLatLng().lng : null,
    type: marker.options.type || null,
    numLivraison: marker.options.numLivraison || null
  };
  
  selectionData[etape.cible] = siteInfo;
  
  // Feedback visuel intenso
  marker.bindPopup(`<strong style="color:#16697A">${etape.icon} ${etape.texte}</strong><br><small>✅ Sélectionné: ${siteInfo.siteId}</small>`).openPopup();
  
  // Highlight temporaire
  if (type === 'site' && marker.setStyle) {
    marker.setStyle({ 
      fillColor: '#00FF00', 
      fillOpacity: 1,
      weight: 5,
      color: '#16697A'
    });
    setTimeout(() => {
      const originalColor = marker._originalFillColor || marker.options.fillColor;
      marker.setStyle({ 
        fillColor: originalColor, 
        fillOpacity: 1,
        weight: 2,
        color: '#ffffff'
      });
    }, 1500);
  }
  
  // Passer à l'étape suivante
  etapeAjout++;
  
  if (etapeAjout >= ETAPES.length) {
    terminerAjout();
  } else {
    mettreAJourInstructions();
    highlightAvailableMarkers();
  }
}

/**
 * Démarre le mode ajout
 */
function demarrerAjoutLivraison() {
  if (!carte) {
    alert("⚠️ La carte n'est pas encore chargée !");
    return;
  }
  
  if (modeAjoutActif) {
    alert("⚠️ Le mode ajout est déjà actif !");
    return;
  }
  
  console.log('🚀 Démarrage mode ajout...');
  console.log('Sites disponibles:', siteMarkers ? siteMarkers.length : 0);
  console.log('Nœuds disponibles:', noeudMarkers ? noeudMarkers.length : 0);
  

  if (!noeudMarkers || noeudMarkers.length === 0) {
    alert("⚠️ Aucun nœud disponible ! Chargez d'abord une carte.");
    return;
  }
  
  modeAjoutActif = true;
  etapeAjout = 0;
  selectionData = {
    collecteNoeud: null,
    collecteSitePrecedent: null,
    depotNoeud: null,
    depotSitePrecedent: null
  };
  
  // Créer l'overlay d'instructions
  creerInstructionOverlay();
  mettreAJourInstructions();
  
  // Ajouter des curseurs visuels
  const container = document.querySelector('.leaflet-container');
  if (container) {
    container.classList.add('mode-ajout-actif');
  }
  
  // listeners avant de highlihgt peut etre?
  activerEcouteursMarqueurs();
  highlightAvailableMarkers();
  
  console.log('✅ Mode ajout activé');
}

/**
 * Active les écouteurs de clics sur tous les marqueurs
 */
function activerEcouteursMarqueurs() {
  console.log('🎯 Activation des écouteurs...');
  console.log('Sites disponibles:', siteMarkers ? siteMarkers.length : 0);
  console.log('Nœuds disponibles:', noeudMarkers ? noeudMarkers.length : 0);
  
  // Sites
  if (Array.isArray(siteMarkers)) {
    siteMarkers.forEach(marker => {

      if (!marker._ajoutHandlerAttached) {
        const existingHandlers = marker._events?.click;
        if (existingHandlers && existingHandlers.length > 0) {
          marker._normalClickHandler = existingHandlers[0].fn;
        }
        
        // Remover listeners previos
        marker.off('click');
        
        // add nouvau listener
        marker.on('click', function(e) {
          L.DomEvent.stopPropagation(e);
          console.log('🔴 Site clicked:', marker.options.siteId, 'modeAjout:', modeAjoutActif);
          
          if (modeAjoutActif) {
            gererClicMarqueur(marker, 'site');
          } else if (marker._normalClickHandler) {
            marker._normalClickHandler.call(marker, e);
          } else {
            marker.openPopup();
          }
        });
        
        marker._ajoutHandlerAttached = true;
      }
    });
    console.log(`✅ ${siteMarkers.length} sites activés`);
  }
  
  if (Array.isArray(noeudMarkers) && noeudMarkers.length > 0) {
    noeudMarkers.forEach(marker => {
      if (!marker._ajoutHandlerAttached) {
        // NOEUDS PRETs
        marker._ajoutHandlerAttached = true;
        
        console.log('✓ Nœud listo:', marker.options.siteId, {
          enCarte: carte && carte.hasLayer(marker),
          position: marker.getLatLng()
        });
      }
    });
    console.log(`✅ ${noeudMarkers.length} nœuds validés`);
  } else {
    console.error('❌ AUCUN NŒUD DISPONIBLE!');
    console.log('noeudMarkers:', noeudMarkers);
  }
}
/**
 * Désactive les écouteurs de clics
 */
function desactiverEcouteursMarqueurs() {
  console.log('🔇 Désactivation des écouteurs...');
  
  if (Array.isArray(siteMarkers)) {
    siteMarkers.forEach(marker => {
      marker.off('click');
      // Reattacher el handler normal si existe
      if (marker._normalClickHandler) {
        marker.on('click', marker._normalClickHandler);
      }
    });
  }
  
  if (Array.isArray(noeudMarkers)) {
    noeudMarkers.forEach(marker => {
      marker.off('click');
      if (marker._normalClickHandler) {
        marker.on('click', marker._normalClickHandler);
      }
    });
  }
  
  clearHighlights();
}

/**
 * Termine le processus d'ajout
 */
function terminerAjout() {
  modeAjoutActif = false;
  
  // Retirer l'overlay
  if (instructionOverlay && carte) {
    carte.removeControl(instructionOverlay);
    instructionOverlay = null;
  }
  
  const container = document.querySelector('.leaflet-container');
  if (container) {
    container.classList.remove('mode-ajout-actif');
  }
  
  // Désactiver les écouteurs
  desactiverEcouteursMarqueurs();
  
  // Afficher un résumé
  const recap = `
🎉 Sélection terminée !

📋 Récapitulatif :
• Nœud collecte : ${selectionData.collecteNoeud?.options.siteId || 'N/A'}
• Site précédent (collecte) : ${selectionData.collecteSitePrecedent?.options.siteId || 'N/A'}
• Nœud dépôt : ${selectionData.depotNoeud?.options.siteId || 'N/A'}
• Site précédent (dépôt) : ${selectionData.depotSitePrecedent?.options.siteId || 'N/A'}

Voulez-vous enregistrer ces modifications ?
  `;
  
  if (confirm(recap)) {
    envoyerNouvellesLivraisons();
  } else {
    console.log('Ajout annulé par l\'utilisateur');
  }
}

/**
 * Annule le processus d'ajout
 */
function annulerAjout() {
  if (!confirm("❌ Voulez-vous vraiment annuler l'ajout de livraison ?")) return;
  
  modeAjoutActif = false;
  etapeAjout = 0;
  
  if (instructionOverlay && carte) {
    carte.removeControl(instructionOverlay);
    instructionOverlay = null;
  }
  
  const container = document.querySelector('.leaflet-container');
  if (container) {
    container.classList.remove('mode-ajout-actif');
  }
  
  desactiverEcouteursMarqueurs();
  
  console.log('Mode ajout annulé');
}

/**
 * Envoie les nouvelles livraisons au serveur
 */
function envoyerNouvellesLivraisons() {
  console.log('📤 Envoi des nouvelles livraisons...', selectionData);
  
  const payload = {
    collecte: {
      noeudId: selectionData.collecteNoeud?.options.siteId,
      sitePrecedentId: selectionData.collecteSitePrecedent?.options.siteId
    },
    depot: {
      noeudId: selectionData.depotNoeud?.options.siteId,
      sitePrecedentId: selectionData.depotSitePrecedent?.options.siteId
    }
  };
  
  fetch('/api/ajouter-livraison', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
  .then(res => {
    if (!res.ok) throw new Error('Erreur serveur: ' + res.status);
    return res.json();
  })
  .then(data => {
    console.log('✅ Livraisons ajoutées avec succès', data);
    alert('✅ Livraisons ajoutées avec succès !');
    
    // Recharger la carte
    fetch("/api/carte")
      .then(res => res.json())
      .then(donnees => {
        donneesGlobales = donnees;
        afficherDonneesSurCarte(donnees);
        configurerControlesVisibilite();
      });
  })
  .catch(err => {
    console.error('❌ Erreur lors de l\'ajout:', err);
    alert('❌ Erreur : ' + err.message);
  });
}
/**
 * Active les écouteurs de clics sur tous les marqueurs
 */
function activerEcouteursMarqueurs() {
  console.log('🎯 Activation des écouteurs...');
  
// Nœuds
  if (Array.isArray(noeudMarkers) && noeudMarkers.length > 0) {
    noeudMarkers.forEach(marker => {

      if (!carte.hasLayer(marker)) {
        console.warn('⚠️ Nœud no está en el mapa:', marker.options.siteId);
        return;
      }
      
      const existingHandler = marker._events && marker._events.click;
      if (existingHandler && !marker._normalClickHandler) {
        marker._normalClickHandler = existingHandler[0].fn;
      }
      
      marker.off('click');
      marker.off('mousedown');
      

      marker.on('click', function(e) {
        L.DomEvent.stopPropagation(e);
        L.DomEvent.preventDefault(e);
        
        console.log('🔵 Nœud clicked:', marker.options.siteId, 'modeAjout:', modeAjoutActif);
        
        if (modeAjoutActif) {
          gererClicMarqueur(marker, 'noeud');
        } else {
          if (marker._normalClickHandler) {
            marker._normalClickHandler.call(marker, e);
          } else if (marker.openPopup) {
            marker.openPopup();
          }
        }
      }, marker); 
      
      marker.on('mousedown', function(e) {
        if (modeAjoutActif) {
          L.DomEvent.stopPropagation(e);
          console.log('🔵 Nœud mousedown:', marker.options.siteId);
        }
      });
    });
    console.log(`✅ ${noeudMarkers.length} nœuds activés`);
  } else {
    console.warn("⚠️ Aucun nœud marker disponible");
  }
  
  // Nœuds
  if (Array.isArray(noeudMarkers) && noeudMarkers.length > 0) {
    noeudMarkers.forEach(marker => {
      const existingHandler = marker._events && marker._events.click;
      if (existingHandler && !marker._normalClickHandler) {
        marker._normalClickHandler = existingHandler[0].fn;
      }
      
      marker.off('click');
      
      marker.on('click', function(e) {
        L.DomEvent.stopPropagation(e);
        
        if (modeAjoutActif) {
          console.log('🔵 Nœud clicked (mode ajout):', marker.options.siteId);
          gererClicMarqueur(marker, 'noeud');
        } else {
          if (marker._normalClickHandler) {
            marker._normalClickHandler.call(marker, e);
          } else if (marker.openPopup) {
            marker.openPopup();
          }
        }
      });
    });
    console.log(`✅ ${noeudMarkers.length} nœuds activés`);
  }
}