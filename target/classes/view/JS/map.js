// Inicializar Leaflet
function initMap() {
  const map = L.map('map', {
    center: [45.7578137, 4.8320114],
    zoom: 20
  });

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution:
      '&copy; <a href="https://www.openstreetmap.org/copyright" target="_blank" rel="noopener noreferrer">OpenStreetMap</a> contributors',
  }).addTo(map);

  // Fetch desde backend
  fetch("/api/carte")
    .then(res => res.json())
    .then(donnees => {
      donnees.noeuds.forEach(n => {
        L.marker([n.lat, n.lng]).addTo(map).bindPopup("Noeud " + n.id);
      });

      donnees.troncons.forEach(t => {
        const from = donnees.noeuds.find(n => n.id === t.from);
        const to = donnees.noeuds.find(n => n.id === t.to);
        if (from && to) {
          L.polyline([[from.lat, from.lng], [to.lat, to.lng]], { color: 'blue', weight: 4, opacity: 0.7 }).addTo(map);
        }
      });

      const bounds = L.latLngBounds(donnees.noeuds.map(n => [n.lat, n.lng]));
      map.fitBounds(bounds);
    })
    .catch(err => console.error(err));
}

// Llamar initMap automáticamente al cargar PickupDelivery.html
initMap();
