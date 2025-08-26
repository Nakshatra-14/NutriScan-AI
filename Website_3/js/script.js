// Toggle mobile navigation
document.addEventListener('DOMContentLoaded', function() {
    const hamburger = document.querySelector('.hamburger');
    const navLinks = document.querySelector('.nav-links');

    if (hamburger) {
        hamburger.addEventListener('click', () => {
            navLinks.classList.toggle('active');
            hamburger.classList.toggle('active');
        });
    }

    // Check if user is logged in
    checkLoginStatus();
});

// Function to check login status
function checkLoginStatus() {
    const userWelcome = document.getElementById('userWelcome');
    const username = localStorage.getItem('username');
    
    if (username && userWelcome) {
        userWelcome.innerHTML = `Welcome, ${username}!`;
        userWelcome.style.display = 'block';
        
        // Update login button to logout
        const loginBtn = document.querySelector('.login-btn');
        if (loginBtn) {
            loginBtn.textContent = 'Logout';
            loginBtn.href = '#';
            loginBtn.addEventListener('click', (e) => {
                e.preventDefault();
                logout();
            });
        }
    } else if (userWelcome) {
        // Hide welcome message if not logged in
        userWelcome.style.display = 'none';
    }
}

// Function to handle login
function login(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    
    if (username && password) {
        // In a real app, you would validate credentials with a server
        // For demo purposes, we'll just store the username in localStorage
        localStorage.setItem('username', username);
        localStorage.setItem('isLoggedIn', 'true');
        
        // Redirect to home page
        window.location.href = 'index.html';
    } else {
        document.getElementById('loginError').textContent = 'Please enter both username and password';
    }
}

// Function to handle logout
function logout() {
    localStorage.removeItem('username');
    localStorage.removeItem('isLoggedIn');
    window.location.href = 'index.html';
}

