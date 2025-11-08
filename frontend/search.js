// ✅ Fixed typo: locationBtn (was loactionBtn)
const locationBtn = document.getElementById("locationBtn");
const searchBar = document.getElementById("searchBar");
const locationBox = document.getElementById("locationBox");
const detectBtn = document.getElementById("detectBtn");
const manualBtn = document.getElementById("manualBtn");
const statusMsg = document.getElementById("statusMsg");

// Step 1: When user clicks 📍 Location button → show popup
locationBtn.addEventListener("click", () => {
  searchBar.classList.add("hidden");
  locationBox.classList.remove("hidden");
});

// Step 2: Detect location using Geolocation API
detectBtn.addEventListener("click", () => {
  statusMsg.textContent = "Detecting location...";
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const lat = pos.coords.latitude;
        const lon = pos.coords.longitude;
        sessionStorage.setItem("latitude", lat);
        sessionStorage.setItem("longitude", lon);
        statusMsg.textContent = "✅ Location detected!";
        closePopupAfterDelay();
        addUserMarker(lat, lon); // show on map
      },
      (err) => {
        statusMsg.textContent = "❌ Error: " + err.message;
      }
    );
  } else {
    statusMsg.textContent = "Geolocation not supported.";
  }
});

// Step 3: Manual city entry → convert to coordinates using OpenStreetMap API
manualBtn.addEventListener("click", async () => {
  const city = document.getElementById("cityInput").value.trim();
  if (!city) {
    alert("Please enter a city name.");
    return;
  }

  statusMsg.textContent = "Fetching coordinates for " + city + "...";

  try {
    const res = await fetch(
      `https://nominatim.openstreetmap.org/search?city=${encodeURIComponent(city)}&format=json`
    );
    const data = await res.json();
    if (data && data.length > 0) {
      const { lat, lon } = data[0];
      sessionStorage.setItem("latitude", lat);
      sessionStorage.setItem("longitude", lon);
      statusMsg.textContent = "✅ Coordinates saved for " + city;
      closePopupAfterDelay();
      addUserMarker(lat, lon); // show on map
    } else {
      statusMsg.textContent = "❌ Could not find city.";
    }
  } catch (err) {
    statusMsg.textContent = "⚠️ Network error.";
  }
});

// Step 4: Close popup after success
function closePopupAfterDelay() {
  setTimeout(() => {
    locationBox.classList.add("hidden");
    searchBar.classList.remove("hidden");
  }, 1000);
}

/* ------------------------------------------------------------------
   🌍 LEAFLET MAP INITIALIZATION (instead of Google Maps)
------------------------------------------------------------------- */
let map;
let markers = [];
let userMarker;

// Initialize map (center Hyderabad)
function initMap() {
  map = L.map("map").setView([17.3850, 78.4867], 12);

  // Add OpenStreetMap layer
  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "© OpenStreetMap contributors",
  }).addTo(map);
}

// Call initMap when page loads
document.addEventListener("DOMContentLoaded", initMap);

// Search Button Click
document.getElementById("searchBtn").addEventListener("click", () => {
  const medicine = document.getElementById("medicineInput").value.trim();
  if (!medicine) {
    alert("Please enter a medicine name!");
    return;
  }

  // Get coordinates from session storage (set by detect/manual)
  const lat = parseFloat(sessionStorage.getItem("latitude"));
  const lon = parseFloat(sessionStorage.getItem("longitude"));

  // ✅ Fixed check — use isNaN() instead of !lat or !lon
  if (isNaN(lat) || isNaN(lon)) {
    alert("Please set your location first 📍");
    return;
  }

  console.log("Sending to backend:", lat, lon);
  const data = {
    medicineName: medicine,
    useAlternative: false,
    userLat: lat,
    userLon: lon,
  };

  fetch("http://localhost:8989/api/medicine/search", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  })
    .then((res) => res.json())
    .then((result) => displayResults(result))
    .catch((err) => console.error(err));
});

// Display pharmacies + markers on Leaflet map
f// Display pharmacies + markers on Leaflet map
function displayResults(pharmacies) {
  const listContainer = document.getElementById("pharmacyList");
  listContainer.innerHTML = "";

  // Clear old markers
  markers.forEach((m) => map.removeLayer(m));
  markers = [];

  if (!Array.isArray(pharmacies) || pharmacies.length === 0) {
    listContainer.innerHTML = `<p>No pharmacies found.</p>`;
    return;
  }

  pharmacies.forEach((pharma) => {
    // Left panel
    const item = document.createElement("div");
    item.className = "pharmacy-item";
    item.innerHTML = `
      <strong>${pharma.name}</strong><br>
      📍 ${pharma.address}, ${pharma.city}<br>
      📞 ${pharma.phone}<br>
      📧 ${pharma.email || "N/A"}<br>
      📏 Distance: ${pharma.distance.toFixed(2)} km
    `;
    listContainer.appendChild(item);

    // Red marker for pharmacy
    const marker = L.circleMarker([pharma.latitude, pharma.longitude], {
      radius: 8,
      fillColor: "#FF0000", // Red
      color: "#fff",
      weight: 2,
      opacity: 1,
      fillOpacity: 1,
    })
      .addTo(map)
      .bindPopup(`<b>${pharma.name}</b><br>${pharma.address}<br>${pharma.city}`);

    markers.push(marker);

    // Click list → focus map
    item.addEventListener("click", () => {
      map.setView([pharma.latitude, pharma.longitude], 15);
      marker.openPopup();
    });
  });

  // Center map on first pharmacy
  map.setView([pharmacies[0].latitude, pharmacies[0].longitude], 13);
}

// User location blue marker
function addUserMarker(lat, lon) {
  if (userMarker) map.removeLayer(userMarker);

  userMarker = L.circleMarker([lat, lon], {
    radius: 8,
    fillColor: "#4285F4", // Blue
    color: "#fff",
    weight: 2,
    opacity: 1,
    fillOpacity: 1,
  })
    .addTo(map)
    .bindPopup("📍 You are here")
    .openPopup();

  map.setView([lat, lon], 13);
}
