// Show selected section
function showSection(sectionId) {
  document.querySelectorAll(".content-section").forEach(sec => sec.classList.add("hidden"));
  document.getElementById(sectionId).classList.remove("hidden");
}

// Logout (just demo)
function logout() {
  alert("Logging out...");
  window.location.href = "login.html";
}

// Handle Add Medicine Form
document.getElementById("medicineForm")?.addEventListener("submit",  async function(e){
  e.preventDefault();
  const userId = sessionStorage.getItem("pharmacyId");
  const name = document.getElementById("medName").value;
  const category = document.getElementById("medCategory").value;
  const qty = document.getElementById("medQty").value;
  const price = document.getElementById("medPrice").value;
  const expiry = document.getElementById("medExpiry").value;
  const batch = document.getElementById("batchNo").value;
  const salt = document.getElementById("salt").value;
  

  const alt = document.getElementById("medalt").value;
  const medicineData = {
    pharmacyId: userId,
    medicineName: name,
    type: category,
    price: price,
    alternativeMedicine: alt,
    saltcomposition: salt,  // Optional — set empty if not used
    expiryDate: expiry,
    stock: qty,
    batchNumber: batch
  };
  
  try {
    const response = await fetch("http://localhost:8989/api/addMedicine", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(medicineData)
    });

    if (!response.ok) {
      throw new Error("Failed to add medicine");
    }

    const result = await response.text(); // Since backend returns String
    alert(result);
    } catch (error) {
    console.error("Error:", error);
    alert("Something went wrong while adding medicine.");
  }
});

// Delete medicine row
function deleteRow(button) {
  const row = button.parentNode.parentNode;
  row.parentNode.removeChild(row);
}

// Function to fetch stock items for the logged-in pharmacy module-3A
async function loadStock() {
  const pharmacyId = sessionStorage.getItem("pharmacyId");
  if (!pharmacyId) {
    alert("Pharmacy ID not found. Please login again.");
    return;
  }

  try {
    const response = await fetch(`http://localhost:8989/${pharmacyId}/stocks`);
    if (!response.ok) throw new Error("Failed to fetch stock items");

    const stocks = await response.json();
    const stockTable = document.getElementById("stockTable");

    // Clear existing rows
    stockTable.innerHTML = "";
    
    stocks.forEach(stock => {
      const row = document.createElement("tr");

      // Highlight low stock (qty ≤ 10)
      if (stock.quantity <= 10) {
        row.classList.add("low-stock");
      }

      row.innerHTML = `
        <td>${stock.stockId}</td> 
        <td>${stock.batchNumber}</td>
        <td>${stock.medicine}</td>
        <td>${stock.type}</td>
        <td>${stock.quantity}</td>
        <td>₹${stock.price}</td>
        <td>${stock.expiryDate}</td>
        <td>
          <button onclick="editStock(${stock.stockId})">Edit</button>
          <button onclick="deleteStock(${stock.stockId}, this)">Delete</button>
        </td>
      `;

      stockTable.appendChild(row);
    });
    } catch (error) {
    console.error("Error loading stock:", error);
    alert("Could not load stock items. Check console for details.");
  }
}

// Show stock section and automatically load data
function showStockSection() {
  showSection("stock");
  loadStock();
}
// Edit a stock row
function editStock(stockId) {
  const table = document.getElementById("stockTable");
  const rows = table.getElementsByTagName("tr");

  for (let row of rows) {
    const idCell = row.cells[0];
    if (idCell && parseInt(idCell.textContent) === stockId) {
      // Hide other rows
      for (let r of rows) {
        if (r !== row) r.style.display = "none";
      }

      // Get current data
      const batchNo = row.cells[1].textContent;
      const name = row.cells[2].textContent;
      const category = row.cells[3].textContent;
      const qty = row.cells[4].textContent;
      const price = row.cells[5].textContent.replace("₹", "");
      const expiry = row.cells[6].textContent;

      // Replace row content with editable fields
      row.innerHTML = `
        <td>${stockId}</td>
        <td><input type="text" id="editBatch" value="${batchNo}"></td>
        <td><input type="text" id="editName" value="${name}"></td>
        <td><input type="text" id="editCategory" value="${category}"></td>
        <td><input type="number" id="editQty" value="${qty}"></td>
        <td><input type="number" id="editPrice" value="${price}"></td>
        <td><input type="date" id="editExpiry" value="${expiry}"></td>
        <td>
          <button onclick="saveStock(${stockId})">Save</button>
          <button onclick="cancelEdit()">Cancel</button>
        </td>
      `;
      break;
    }
  }
}

// Cancel edit and reload all data
function cancelEdit() {
  loadStock();
}

// Save the edited stock details
async function saveStock(stockId) {
  const updatedStock = {
    stockId: stockId,
    batchNumber: document.getElementById("editBatch").value,
    medicine: document.getElementById("editName").value,
    type: document.getElementById("editCategory").value,
    quantity: document.getElementById("editQty").value,
    price: document.getElementById("editPrice").value,
    expiryDate: document.getElementById("editExpiry").value
  };

  try {
    const response = await fetch("http://localhost:8989/edit", {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(updatedStock)
    });

    if (!response.ok) throw new Error("Failed to update stock");

    alert("Stock updated successfully!");
    loadStock(); // reload table
  } catch (error) {
    console.error("Error saving stock:", error);
    alert("Could not save stock changes.");
  }
 
  
}
 //deleting the stock
