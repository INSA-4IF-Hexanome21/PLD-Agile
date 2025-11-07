let modeAjoutActif = false;
let etapeAjout = 0;
let selectionData = {
  collecteNoeud: null,
  collecteSitePrecedent: null,
  depotNoeud: null,
  depotSitePrecedent: null
};
let trajetChoisi = {};
let instructionOverlay = null;
let highlightedMarkers = [];



const ETAPES = [
  
  { 
    numero: 1, 
    texte: "Sélectionnez un trajet", 
    type: 'trajet',
    cible: 'trajetChoisi',
    icon: '🛣️'
  },
  { 
    numero: 2, 
    texte: "Sélectionnez un nœud pour ajouter une collecte", 
    type: 'noeud',
    cible: 'collecteNoeud',
    icon: '📍'
  },
  { 
    numero: 3, 
    texte: "Sélectionnez le site qui précède la nouvelle collecte", 
    type: 'site',
    cible: 'collecteSitePrecedent',
    icon: '🟢'
  },
  { 
    numero: 4, 
    texte: "Sélectionnez un nœud pour ajouter un dépôt", 
    type: 'noeud',
    cible: 'depotNoeud',
    icon: '📍'
  },
  { 
    numero: 5, 
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
  
  griserTousLesElementsCliquables();
  instructionOverlay.onAdd = function() {
    const div = L.DomUtil.create('div', 'instruction-overlay');

    // Construction HTML avec boutons pour chaque trajet
    let boutonsTrajets = '';
    if (Object.keys(sitesParTrajet).length > 0) {
      for (const key in sitesParTrajet) {
        boutonsTrajets += `<button class="btn-trajet" data-trajet="${key}">Trajet ${parseInt(key) + 1}</button> `;
      }
    }

    div.innerHTML = `
      <div class="instruction-header">
        <strong>✍🏻 Mode Ajout Livraison</strong>
        <button id="annuler-ajout" class="btn-cancel">✖</button>
      </div>
      <div class="instruction-body">
        <div class="etape-numero">
          <span id="etape-icon">📍</span>
          Étape <span id="etape-num">1</span>/5
        </div>
        <p id="instruction-texte">Sélectionnez un nœud pour ajouter une collecte</p>
        <div class="progress-bar">
          <div id="progress-fill" class="progress-fill" style="width: 25%"></div>
        </div>
      </div>
      <div class="selections-recap">
        <div id="recap-content"><small>Aucune sélection</small></div>
         <div class="trajet-buttons-container">
          ${boutonsTrajets}
        </div>
      </div>
    `;
    
    L.DomEvent.disableClickPropagation(div);
    L.DomEvent.disableScrollPropagation(div);
    
    setTimeout(() => {
      const btnCancel = document.getElementById('annuler-ajout');
      if (btnCancel) {
        btnCancel.addEventListener('click', annulerAjout);
      }

      const btnTrajets = div.querySelectorAll('.btn-trajet');
      btnTrajets.forEach(btn => {
        const trajetKey = btn.getAttribute('data-trajet');

        // Hover pour surligner le trajet sur la carte
        btn.addEventListener('mouseenter', () => {
          surlignerTrajet(trajetKey);
        });
        btn.addEventListener('mouseleave', () => {
          enleverSurlignageTrajet(trajetKey);
        });

        // Click: sélection du trajet
        btn.addEventListener('click', () => {
            enleverSurlignageTrajet(trajetKey);
          // Remplir trajetChoisi avec toutes les infos du trajet
           trajetChoisi = {
            key: trajetKey,
            parcours: sitesParTrajet[trajetKey]?.parcours || [],
            entrepot: sitesParTrajet[trajetKey]?.entrepot || null,
            lignes: sitesParTrajet[trajetKey]?.lignes || [],
            couleur: sitesParTrajet[trajetKey]?.couleur || '#FFA62B'
          };

          // Supprimer la div contenant les boutons
          const container = div.querySelector('.trajet-buttons-container');
          if (container) container.remove();

          // Passer à l'étape suivante
          etapeAjout++;
          mettreAJourInstructions();

          
          setTimeout(() => {
            highlightAvailableMarkers();
          }, 100);
        });
      });

    }, 100);

        const zoomLinks = document.querySelectorAll('.leaflet-control-zoom a');
    zoomLinks.forEach(el => {
        el.style.display = '';
        el.style.background = 'var(--spie-dark-teal)';
        el.style.color = 'var(--white)';
        el.style.border = 'none';
        el.style.borderRadius = '4px';
        el.style.transition = 'all 0.3s ease';
        el.style.width = '';
        el.style.height = '';
        el.style.padding = '0'
    });

    // Undo/Redo : complètement caché
    const undoRedo = document.querySelectorAll('.leaflet-control-undoRedo');
    undoRedo.forEach(el => {
        el.style.display = 'none';
        el.style.width = '0';
        el.style.height = '0';
    });
    return div;
  };
  
  instructionOverlay.addTo(carte);
  
  // Attacher l'événement d'annulation
  setTimeout(() => {
    const btnCancel = document.getElementById('annuler-ajout');
    if (btnCancel) {
      btnCancel.addEventListener('click', annulerAjout);
    }
  }, 100);
}


/**
 * Fonction pour surligner un trajet et ses lignes
 */
function surlignerTrajet(trajetKey) {
  if (!sitesParTrajet[trajetKey]) return;
  const lignes = sitesParTrajet[trajetKey].lignes || [];
  
  lignes.forEach(ligne => {
    // stocker l’original si ce n’est pas déjà fait
    if (!ligne.options.originalColor) {
      ligne.options.originalColor = ligne.options.color;
      ligne.options.originalWeight = ligne.options.weight;
      ligne.options.originalOpacity = ligne.options.opacity;
    }

    if (ligne.setStyle) {
      ligne.setStyle({ color: '#FFA62B', weight: 5, opacity: 1 });
    }
  });
}


/**
 * Enlève le surlignage d'un trajet
 */
function enleverSurlignageTrajet(trajetKey) {
  if (!sitesParTrajet[trajetKey]) return;
  const lignes = sitesParTrajet[trajetKey].lignes || [];
  
  lignes.forEach(ligne => {
    if (ligne.setStyle) {
      ligne.setStyle({ 
        color: ligne.options.originalColor || '#3388ff', 
        weight: ligne.options.originalWeight || 3, 
        opacity: ligne.options.originalOpacity || 0.8 
      });
    }
  });
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
  if (progressEl) progressEl.style.width = (((etapeAjout + 1) / 5) * 100) + '%';
  
  // Mise à jour du récapitulatif
  if (recapEl) {
    let html = '<small><strong>Sélections:</strong></small><ul class="recap-list">';
    if (selectionData.collecteNoeud) 
      html += `<li>✅ Nœud collecte: <code>${selectionData.collecteNoeud.siteId || 'N/A'}</code></li>`;
    if (selectionData.collecteSitePrecedent) 
      html += `<li>✅ Site précédent collecte: <code>${selectionData.collecteSitePrecedent.siteId || 'N/A'}</code></li>`;
    if (selectionData.depotNoeud) 
      html += `<li>✅ Nœud dépôt: <code>${selectionData.depotNoeud.siteId || 'N/A'}</code></li>`;
    if (selectionData.depotSitePrecedent) 
      html += `<li>✅ Site précédent dépôt: <code>${selectionData.depotSitePrecedent.siteId || 'N/A'}</code></li>`;
    html += '</ul>';
    recapEl.innerHTML = html;
  }
}


function highlightAvailableMarkers() {
  clearHighlights();
  
  const etape = ETAPES[etapeAjout];
  const sitesConcernes = [];

 sitesConcernes.length = 0; // Vide la liste avant de la remplir

  siteMarkers.forEach(marker => {
    const siteId = String(marker.options.siteId || marker.options.id || marker.siteId || marker.id);

    
    const faitPartieTrajet =
      (trajetChoisi.parcours && trajetChoisi.parcours.some(site => String(site.id) === siteId)) ||
      (trajetChoisi.entrepot && String(trajetChoisi.entrepot.id) === siteId);

    if (faitPartieTrajet) {
      sitesConcernes.push(marker);
    }
  });


  
  const targetMarkers = etape.type === 'noeud' ? noeudMarkers : sitesConcernes;
  
  if (!Array.isArray(targetMarkers)) return;
  
  

  Object.keys(visibilityState).forEach(k => visibilityState[k] = false);





  if (etape.type === 'noeud') {
    visibilityState['noeuds'] = true;
  }
  else if(etape.type == 'trajet') {
    visibilityState['trajets'] = true;
    visibilityState['depot'] = true;
    visibilityState['collecte'] = true;
    visibilityState['entrepot'] = true;
  }else if (etape.type === 'site') {
    visibilityState['entrepot'] = true;
    visibilityState['depot'] = true;
    visibilityState['collecte'] = true;
  }

  updateVisibility();
 
  if (etape.type === 'site') {
    for (const site of siteMarkers) {
      try {
        const siteId = site.options?.siteId;
        const estConcerne = sitesConcernes.some(s => s.options?.siteId === siteId);

        // Cacher les sites qui ne sont pas dans le trajet
        if (!estConcerne && carte.hasLayer(site)) {
          carte.removeLayer(site);
        }

        if (selectionData.collecteSitePrecedent && trajetChoisi.parcours) {
          const precedentId = String(selectionData.collecteSitePrecedent.siteId);
          const indexPrecedent = trajetChoisi.parcours.findIndex(s => String(s.id) === precedentId);

          if (indexPrecedent === -1) return; // quitte la fonction si problème

          const sitesApres = trajetChoisi.parcours.slice(indexPrecedent + 1);

          if (sitesApres.length === 0) {
            selectionData.depotSitePrecedent = selectionData.collecteNoeud;
            etapeAjout++;
            terminerAjout();
            return; // quitte complètement highlightAvailableMarkers()
          }

          // Supprimer les sites qui ne sont pas après le site précédent
          for (const marker of siteMarkers) {
            const markerId = String(marker.options.siteId);
            const estApres = sitesApres.some(s => String(s.id) === markerId);

            if (!estApres && carte.hasLayer(marker)) {
              carte.removeLayer(marker);
            }
          }
        }

      } catch (e) {
        console.warn('Erreur traitement site:', e);
      }
    }
  }




    

  targetMarkers.forEach(marker => {
    if (etape.type === 'noeud') {
      // Highlight des nœuds
      try {
        const iconElement = marker.getElement();
        if (iconElement) {
          const noeudDiv = iconElement.querySelector('.marqueur-noeud'); 
          const isSelected = selectionData.collecteNoeud && (String(marker.options.siteId || marker.options.id) === String(selectionData.collecteNoeud.siteId || selectionData.collecteNoeud.id));

          marker._originalStyle = {
            background: noeudDiv.style.background,
            boxShadow: noeudDiv.style.boxShadow,
            width: noeudDiv.style.width,
            height: noeudDiv.style.height
          };
          if (noeudDiv && !isSelected) {

            
            // Aplicar highlight
            noeudDiv.style.background = '#FFA62B';
            noeudDiv.style.boxShadow = '0 0 15px #FFA62B, 0 0 25px #FFA62B';
            noeudDiv.classList.add('marqueur-noeud-highlight');
            
            marker.options.interactive = true;
            marker.options.bubblingMouseEvents = false;
            
            marker.off('click');
            marker.off('mousedown');
            marker.on('click', function(e) {
              L.DomEvent.stopPropagation(e);
              L.DomEvent.preventDefault(e);
              if (modeAjoutActif) {
                gererClicMarqueur(marker, 'noeud');
              }
            });
            marker.on('mousedown', function(e) {
              L.DomEvent.stopPropagation(e);
              L.DomEvent.preventDefault(e);
            });
          }
          if (noeudDiv && isSelected) {

            if (marker._blinkInterval) clearInterval(marker._blinkInterval);

            let isRed = false;
            marker._blinkInterval = setInterval(() => {
              isRed = !isRed;
              noeudDiv.style.background = isRed ? '#ff0000' : '#ffffff';
              noeudDiv.style.boxShadow = isRed
                ? '0 0 10px #ff0000, 0 0 20px #ff0000'
                : '0 0 10px #ffffff, 0 0 20px #ffffff';
            }, 500);

            marker.off('click');
          }
        }
      } catch (e) {
        console.warn('Erreur highlight noeud:', e);
      }
    } else if(etape.type === 'site'){
      // Highlight sites
      try {
        noeudMarkers.forEach(noeud => {
          if (carte.hasLayer(noeud)) {
            carte.removeLayer(noeud);
          }
        });
        if (marker.setStyle && marker.setRadius) {
          const originalColor = marker.options.fillColor;
          const originalRadius = marker.options.radius;
          marker._originalFillColor = originalColor;
          marker._originalRadius = originalRadius;
          
          const newRadius = Math.max(originalRadius * 2, 16);
          marker.setRadius(newRadius);
          
          marker.setStyle({
            weight: 4,
            color: '#FFA62B',
            fillColor: originalColor,
            fillOpacity: 0.9
          });
          marker.setZIndexOffset(5000);
          
          const tip = marker.getTooltip && marker.getTooltip();
          if (tip) {
            const content = tip.getContent ? tip.getContent() : (marker.options && marker.options.siteId ? marker.options.siteId : '');
            marker.unbindTooltip();
            marker.bindTooltip(content, { 
              permanent: false, 
              direction: 'top', 
              offset: [0, -newRadius - 8] 
            });
          }
        }
      } catch (e) {
        console.warn('Erreur highlight site:', e);
      }
    }
    highlightedMarkers.push(marker);
  });
  
}

/**
Restaure les styles originaux des marqueurs mis en évidence
 */
function clearHighlights() {
  highlightedMarkers.forEach(marker => {
    try {
      // --- Pour les sites (Leaflet circleMarker) ---
      if (marker.setStyle && marker._originalFillColor) {
        if (marker.setRadius && marker._originalRadius) {
          marker.setRadius(marker._originalRadius);
        }
        marker.setStyle({
          weight: 2,
          color: '#ffffff',
          fillColor: marker._originalFillColor,
          fillOpacity: 1
        });
        marker.setZIndexOffset(0);

        // Restaurer le tooltip si nécessaire
        const tip = marker.getTooltip && marker.getTooltip();
        if (tip) {
          const content = tip.getContent ? tip.getContent() : (marker.options.siteId || '');
          marker.unbindTooltip();
          marker.bindTooltip(content, { 
            permanent: false, 
            direction: 'top', 
            offset: [0, - (marker._originalRadius || 8) - 6] 
          });
        }
      } 
      // --- Pour les nœuds HTML ---
      else if (marker._originalStyle) {
        const iconElement = marker.getElement();
        if (iconElement) {
          const noeudDiv = iconElement.querySelector('.marqueur-noeud');
          if (noeudDiv) {
            noeudDiv.style.background = marker._originalStyle.background || '#16697A';
            noeudDiv.style.boxShadow = marker._originalStyle.boxShadow || '0 2px 4px rgba(0,0,0,0.3)';
            noeudDiv.style.width = marker._originalStyle.width || '24px';
            noeudDiv.style.height = marker._originalStyle.height || '24px';
            noeudDiv.classList.remove('marqueur-noeud-highlight');

            // Restaurer l'interactivité
            marker.options.interactive = true;
            marker.options.bubblingMouseEvents = true;

            // Stop blink si actif
            if (marker._blinkInterval) {
              clearInterval(marker._blinkInterval);
              marker._blinkInterval = null;
            }
          }
        }
      }
    } catch (e) {
      console.warn('Erreur clearHighlights pour marker:', marker, e);
    }
  });

  // Vide le tableau
  highlightedMarkers = [];
}


/**
 * Gère le clic sur un marqueur pendant le mode ajout
 */
function gererClicMarqueur(marker, type) {
  
  if (!modeAjoutActif) {
    console.warn('⚠️ Mode ajout non actif!');
    return;
  }
  
  const etape = ETAPES[etapeAjout];
  
  
  
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
    type: marker.options.siteType || marker.options.type || null,
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
    }, 500);
  }
  

  clearHighlights();
  etapeAjout++;
  
  if (etapeAjout >= ETAPES.length) {
    terminerAjout();
  } else {
    mettreAJourInstructions();

    setTimeout(() => {
      highlightAvailableMarkers();
    }, 100);
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

  if (Object.keys(sitesParTrajet).length === 0) {
    alert("⚠️ Aucun trajet disponible !");
    return;
  }
  

  

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
  
  activerEcouteursMarqueurs();
  highlightAvailableMarkers();
  
}