// Barcode scanner functionality
function initBarcodeScanner() {
    const video = document.getElementById('video');
    const canvas = document.getElementById('canvas');
    const scanBtn = document.getElementById('scanBtn');
    const scanResult = document.getElementById('scanResult');
    const loadingIndicator = document.getElementById('loadingIndicator');
    const resultContainer = document.getElementById('resultContainer');
    
    if (!video || !canvas || !scanBtn) return;
    
    let scanning = false;
    
    // Check if the browser supports getUserMedia
    if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        scanBtn.addEventListener('click', function() {
            if (scanning) {
                stopScanning();
                scanBtn.textContent = 'Start Scanning';
                scanning = false;
            } else {
                startScanning();
                scanBtn.textContent = 'Stop Scanning';
                scanning = true;
            }
        });
    } else {
        scanResult.textContent = 'Sorry, your browser does not support camera access.';
        scanBtn.disabled = true;
    }
    
    function startScanning() {
        navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } })
            .then(function(stream) {
                video.srcObject = stream;
                video.setAttribute('playsinline', true);
                video.play();
                requestAnimationFrame(tick);
            })
            .catch(function(err) {
                console.error('Error accessing the camera: ', err);
                scanResult.textContent = 'Error accessing the camera. Please make sure you have granted camera permissions.';
            });
    }
    
    function stopScanning() {
        if (video.srcObject) {
            const tracks = video.srcObject.getTracks();
            tracks.forEach(track => track.stop());
            video.srcObject = null;
        }
    }
    
    function tick() {
        if (video.readyState === video.HAVE_ENOUGH_DATA && scanning) {
            const context = canvas.getContext('2d');
            canvas.height = video.videoHeight;
            canvas.width = video.videoWidth;
            context.drawImage(video, 0, 0, canvas.width, canvas.height);
            
            // In a real app, you would use a barcode scanning library here
            // For demo purposes, we'll simulate a scan after a few seconds
            if (!window.scanTimeout) {
                window.scanTimeout = setTimeout(() => {
                    simulateBarcodeDetection();
                    window.scanTimeout = null;
                }, 3000);
            }
            
            requestAnimationFrame(tick);
        } else if (scanning) {
            requestAnimationFrame(tick);
        }
    }
    
    function simulateBarcodeDetection() {
        if (!scanning) return;
        
        // Simulate loading
        loadingIndicator.style.display = 'block';
        scanResult.textContent = 'Processing...';
        
        setTimeout(() => {
            // Simulate barcode detection result
            const mockBarcodes = [
                { code: '8901234567890', product: 'Whole Grain Cereal', nutrition: { protein: '8g', carbs: '22g', fat: '2g', sugar: '5g', fiber: '6g' }, gi: 'Low (45)', nutriscore: 'A', ecoscore: 'B' },
                { code: '7654321098765', product: 'Chocolate Cookies', nutrition: { protein: '3g', carbs: '35g', fat: '15g', sugar: '22g', fiber: '1g' }, gi: 'High (75)', nutriscore: 'D', ecoscore: 'C' },
                { code: '5678901234567', product: 'Fruit Yogurt', nutrition: { protein: '5g', carbs: '18g', fat: '3g', sugar: '15g', fiber: '0g' }, gi: 'Medium (55)', nutriscore: 'B', ecoscore: 'B' }
            ];
            
            const randomBarcode = mockBarcodes[Math.floor(Math.random() * mockBarcodes.length)];
            
            // Display the result
            loadingIndicator.style.display = 'none';
            resultContainer.style.display = 'block';
            
            document.getElementById('productName').textContent = randomBarcode.product;
            document.getElementById('barcodeValue').textContent = randomBarcode.code;
            
            document.getElementById('proteinValue').textContent = randomBarcode.nutrition.protein;
            document.getElementById('carbsValue').textContent = randomBarcode.nutrition.carbs;
            document.getElementById('fatValue').textContent = randomBarcode.nutrition.fat;
            document.getElementById('sugarValue').textContent = randomBarcode.nutrition.sugar;
            document.getElementById('fiberValue').textContent = randomBarcode.nutrition.fiber;
            
            document.getElementById('giValue').textContent = randomBarcode.gi;
            document.getElementById('nutriscoreValue').textContent = randomBarcode.nutriscore;
            document.getElementById('ecoscoreValue').textContent = randomBarcode.ecoscore;
            
            // Set health label based on nutriscore
            let healthLabel = 'Bad';
            let healthLabelClass = 'health-bad';
            
            if (randomBarcode.nutriscore === 'A' || randomBarcode.nutriscore === 'B') {
                healthLabel = 'Good';
                healthLabelClass = 'health-good';
            } else if (randomBarcode.nutriscore === 'C') {
                healthLabel = 'OK';
                healthLabelClass = 'health-ok';
            }
            
            const healthLabelElement = document.getElementById('healthLabel');
            healthLabelElement.textContent = healthLabel;
            healthLabelElement.className = healthLabelClass;
            
            // Generate random tips
            const tips = [
                'Try to consume this product in moderation due to its sugar content.',
                'This product is a good source of fiber, which helps with digestion.',
                'Consider alternatives with lower glycemic index if you have diabetes.',
                'This product contains essential proteins for muscle maintenance.',
                'Look for products with higher fiber content for better gut health.',
                'Consume this after physical activity to help with energy replenishment.'
            ];
            
            const tipsContainer = document.getElementById('nutritionTips');
            tipsContainer.innerHTML = '';
            
            // Select 5 random tips
            const selectedTips = [];
            while (selectedTips.length < 5) {
                const randomTip = tips[Math.floor(Math.random() * tips.length)];
                if (!selectedTips.includes(randomTip)) {
                    selectedTips.push(randomTip);
                }
            }
            
            selectedTips.forEach(tip => {
                const tipElement = document.createElement('li');
                tipElement.textContent = tip;
                tipsContainer.appendChild(tipElement);
            });
            
            // Stop scanning after successful detection
            stopScanning();
            scanBtn.textContent = 'Scan Again';
            scanning = false;
        }, 2000);
    }
}

// Initialize barcode scanner if on the barcode page
if (window.location.pathname.includes('barcode.html')) {
    window.addEventListener('load', initBarcodeScanner);
}

// Add event listener for login form
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', login);
    }
});