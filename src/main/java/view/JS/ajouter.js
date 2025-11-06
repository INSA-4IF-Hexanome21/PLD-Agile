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
  
  instructionOverlay.onAdd = function() {
    const div = L.DomUtil.create('div', 'instruction-overlay');

    // Construction HTML avec boutons pour chaque trajet
    let boutonsTrajets = '';
    if (Object.keys(sitesParTrajet).length > 0) {
      for (const key in sitesParTrajet) {
        boutonsTrajets += `<button class="btn-trajet" data-trajet="${key}">Trajet ${key}</button> `;
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
          // Remplir trajetChoisi avec toutes les infos du trajet
          trajetChoisi = {
            key: trajetKey,
            sites: sitesParTrajet[trajetKey]?.sites || [],
            lignes: sitesParTrajet[trajetKey]?.lignes || [],
            couleur: sitesParTrajet[trajetKey]?.couleur || '#FFA62B'
          };
          console.log('🚚 Trajet choisi:', trajetChoisi);

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
 * Fonction pour surligner un trajet et ses lignes
 */
function surlignerTrajet(trajetKey) {
  if (!sitesParTrajet[trajetKey]) return;
  const lignes = sitesParTrajet[trajetKey].lignes || [];
  
  lignes.forEach(ligne => {
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
      // Restaurer la couleur originale (tu peux stocker la couleur si tu veux)
      ligne.setStyle({ color: ligne.options?.originalColor || '#3388ff', weight: 3, opacity: 0.8 });
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
  const targetMarkers = etape.type === 'noeud' ? noeudMarkers : siteMarkers;
  
  if (!Array.isArray(targetMarkers)) return;
  
  console.log(`🎨 Highlighting ${targetMarkers.length} ${etape.type}s pour l'étape ${etape.numero}`);
  
  targetMarkers.forEach(marker => {
    if (etape.type === 'noeud') {
      // Highlight des nœuds
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
  
  console.log(`✅ ${highlightedMarkers.length} markers highlighted`);
}

/**
Restaure les styles originaux des marqueurs mis en évidence
 */
function clearHighlights() {
  highlightedMarkers.forEach(marker => {
    try {
      if (marker.setStyle && marker._originalFillColor) {
        const originalRadius = marker._originalRadius || computeSiteRadius(carte);
        if (marker.setRadius) {
          marker.setRadius(originalRadius);
        }
        
        marker.setStyle({
          weight: 2,
          color: '#ffffff',
          fillColor: marker._originalFillColor,
          fillOpacity: 1
        });
        marker.setZIndexOffset(0);
        

        const tip = marker.getTooltip && marker.getTooltip();
        if (tip) {
          const content = tip.getContent ? tip.getContent() : (marker.options && marker.options.siteId ? marker.options.siteId : '');
          marker.unbindTooltip();
          marker.bindTooltip(content, { 
            permanent: false, 
            direction: 'top', 
            offset: [0, -originalRadius - 6] 
          });
        }
      } else if (marker._originalStyle) {
        const iconElement = marker.getElement();
        if (iconElement) {
          const noeudDiv = iconElement.querySelector('.marqueur-noeud');
          if (noeudDiv) {
            noeudDiv.style.background = marker._originalStyle.background || '#16697A';
            noeudDiv.style.boxShadow = marker._originalStyle.boxShadow || '0 2px 4px rgba(0,0,0,0.3)';
            noeudDiv.classList.remove('marqueur-noeud-highlight');
          }
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
}

/**
 * Désactive les écouteurs de clics
 */
function desactiverEcouteursMarqueurs() {
  console.log('🔇 Désactivation des écouteurs...');
  
  if (Array.isArray(siteMarkers)) {
    siteMarkers.forEach(marker => {
      marker.off('click');
      // Reattacher el handler normal si ca existe
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
  console.log('🎉 Terminer ajout - Données:', selectionData);
  
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
  
  // Déterminer le trajet à partir du site précédent de la collecte (j'ai laisse ca pour l'avoir quand on fixe les autres bugs)
  let numeroTrajet = null;
  if (selectionData.collecteSitePrecedent) {
    // Chercher le numéro de livraison/trajet du site précédent
    const sitePrecedent = siteMarkers.find(m => 
      m.options.siteId === selectionData.collecteSitePrecedent.siteId
    );
    
    if (sitePrecedent && sitePrecedent.options.numLivraison != null) {
      numeroTrajet = parseInt(sitePrecedent.options.numLivraison);
      console.log('📍 Trajet détecté:', numeroTrajet);
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
function envoyerNouvellesLivraisons(numeroTrajet) {
  console.log('📤 Envoi des nouvelles livraisons...', selectionData);
  
  // Préparer le payload selon le format requis
  const payload = {
    idCollecte: selectionData.collecteNoeud?.siteId,
    idPrecCollecte: selectionData.collecteSitePrecedent?.siteId,
    idDepot: selectionData.depotNoeud?.siteId,
    idPrecDepot: selectionData.depotSitePrecedent?.siteId,
    Trajet: numeroTrajet
  };
  
  console.log('📦 Payload préparé:', payload);
  console.log('📦 Payload JSON:', JSON.stringify(payload, null, 2));
  
  // POUR L'INSTANT: Juste afficher dans la console
  alert(`✅ Données prêtes à envoyer !

📦 Format:
${JSON.stringify(payload, null, 2)}

(Vérifiez la console pour voir les détails)`);
  

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