/**
 * Active les écouteurs de clics sur tous les marqueurs
 */
function activerEcouteursMarqueurs() {
  
  
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
  }
  
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
        }
      });
    });
  } else {
    console.warn("⚠️ Aucun nœud marker disponible");
  }
}

/**
 * Désactive les écouteurs de clics
 */
function desactiverEcouteursMarqueurs() {
  
  if (Array.isArray(siteMarkers)) {
    siteMarkers.forEach(marker => {
      marker.off('click');
      // Reattacher el handler normal si ca existe
      if (marker._normalClickHandler) {
        marker.on('click', marker._normalClickHandler);
      }

      marker._ajoutHandlerAttached = false;
    });
  }
  
  if (Array.isArray(noeudMarkers)) {
    noeudMarkers.forEach(marker => {
      marker.off('click');
      if (marker._normalClickHandler) {
        marker.on('click', marker._normalClickHandler);
      }

      marker._ajoutHandlerAttached = false;
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
  
  
  desactiverEcouteursMarqueurs();
  restaurerElementsCliquables();

  // Déterminer le trajet à partir du site précédent de la collecte (j'ai laisse ca pour l'avoir quand on fixe les autres bugs)
  let numeroTrajet = null;
  if (selectionData.collecteSitePrecedent) {
    // Chercher le numéro de livraison/trajet du site précédent
    const sitePrecedent = siteMarkers.find(m => 
      m.options.siteId === selectionData.collecteSitePrecedent.siteId
    );
    
    if (sitePrecedent && sitePrecedent.options.numLivraison != null) {
      numeroTrajet = parseInt(sitePrecedent.options.numLivraison);
    }
  }
  
  // Si on n'a pas trouvé le trajet, demander à l'utilisateur
  if (numeroTrajet == null) {
    const input = prompt("⚠️ Numéro de trajet non détecté. Entrez le numéro du trajet (1, 2, 3, etc.):");
    if (input) {
      numeroTrajet = parseInt(input);
    }
  }
  
  // Afficher un résumé détaillé
  const recap = `
🎉 Sélection terminée !

📋 Récapitulatif :
━━━━━━━━━━━━━━━━━━━━━
🟢 COLLECTE
  • Nœud : ${selectionData.collecteNoeud?.siteId || 'N/A'}
  • Site précédent : ${selectionData.collecteSitePrecedent?.siteId || 'N/A'}

🔴 DÉPÔT
  • Nœud : ${selectionData.depotNoeud?.siteId || 'N/A'}
  • Site précédent : ${selectionData.depotSitePrecedent?.siteId || 'N/A'}

🚚 TRAJET : ${numeroTrajet || 'Non défini'}
━━━━━━━━━━━━━━━━━━━━━

Voulez-vous enregistrer ces modifications ?
  `;
  
  if (confirm(recap)) {
    envoyerNouvellesLivraisons(numeroTrajet);
  }
}

/**
 * Annule le processus d'ajout
 */
function annulerAjout() {
  if (!confirm("❌ Voulez-vous vraiment annuler l'ajout de livraison ?")) return;
  
  restaurerElementsCliquables();
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
  visibilityState['trajets'] = true;
  visibilityState['depot'] = true;
  visibilityState['collecte'] = true;
  visibilityState['entrepot'] = true;
   visibilityState['noeuds'] = false;
  updateVisibility();
  mettreAJourTrajetsFlottant();
  
}

/**
 * Envoie les nouvelles livraisons au serveur
 */
function envoyerNouvellesLivraisons(numeroTrajet) {
  
  // Préparer le payload selon le format requis
  const payload = {
    idCollecte: selectionData.collecteNoeud?.siteId,
    idPrecCollecte: selectionData.collecteSitePrecedent?.siteId,
    idDepot: selectionData.depotNoeud?.siteId,
    idPrecDepot: selectionData.depotSitePrecedent?.siteId,
    Trajet: numeroTrajet
  };
  
  
//   // POUR L'INSTANT: Juste afficher dans la console
//   alert(`✅ Données prêtes à envoyer !

// 📦 Format:
// ${JSON.stringify(payload, null, 2)}

// (Vérifiez la console pour voir les détails)`);
  

  fetch('/api/ajouter-livraison', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload, null, 2)
  })
  .then(res => {
    if (!res.ok) throw new Error('Erreur serveur: ' + res.status);
    return res.json();
  })
  .then(data => {
    
    // Recharger la carte
    fetch("/api/carte")
      .then(res => res.json())
      .then(donnees => {
        donneesGlobales = donnees;
        afficherDonneesSurCarte(donnees);
        configurerControlesVisibilite();
        mettreAJourTrajetsFlottant();
        mettreAJourSitesImpactesFlottant();
        if (document.getElementById('form-livreurs')) {
          assignationLivraison();
      }
      });
  })
  .catch(err => {
    console.error('❌ Erreur lors de l\'ajout:', err);
    alert('❌ Erreur : ' + err.message);
  });

}


// --- Sauvegarde de l'état original des éléments ---
function griserTousLesElementsCliquables() {
  const cliquables = document.querySelectorAll(
      'button, a, input, select, textarea, [role="button"], .cliquable'
  );

  cliquables.forEach(el => {
      // Exclure overlay
      if (el.closest('.instruction-overlay')) return;

      // Garder le zoom visible et fonctionnel
      if (el.closest('.leaflet-control-zoom')) return;

      // Cacher complètement le undo/redo
      if (el.closest('.leaflet-control-undoRedo')) {
          el.style.display = 'none';
          el.style.height = 0;
          el.style.width = 0;
          return;
      }

      // Sauvegarder l'état original
      if (!el.dataset.originalState) {
          el.dataset.originalState = JSON.stringify({
              pointerEvents: el.style.pointerEvents || '',
              opacity: el.style.opacity || '',
              disabled: el.disabled || false
          });
      }

      // Griser et désactiver
      el.style.pointerEvents = 'none';
      el.style.opacity = '0.5';
      if ('disabled' in el) el.disabled = true;
  });

}





// --- Restauration de l'état original ---
function restaurerElementsCliquables() {
    const cliquables = document.querySelectorAll(
        'button, a, input, select, textarea, [role="button"], .cliquable'
    );

    cliquables.forEach(el => {
        if (el.dataset.originalState) {
            const state = JSON.parse(el.dataset.originalState);

            // Restauration des valeurs d'origine
            el.style.pointerEvents = state.pointerEvents;
            el.style.opacity = state.opacity;
            el.disabled = state.disabled;

            // Nettoyage du dataset
            delete el.dataset.originalState;
        }
    });

    // Zoom : restaurer le style
    const zoomLinks = document.querySelectorAll('.leaflet-control-zoom a');
    zoomLinks.forEach(el => {
        el.style.display = '';
        el.style.background = 'var(--spie-dark-teal)';
        el.style.color = 'var(--white)';
        el.style.border = 'none';
        el.style.borderRadius = '4px';
        el.style.transition = 'all 0.3s ease';
        el.style.width = '';
        el.style.height = '';
        el.style.padding = '';
        el.style.lineHeight = '';
        el.style.justifyContent = '';
        el.style.alignItems = '';
    });

    // Undo/Redo : restaurer conteneur ET boutons internes
    const undoRedoContainers = document.querySelectorAll('.leaflet-control-undoRedo');
    undoRedoContainers.forEach(container => {
        container.style.display = '';
        container.style.width = '';
        container.style.height = '';
        container.style.padding = '';

        // Restaurer boutons internes
        const buttons = container.querySelectorAll('a');
        buttons.forEach(btn => {
            btn.style.display = '';
            btn.style.width = '';
            btn.style.height = '';
            btn.style.background = 'var(--spie-dark-teal)';
            btn.style.color = 'var(--white)';
            btn.style.border = 'none';
            btn.style.borderRadius = '4px';
            btn.style.transition = 'all 0.3s ease';
            btn.style.lineHeight = '';
            btn.style.padding = '';
            btn.style.justifyContent = '';
            btn.style.alignItems = '';
        });
    });

}