async function deleteStock(stockId, button) {
  if (!confirm("Are you sure you want to delete this stock?")) return;

  try {
    const response = await fetch(`http://localhost:8989/deleteStock/${stockId}`, {
      method: "DELETE",
    });

    if (!response.ok) {
      throw new Error("Failed to delete stock");
    }

    const result = await response.text();
    alert(result);

    // Remove the deleted row from table
    const row = button.closest("tr");
    row.remove();

    // Reload updated stock data (optional)
    loadStock();

  } catch (error) {
    console.error("Error deleting stock:", error);
    alert("Something went wrong while deleting the stock.");
  }
}
//dashboard
// Load dashboard data
async function loadDashboard() {
  const pharmacyId = sessionStorage.getItem("pharmacyId");
  if (!pharmacyId) {
    alert("Pharmacy ID not found. Please login again.");
    return;
  }

  try {
    const response = await fetch(`http://localhost:8989/${pharmacyId}/counts`);
    if (!response.ok) throw new Error("Failed to fetch dashboard data");

    const data = await response.json();
    console.log("Dashboard data:", data);

    // Update dashboard cards
    document.getElementById("totalMedicines").textContent = `Totalmedicines:${data.totalMedicines}`;
    document.getElementById("lowStock").textContent = `LowStack:${data.lowStock}`;
    document.getElementById("expiredMedicines").textContent =`ExpiredDate:${data.expired}`;

    //Optionally load open/close times (from pharmacy profile)
    //loadPharmacyTimings();

  } catch (error) {
    console.error("Error loading dashboard:", error);
    alert("Could not load dashboard data. Check console for details.");
  }
}
function showDashBoardSection() {
  showSection("dashboard");
  loadDashboard();
}



// Toggle Edit Mode for Profile
function toggleEditMode() {
  const inputs = document.querySelectorAll('#profileForm input, #profileForm textarea');
  const updateBtn = document.getElementById('updateBtn');
  
  inputs.forEach(input => {
    if (input.hasAttribute('readonly')) {
      input.removeAttribute('readonly');
      input.style.background = "#fff";
    } else {
      input.setAttribute('readonly', true);
      input.style.background = "#f9f9f9";
    }
  });

  updateBtn.classList.toggle('hidden');
}

// // Profile Picture Upload Preview
// const uploadInput = document.getElementById('uploadPic');
// const profileImage = document.getElementById('profileImage');

// uploadInput.addEventListener('change', function () {
//   const file = this.files[0];
//   if (file) {
//     const reader = new FileReader();
//     reader.onload = function (e) {
//       profileImage.src = e.target.result;
//     };
//     reader.readAsDataURL(file);
//   }

// });
//profile section loading
async function loadProfile() {
  const pharmacyId = sessionStorage.getItem("pharmacyId"); // saved during login

  try {
    const response = await fetch(`http://localhost:8989/profile/${pharmacyId}`);
    if (!response.ok) {
      alert("Failed to load profile ❌");
      return;
    }

    const data = await response.json();
    const name=sessionStorage.setItem(data.name);

    // Fill the form
    document.getElementById("pharmacyName").textContent = "🏥 " + data.name;
    document.getElementById("license").value = data.licenseId;
    document.getElementById("address").value = data.address;
    document.getElementById("phone").value = data.phone;
    document.getElementById("email").value = data.email;
    document.getElementById("openTime").value = data.openingTime;
    document.getElementById("closeTime").value = data.closingTime;

    // Store current profile data in memory to compare later
    window.currentProfile = data;

  } catch (error) {
    console.error("Error loading profile:", error);
  }
}

function showProfileSection() {
  
  document.querySelectorAll(".content-section").forEach(section => section.classList.add("hidden"));

 
  document.getElementById("profile").classList.remove("hidden");

  
  loadProfile();
}

// Handle Profile Update
document.getElementById("profileForm").addEventListener("submit", async function (event) {
  event.preventDefault();

  const pharmacyId = sessionStorage.getItem("pharmacyId");
  const oldData = window.currentProfile;

  // New or changed data from inputs
  const newData = {
    name: document.getElementById("pharmacyName").textContent.replace("🏥 ", "").trim(),
    phone: document.getElementById("phone").value,
    email: document.getElementById("email").value,
    address: document.getElementById("address").value,
    city: oldData.city, // no input, so reuse
    openingTime: document.getElementById("openTime").value,
    closingTime: document.getElementById("closeTime").value
  };

  // Prepare clean payload (no password, license, or id)
  const payload = {
    name: newData.name || oldData.name,
    phone: Number(newData.phone || oldData.phone),
    email: newData.email || oldData.email,
    address: newData.address || oldData.address,
    city: newData.city || oldData.city,
    openingTime: newData.openingTime || oldData.openingTime,
    closingTime: newData.closingTime || oldData.closingTime
  };

  try {
    const response = await fetch(`http://localhost:8989/profile/editing/${pharmacyId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    if (!response.ok) {
      alert("Failed to update profile ❌");
      return;
    }

    const updatedProfile = await response.json();
    alert("Profile updated successfully ✅");

    // Update displayed values
    document.getElementById("pharmacyName").textContent = "🏥 " + updatedProfile.name;
    document.getElementById("address").value = updatedProfile.address;
    document.getElementById("phone").value = updatedProfile.phone;
    document.getElementById("email").value = updatedProfile.email;
    document.getElementById("openTime").value = updatedProfile.openingTime;
    document.getElementById("closeTime").value = updatedProfile.closingTime;

    // Save latest data in memory
    window.currentProfile = updatedProfile;

    // Lock fields again
    document.querySelectorAll("#profileForm input, #profileForm textarea").forEach(input => {
      input.setAttribute("readonly", true);
      input.style.background = "#f9f9f9";
    });
    document.getElementById("updateBtn").classList.add("hidden");

  } catch (error) {
    console.error("Error updating profile:", error);
    alert("Something went wrong ❌");
  }
});
//new logic for dash board

