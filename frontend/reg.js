// -------------------- Global Variables --------------------
let isOtpVerified = false;
let timer;

// -------------------- Toggle Pharmacy Fields --------------------
function toggleForm() {
    const role = document.getElementById("role").value;
    const pharmacyFields = document.getElementById("pharmacy-fields");
    pharmacyFields.classList.toggle("hidden", role !== "pharmacy");
}

// -------------------- Show OTP Page & Generate OTP --------------------
function showOtpPage() {
    const email = document.getElementById("email").value;
    if (!email) {
        alert("Please enter email first!");
        return;
    }

    fetch("http://localhost:8989/otp/generate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email })
    })
    .then(res => res.text())  // ✅ use text instead of json
    .then(result => {
        console.log("Generate OTP Response:", result);

        if(result.toLowerCase().includes("otp sent")) {
            alert(result); // shows: OTP sent to user@gmail.com
            document.getElementById("registerPage").classList.add("hidden");
            document.getElementById("otpPage").classList.remove("hidden");
            startTimer();
        } else {
            alert("Failed to send OTP: " + result);
        }
    })
    .catch(err => {
        console.error("Fetch error:", err);
        alert("Error sending OTP!");
    });
}


// -------------------- Go Back to Registration --------------------
function goBack() {
    document.getElementById("otpPage").classList.add("hidden");
    document.getElementById("registerPage").classList.remove("hidden");
}

// -------------------- OTP Auto-Move --------------------
function moveNext(input, index) {
    let inputs = document.querySelectorAll(".otp-input");
    if (input.value && index < inputs.length - 1) {
        inputs[index + 1].focus();
    }
}

// -------------------- Verify OTP --------------------
//let isOtpVerified = false;
// -------------------- Verify OTP --------------------
function verifyOtp() {
    const email = document.getElementById("email").value;
    let otp = "";
    document.querySelectorAll(".otp-input").forEach(inp => otp += inp.value);

    if (otp.length !== 6) {
        alert("Enter full 6-digit OTP");
        return;
    }

    fetch("http://localhost:8989/otp/validate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email, otp: otp })
    })
    .then(res => res.text())
    .then(result => {
        console.log("Validate OTP Response:", result);

        if (result.toLowerCase().includes("verified")) {
            alert(result); // "OTP verified successfully!"
            isOtpVerified = true;

            // ✅ Hide Verify Email button after success
            const btn = document.getElementById("verifyEmailBtn");
            if (btn) {
                btn.style.display = "none";
            }

            // ✅ Disable OTP input fields
            document.querySelectorAll(".otp-input").forEach(inp => inp.disabled = true);

            goBack(); // return to registration page
        } else {
            alert(result); // "Invalid or expired OTP!"
        }
    })
    .catch(err => {
        console.error("Fetch error:", err);
        alert("OTP verification failed!");
    });
}


// -------------------- OTP Timer --------------------
function startTimer() {
    let timeLeft = 300;
    const timerElement = document.getElementById("timer");
    const resendBtn = document.querySelector(".resend-btn");

    resendBtn.disabled = true;
    clearInterval(timer);

    timer = setInterval(() => {
        if(timeLeft <= 0){
            clearInterval(timer);
            timerElement.textContent = "0";
            resendBtn.disabled = false;
        } else {
            timerElement.textContent = timeLeft;
            timeLeft--;
        }
    }, 1000);
}

// -------------------- Resend OTP --------------------
function resendOtp() {
    startTimer();
    const email = document.getElementById("email").value;

    fetch("http://localhost:8989/otp/generate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email })
    })
    .then(res => res.text())
    .then(result => {
        if(result.success){
            alert("New OTP sent!");
        } else {
            alert("Failed to resend OTP!");
        }
    })
    .catch(err => {
        console.error(err);
        alert("Error resending OTP!");
    });
}
async function getCoordinates(address) {
  const url = `https://nominatim.openstreetmap.org/search?format=json&addressdetails=1&limit=1&q=${encodeURIComponent(address)}`;

  try {
    const response = await fetch(url, {
      headers: {
          headers: {
    "User-Agent": "EmergencyHospitalLocator/1.0 (chaithanya@example.com)",
    "Accept-Language": "en"
  }

      }
    });
    const data = await response.json();

    console.log("🌍 Nominatim API response:", data);

    if (data && data.length > 0) {
      return {
        latitude: parseFloat(data[0].lat),
        longitude: parseFloat(data[0].lon)
      };
    } else {
      console.warn("⚠️ No coordinates found for:", address);
      return { latitude: null, longitude: null };
    }
  } catch (err) {
    console.error("Error fetching coordinates:", err);
    return { latitude: null, longitude: null };
  }
}


// -------------------- Registration Submit --------------------
document.getElementById("registerForm").addEventListener("submit", async function(e){
    e.preventDefault();

    if(!isOtpVerified){
        alert("Please verify OTP before registering!");
        return;
    }

    const role = document.getElementById("role").value;
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const phone = document.getElementById("phone").value;
    const passwordHashed = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirm-password").value;

    if(passwordHashed !== confirmPassword){
        alert("Passwords do not match!");
        return;
    }

    let data = { role, name, email, phone, passwordHashed };

    if(role === "pharmacy"){
        data.pharmacyName = document.getElementById("pharma-name").value;
        data.licenseId = document.getElementById("pharma-license").value;
        const ruralValue = document.getElementById("is-rural").value; // "Yes" or "No"
    const isRural = ruralValue.toLowerCase() === "yes" ? 1 : 0;
    data.isRural = isRural; // ✅ use converted value

        //data.isRural = document.getElementById("is-rural").value;
        data.openingTime = document.getElementById("opening-time").value;
        data.closingTime = document.getElementById("closing-time").value;
        // data.address = {
        //     hno: document.getElementById("hno").value,
        //     street: document.getElementById("street").value,
        //     area: document.getElementById("area").value,
        //     city: document.getElementById("city").value,
        //     state: document.getElementById("state").value,
        //     pincode: document.getElementById("pincode").value
        // };
      // ✅ Combine full address into a variable
const address = `${document.getElementById("hno").value}, ` +
                `${document.getElementById("street").value}, ` +
                `${document.getElementById("area").value}, ` +
                `${document.getElementById("city").value}, ` +
                `${document.getElementById("state").value} - ` +
                `${document.getElementById("pincode").value}`;

data.address = address;
data.city = document.getElementById("city").value;

        const { latitude, longitude } = await getCoordinates(address);
  data.latitude = latitude;
  data.longitude = longitude;

  console.log("📍 Coordinates fetched:", latitude, longitude);
}

console.log("📦 Final data sent to backend:", data);

    fetch("http://localhost:8989/api/register", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify(data)
})
.then(async (res) => {
  const text = await res.text(); // get plain text
  return text;
})
.then(result => {
  alert(result); // "Registration successful!"
  window.location.href = "login.html";
})
.catch(err => {
  console.error(err);
  alert("Registration failed!");
});

});
