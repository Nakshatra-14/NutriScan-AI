document.addEventListener('DOMContentLoaded', function() {
    // Camera Button Functionality
    const cameraButton = document.getElementById('camera-button');
    const cameraModal = document.getElementById('camera-modal');
    const cameraFeed = document.getElementById('camera-feed');
    const closeCamera = document.querySelector('.close-camera');
    const scanResult = document.getElementById('scan-result');
    const barcodeValue = document.getElementById('barcode-value');
    
    let stream = null;
    
    // Open camera modal and start camera when button is clicked
    if (cameraButton) {
        cameraButton.addEventListener('click', function() {
            cameraModal.classList.add('show');
            startCamera();
        });
    }
    
    // Close camera modal and stop camera
    if (closeCamera) {
        closeCamera.addEventListener('click', function() {
            cameraModal.classList.remove('show');
            stopCamera();
        });
    }
    
    // Store camera permission state
    let cameraPermissionGranted = false;
    
    // Start camera and barcode scanner
    function startCamera() {
        // If permission was already granted and we have an active stream, use it
        if (cameraPermissionGranted && stream && stream.active) {
            cameraFeed.srcObject = stream;
            cameraFeed.play();
            startBarcodeScanner();
            return;
        }
        
        // Check if browser supports getUserMedia
        if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
            navigator.mediaDevices.getUserMedia({ video: { facingMode: "environment" } })
                .then(function(mediaStream) {
                    // Store permission state
                    cameraPermissionGranted = true;
                    stream = mediaStream;
                    cameraFeed.srcObject = mediaStream;
                    cameraFeed.onloadedmetadata = function(e) {
                        cameraFeed.play();
                        startBarcodeScanner();
                    };
                })
                .catch(function(err) {
                    console.error("Error accessing camera: ", err);
                    alert("Error accessing camera. Please make sure you've granted camera permissions.");
                    cameraModal.classList.remove('show');
                });
        } else {
            alert("Sorry, your browser doesn't support camera access.");
            cameraModal.classList.remove('show');
        }
    }
    
    // Stop camera and barcode scanner
    function stopCamera() {
        // If we have camera permission, just pause the video but keep the stream
        // This prevents having to request permission again
        if (cameraPermissionGranted && stream) {
            // Just pause the video element instead of stopping tracks
            cameraFeed.pause();
            
            // Only stop Quagga processing
            if (Quagga) {
                Quagga.stop();
            }
        } else {
            // If no permission yet, fully stop everything
            if (stream) {
                stream.getTracks().forEach(track => {
                    track.stop();
                });
            }
            
            if (Quagga) {
                Quagga.stop();
            }
        }
        
        // Hide scan result
        scanResult.classList.remove('show');
    }
    
    // Start barcode scanner using QuaggaJS
    function startBarcodeScanner() {
        if (typeof Quagga === 'undefined') {
            console.error("Quagga is not loaded. Make sure the script is included.");
            return;
        }
        
        Quagga.init({
            inputStream: {
                name: "Live",
                type: "LiveStream",
                target: cameraFeed,
                constraints: {
                    width: 640,
                    height: 480,
                    facingMode: "environment"
                },
            },
            decoder: {
                readers: [
                    "ean_reader",
                    "ean_8_reader",
                    "upc_reader",
                    "upc_e_reader",
                    "code_128_reader",
                    "code_39_reader",
                    "code_93_reader",
                    "codabar_reader"
                ],
                debug: {
                    showCanvas: true,
                    showPatches: true,
                    showFoundPatches: true,
                    showSkeleton: true,
                    showLabels: true,
                    showPatchLabels: true,
                    showRemainingPatchLabels: true,
                    boxFromPatches: {
                        showTransformed: true,
                        showTransformedBox: true,
                        showBB: true
                    }
                }
            },
        }, function(err) {
            if (err) {
                console.error("Error initializing Quagga: ", err);
                return;
            }
            
            console.log("Barcode scanner started");
            Quagga.start();
        });
        
        // Process detected barcodes
        Quagga.onDetected(function(result) {
            if (result && result.codeResult && result.codeResult.code) {
                const code = result.codeResult.code;
                console.log("Barcode detected: ", code);
                
                // Display the barcode value
                barcodeValue.textContent = code;
                scanResult.classList.add('show');
                
                // You can add additional functionality here, such as:
                // - Looking up product information based on the barcode
                // - Adding the scanned item to the user's food log
                // - Displaying nutritional information
                
                // For demo purposes, we'll just show the barcode for 3 seconds
                setTimeout(function() {
                    scanResult.classList.remove('show');
                }, 3000);
            }
        });
    }
    // Sign Up Modal Functionality
    const signupModal = document.getElementById('signup-modal');
    const signupButtons = document.querySelectorAll('.btn-primary');
    const closeModal = document.querySelector('.close-modal');
    const signupForm = document.getElementById('signup-form');
    const updateProfileBtn = document.getElementById('update-profile-btn');
    const heroTitle = document.querySelector('.hero-content h1');
    
    // Open modal when Sign Up or Get Started buttons are clicked
    signupButtons.forEach(button => {
        if (button.textContent.includes('Sign Up Free') || button.textContent.includes('Get Started Free')) {
            button.addEventListener('click', function(e) {
                e.preventDefault();
                signupModal.classList.add('show');
            });
        }
    });
    
    // Close modal when X is clicked or when clicking outside the modal
    closeModal.addEventListener('click', function() {
        signupModal.classList.remove('show');
    });
    
    window.addEventListener('click', function(e) {
        if (e.target === signupModal) {
            signupModal.classList.remove('show');
        }
    });
    
    // Form validation
    const formInputs = signupForm.querySelectorAll('input');
    
    formInputs.forEach(input => {
        input.addEventListener('input', validateForm);
    });
    
    function validateForm() {
        let isValid = true;
        
        // Check if all fields are filled
        formInputs.forEach(input => {
            if (!input.value.trim()) {
                isValid = false;
            }
        });
        
        // Enable/disable submit button based on validation
        updateProfileBtn.disabled = !isValid;
    }
    
    // Handle form submission
    signupForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Get form data
        const formData = {
            name: document.getElementById('name').value,
            age: document.getElementById('age').value,
            phone: document.getElementById('phone').value,
            email: document.getElementById('email').value
        };
        
        // Store user data in localStorage (simulating database storage)
        localStorage.setItem('userData', JSON.stringify(formData));
        
        // Close modal
        signupModal.classList.remove('show');
        
        // Update greeting with time-based message
        updateGreeting();
    });
    
    // Check if user data exists and update greeting
    function updateGreeting() {
        const userData = JSON.parse(localStorage.getItem('userData'));
        
        if (userData && userData.name) {
            const hour = new Date().getHours();
            let greeting;
            let quote;
            
            // Set greeting based on time of day
            if (hour >= 5 && hour < 12) {
                greeting = `Good Morning, ${userData.name}`;
                quote = "Start your day with nutritious choices!";
            } else if (hour >= 12 && hour < 18) {
                greeting = `Good Afternoon, ${userData.name}`;
                quote = "Fuel your afternoon with balanced nutrition!";
            } else if (hour >= 18 && hour < 22) {
                greeting = `Good Evening, ${userData.name}`;
                quote = "Wind down with mindful eating choices!";
            } else {
                greeting = `Good Night, ${userData.name}`;
                quote = "Rest well for a healthy tomorrow!";
            }
            
            // Update hero title and add quote
            if (heroTitle) {
                // Format greeting with user name in green
                const userName = userData.name;
                const greetingText = greeting.replace(userName, `<span class="user-name">${userName}</span>`);
                heroTitle.innerHTML = greetingText;
                
                // Create and add quote element if it doesn't exist
                let quoteElement = heroTitle.nextElementSibling;
                if (quoteElement && !quoteElement.classList.contains('user-quote')) {
                    quoteElement = document.createElement('p');
                    quoteElement.classList.add('user-quote');
                    heroTitle.parentNode.insertBefore(quoteElement, heroTitle.nextElementSibling);
                }
                
                if (quoteElement) {
                    quoteElement.textContent = quote;
                }
            }
        }
    }
    
    // Check for existing user data on page load
    updateGreeting();

    // Glycemic Index card redirect functionality
    const glycemicCard = document.querySelector('.learn-card.glycemic');
    
    if (glycemicCard) {
        glycemicCard.addEventListener('click', function() {
            window.location.href = 'glycemic-index.html';
        });
        
        // Add cursor pointer style to indicate it's clickable
        glycemicCard.style.cursor = 'pointer';
    }
    
    // FAQ accordion functionality
    const faqItems = document.querySelectorAll('.faq-item');
    
    faqItems.forEach(item => {
        const question = item.querySelector('.faq-question');
        
        question.addEventListener('click', () => {
            // Toggle active class on the clicked item
            item.classList.toggle('active');
            
            // Close other items when one is opened (optional)
            faqItems.forEach(otherItem => {
                if (otherItem !== item) {
                    otherItem.classList.remove('active');
                }
            });
        });
    });
    
    // Theme switching functionality
    const darkModeToggle = document.getElementById('dark-mode-toggle');
    const htmlElement = document.documentElement;
    
    // Check if user previously set a theme preference
    const currentTheme = localStorage.getItem('theme');
    if (currentTheme) {
        htmlElement.setAttribute('data-theme', currentTheme);
        if (darkModeToggle && currentTheme === 'dark') {
            const darkModeIcon = darkModeToggle.querySelector('i');
            if (darkModeIcon) {
                darkModeIcon.classList.remove('fa-moon');
                darkModeIcon.classList.add('fa-sun');
            }
        }
    }
    
    // Toggle theme when button is clicked
    if (darkModeToggle) {
        darkModeToggle.addEventListener('click', function() {
            const icon = this.querySelector('i');
            if (!icon) return; // Ensure icon exists
            
            const currentDataTheme = htmlElement.getAttribute('data-theme');
            
            if (currentDataTheme === 'dark') {
                htmlElement.removeAttribute('data-theme');
                icon.classList.remove('fa-sun');
                icon.classList.add('fa-moon');
                localStorage.setItem('theme', 'light');
            } else {
                htmlElement.setAttribute('data-theme', 'dark');
                icon.classList.remove('fa-moon');
                icon.classList.add('fa-sun');
                localStorage.setItem('theme', 'dark');
            }
        });
    }
    
    // Mobile menu toggle
    const hamburger = document.querySelector('.hamburger');
    const navLinks = document.querySelector('.nav-links');
    const navButtons = document.querySelector('.nav-buttons');
    
    if (hamburger) {
        hamburger.addEventListener('click', function() {
            hamburger.classList.toggle('active');
            navLinks.classList.toggle('active');
            navButtons.classList.toggle('active');
            
            // Animate hamburger bars
            const bars = hamburger.querySelectorAll('.bar');
            bars.forEach(bar => bar.classList.toggle('animate'));
        });
    }
    
    // Add active class to nav links based on scroll position
    const sections = document.querySelectorAll('section[id]');
    const navItems = document.querySelectorAll('.nav-links a');
    const homeLink = document.querySelector('.nav-links a[href="#"]');
    
    function highlightNavItem() {
        const scrollPosition = window.scrollY;
        let currentSection = '';
        
        // Set Home as active by default when at the top of the page
        if (scrollPosition < 100) {
            navItems.forEach(item => item.classList.remove('active'));
            if (homeLink) homeLink.classList.add('active');
            return;
        }
        
        // Find the current section based on scroll position
        sections.forEach(section => {
            const sectionTop = section.offsetTop - 100;
            const sectionHeight = section.offsetHeight;
            const sectionId = section.getAttribute('id');
            
            if (scrollPosition >= sectionTop && scrollPosition < sectionTop + sectionHeight) {
                currentSection = sectionId;
            }
        });
        
        // Update active class on nav items
        if (currentSection) {
            navItems.forEach(item => {
                item.classList.remove('active');
                if (item.getAttribute('href') === `#${currentSection}`) {
                    item.classList.add('active');
                }
            });
        }
    }
    
    // Run on page load to set initial active state
    highlightNavItem();
    
    // Update on scroll
    window.addEventListener('scroll', highlightNavItem);
    
    // Smooth scroll for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            // Remove active class from all links
            navItems.forEach(item => item.classList.remove('active'));
            
            // Add active class to clicked link
            this.classList.add('active');
            
            if (this.getAttribute('href') !== '#') {
                e.preventDefault();
                
                const targetId = this.getAttribute('href');
                const targetElement = document.querySelector(targetId);
                
                if (targetElement) {
                    // Close mobile menu if open
                    if (hamburger && hamburger.classList.contains('active')) {
                        hamburger.click();
                    }
                    
                    window.scrollTo({
                        top: targetElement.offsetTop - 80,
                        behavior: 'smooth'
                    });
                }
            }
        });
    });
    
    // Testimonial slider (simple version)
    const testimonialCards = document.querySelectorAll('.testimonial-card');
    let currentTestimonial = 0;
    
    function showTestimonials() {
        // This is a simplified version. For a real slider, you'd want to implement
        // proper controls and transitions.
        testimonialCards.forEach((card, index) => {
            if (window.innerWidth <= 768) {
                // On mobile, show only the current testimonial
                card.style.display = index === currentTestimonial ? 'block' : 'none';
            } else {
                // On desktop, show all testimonials
                card.style.display = 'block';
            }
        });
    }
    
    // Initialize testimonial display
    if (testimonialCards.length > 0) {
        showTestimonials();
        
        // For mobile: auto-rotate testimonials every 5 seconds
        if (window.innerWidth <= 768) {
            setInterval(() => {
                currentTestimonial = (currentTestimonial + 1) % testimonialCards.length;
                showTestimonials();
            }, 5000);
        }
        
        // Update testimonial display on window resize
        window.addEventListener('resize', showTestimonials);
    }
    
    // Add animation classes when elements come into view
    const animatedElements = document.querySelectorAll('.feature-card, .step, .pricing-card');
    
    function checkIfInView() {
        animatedElements.forEach(element => {
            const elementTop = element.getBoundingClientRect().top;
            const elementVisible = 150;
            
            if (elementTop < window.innerHeight - elementVisible) {
                element.classList.add('visible');
            }
        });
    }
    
    window.addEventListener('scroll', checkIfInView);
    checkIfInView(); // Check on initial load
    
    // Add CSS for animations
    const style = document.createElement('style');
    style.textContent = `
        .feature-card, .step, .pricing-card {
            opacity: 0;
            transform: translateY(20px);
            transition: opacity 0.6s ease, transform 0.6s ease;
        }
        
        .feature-card.visible, .step.visible, .pricing-card.visible {
            opacity: 1;
            transform: translateY(0);
        }
        
        .hamburger.active .bar:nth-child(1) {
            transform: translateY(8px) rotate(45deg);
        }
        
        .hamburger.active .bar:nth-child(2) {
            opacity: 0;
        }
        
        .hamburger.active .bar:nth-child(3) {
            transform: translateY(-8px) rotate(-45deg);
        }
        
        @media (max-width: 768px) {
            .nav-links.active, .nav-buttons.active {
                display: flex;
                flex-direction: column;
                position: absolute;
                top: 70px;
                left: 0;
                right: 0;
                background-color: white;
                box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
                padding: 20px;
                z-index: 999;
            }
            
            .nav-links.active {
                gap: 20px;
            }
            
            .nav-buttons.active {
                padding-top: 0;
                gap: 10px;
            }
        }
    `;
    document.head.appendChild(style);
});