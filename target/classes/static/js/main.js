// Main JavaScript functionality for RideShare app

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    // Set minimum date to today for date inputs
    const dateInputs = document.querySelectorAll('input[type="date"]');
    const today = new Date().toISOString().split('T')[0];
    dateInputs.forEach(input => {
        input.min = today;
    });

    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Handle quick search form on homepage
    const quickSearchForm = document.getElementById('quickSearchForm');
    if (quickSearchForm) {
        quickSearchForm.addEventListener('submit', handleQuickSearch);
    }

    // Handle main search form
    const searchForm = document.getElementById('searchForm');
    if (searchForm) {
        searchForm.addEventListener('submit', handleSearch);
    }

    // Handle login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    // Handle registration form
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }

    // Handle publish ride form
    const publishRideForm = document.getElementById('publishRideForm');
    if (publishRideForm) {
        publishRideForm.addEventListener('submit', handlePublishRide);
    }

    // Load initial data based on page
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    switch (currentPage) {
        case 'search.html':
            loadSearchResults();
            break;
        case 'dashboard.html':
            if (authService.requireAuth()) loadDashboard();
            break;
        case 'reservations.html':
            if (authService.requireAuth()) loadReservations();
            break;
        case 'my-rides.html':
            if (authService.requireDriver()) loadMyRides();
            break;
        case 'profile.html':
            if (authService.requireAuth()) loadProfile();
            break;
    }
});

// Quick search from homepage
async function handleQuickSearch(event) {
    event.preventDefault();
    
    const origin = document.getElementById('origin').value;
    const destination = document.getElementById('destination').value;
    const date = document.getElementById('date').value;
    
    // Build search URL
    const params = new URLSearchParams();
    if (origin) params.append('origin', origin);
    if (destination) params.append('destination', destination);
    if (date) params.append('date', date);
    
    window.location.href = `search.html?${params.toString()}`;
}

// Search rides
async function handleSearch(event) {
    event.preventDefault();
    await loadSearchResults();
}

async function loadSearchResults() {
    const resultsContainer = document.getElementById('searchResults');
    const loadingDiv = document.getElementById('loading');
    
    if (!resultsContainer) return;

    // Show loading
    if (loadingDiv) loadingDiv.style.display = 'block';
    resultsContainer.innerHTML = '';

    try {
        // Get search parameters
        const urlParams = new URLSearchParams(window.location.search);
        const formParams = new URLSearchParams();
        
        // From URL or form
        const origin = urlParams.get('origin') || document.getElementById('searchOrigin')?.value || '';
        const destination = urlParams.get('destination') || document.getElementById('searchDestination')?.value || '';
        const date = urlParams.get('date') || document.getElementById('searchDate')?.value || '';

        if (origin) formParams.append('origin', origin);
        if (destination) formParams.append('destination', destination);
        if (date) formParams.append('date', date + 'T00:00:00');

        // Fill form fields if they exist
        if (document.getElementById('searchOrigin')) document.getElementById('searchOrigin').value = origin;
        if (document.getElementById('searchDestination')) document.getElementById('searchDestination').value = destination;
        if (document.getElementById('searchDate')) document.getElementById('searchDate').value = date;

        const response = await fetch(`/api/rides/search?${formParams.toString()}`);
        
        if (!response.ok) {
            throw new Error('Failed to fetch rides');
        }

        const rides = await response.json();
        displaySearchResults(rides);
        
    } catch (error) {
        console.error('Search error:', error);
        showAlert('Неуспешно зареждане на резултати от търсенето. Моля, опитайте отново.', 'danger', 'searchAlerts');
    } finally {
        if (loadingDiv) loadingDiv.style.display = 'none';
    }
}

function displaySearchResults(rides) {
    const resultsContainer = document.getElementById('searchResults');
    if (!resultsContainer) return;

    if (rides.length === 0) {
        resultsContainer.innerHTML = `
            <div class="no-results">
                <i class="bi bi-search"></i>
                <h4>Няма намерени пътувания</h4>
                <p>Опитайте да промените критериите за търсене или проверете по-късно за нови пътувания.</p>
            </div>
        `;
        return;
    }

    resultsContainer.innerHTML = rides.map(ride => `
        <div class="card ride-card mb-3 fade-in">
            <div class="ride-header">
                <div class="route-info">
                    <h5 class="mb-0">${ride.origin}</h5>
                    <i class="bi bi-arrow-right route-arrow"></i>
                    <h5 class="mb-0">${ride.destination}</h5>
                </div>
                <small class="text-light">
                    <i class="bi bi-calendar me-1"></i>${formatDateTime(ride.dateTime)}
                </small>
            </div>
            <div class="card-body">
                <div class="row align-items-center">
                    <div class="col-md-6">
                        <div class="driver-info mb-2">
                            <div class="driver-avatar">
                                <i class="bi bi-person-fill"></i>
                            </div>
                            <div>
                                <h6 class="mb-0">${ride.driver.name}</h6>
                                <small class="text-muted">
                                    ${generateStars(ride.driver.rating || 0)}
                                    <span class="ms-1">(${ride.driver.totalReviews || 0})</span>
                                </small>
                            </div>
                        </div>
                        ${ride.description ? `<p class="text-muted small text-truncate-2">${ride.description}</p>` : ''}
                    </div>
                    <div class="col-md-6 text-md-end">
                        <div class="mb-2">
                            <span class="badge bg-success me-2">
                                <i class="bi bi-people-fill me-1"></i>${ride.availableSeats} места
                            </span>
                            <span class="h4 text-primary mb-0">$${ride.price}</span>
                        </div>
                        <button class="btn btn-primary" onclick="viewRideDetails(${ride.id})">
                            Подробности
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

// View ride details - redirect to search page with ride details
function viewRideDetails(rideId) {
    window.location.href = `search.html?rideId=${rideId}`;
}

// Handle login
async function handleLogin(event) {
    event.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    showLoading('loginBtn');
    
    try {
        await authService.login(email, password);
        showAlert('Успешен вход! Пренасочване...', 'success');
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 1500);
    } catch (error) {
        showAlert(error.message || 'Неуспешен вход. Моля, опитайте отново.');
        hideLoading('loginBtn', 'Влез');
    }
}

// Handle registration
async function handleRegister(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const userData = {
        name: formData.get('name'),
        email: formData.get('email'),
        password: formData.get('password'),
        role: formData.get('role')
    };
    
    // Validate password confirmation
    const confirmPassword = formData.get('confirmPassword');
    if (userData.password !== confirmPassword) {
        showAlert('Паролите не съвпадат.');
        return;
    }
    
    showLoading('registerBtn');
    
    try {
        await authService.register(userData);
        showAlert('Успешна регистрация! Пренасочване...', 'success');
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 1500);
    } catch (error) {
        showAlert(error.message || 'Неуспешна регистрация. Моля, опитайте отново.');
        hideLoading('registerBtn', 'Създай Профил');
    }
}

// Handle publish ride
async function handlePublishRide(event) {
    event.preventDefault();
    
    if (!authService.requireDriver()) return;
    
    const formData = new FormData(event.target);
    const rideData = {
        origin: formData.get('origin'),
        destination: formData.get('destination'),
        dateTime: formData.get('date') + 'T' + formData.get('time'),
        price: parseFloat(formData.get('price')),
        availableSeats: parseInt(formData.get('availableSeats')),
        description: formData.get('description')
    };
    
    showLoading('publishBtn');
    
    try {
        const response = await authService.apiRequest('/rides', {
            method: 'POST',
            body: JSON.stringify(rideData)
        });
        
        if (!response.ok) {
            const error = await response.text();
            throw new Error(error);
        }
        
        const ride = await response.json();
        showAlert('Пътуването е публикувано успешно!', 'success');
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 1500);
    } catch (error) {
        console.error('Publish ride error:', error);
        showAlert(error.message || 'Неуспешно публикуване на пътуване. Моля, опитайте отново.');
        hideLoading('publishBtn', 'Публикувай Пътуване');
    }
}

// Load dashboard data
async function loadDashboard() {
    try {
        const [ridesResponse, reservationsResponse] = await Promise.all([
            authService.apiRequest('/rides'),
            authService.apiRequest('/reservations/my-reservations')
        ]);
        
        if (ridesResponse.ok && reservationsResponse.ok) {
            const rides = await ridesResponse.json();
            const reservations = await reservationsResponse.json();
            
            displayDashboardStats(rides, reservations);
        }
    } catch (error) {
        console.error('Dashboard error:', error);
        showAlert('Неуспешно зареждане на данни за таблото.', 'warning', 'dashboardAlerts');
    }
}

function displayDashboardStats(rides, reservations) {
    const user = authService.getCurrentUser();
    
    // Update welcome message
    const welcomeMsg = document.getElementById('welcomeMessage');
    if (welcomeMsg) {
        welcomeMsg.textContent = `Добре се върна, ${user.name}!`;
    }
    
    // Show/hide stats based on user role
    const totalRidesCard = document.getElementById('totalRidesCard');
    const totalReservationsCard = document.getElementById('totalReservationsCard');
    const userRatingCard = document.getElementById('userRatingCard');
    
    if (user.role === 'DRIVER') {
        // Driver: Show rides, reservations and rating (3 columns each)
        if (totalRidesCard) {
            totalRidesCard.className = 'col-md-4';
            totalRidesCard.style.display = 'block';
        }
        if (totalReservationsCard) {
            totalReservationsCard.className = 'col-md-4';
            totalReservationsCard.style.display = 'block';
        }
        if (userRatingCard) {
            userRatingCard.className = 'col-md-4';
            userRatingCard.style.display = 'block';
        }
    } else {
        // User: Show only reservations (hide rides and rating)
        if (totalRidesCard) {
            totalRidesCard.style.display = 'none';
        }
        if (totalReservationsCard) {
            totalReservationsCard.className = 'col-md-12';
            totalReservationsCard.style.display = 'block';
        }
        if (userRatingCard) {
            userRatingCard.style.display = 'none';
        }
    }
    
    // Update stats values
    const totalRides = document.getElementById('totalRides');
    const totalReservations = document.getElementById('totalReservations');
    const userRating = document.getElementById('userRating');
    
    if (totalRides) totalRides.textContent = rides.length;
    if (totalReservations) totalReservations.textContent = reservations.length;
    if (userRating) userRating.textContent = user.rating?.toFixed(1) || '0.0';
}

// Load reservations
async function loadReservations() {
    const container = document.getElementById('reservationsContainer');
    if (!container) return;
    
    container.innerHTML = '<div class="loading">Зареждане на резервации...</div>';
    
    try {
        const response = await authService.apiRequest('/reservations/my-reservations');
        if (!response.ok) throw new Error('Failed to load reservations');
        
        const reservations = await response.json();
        displayReservations(reservations);
    } catch (error) {
        console.error('Reservations error:', error);
        container.innerHTML = '<div class="alert alert-danger">Неуспешно зареждане на резервации.</div>';
    }
}

function displayReservations(reservations) {
    const container = document.getElementById('reservationsContainer');
    if (!container) return;
    
    if (reservations.length === 0) {
        container.innerHTML = `
            <div class="text-center py-5">
                <i class="bi bi-calendar-x display-1 text-muted"></i>
                <h4 class="mt-3">Още няма резервации</h4>
                <p class="text-muted">Започнете с търсене на пътувания!</p>
                <a href="search.html" class="btn btn-primary">Търси Пътувания</a>
            </div>
        `;
        return;
    }
    
    container.innerHTML = reservations.map(reservation => `
        <div class="card reservation-card ${reservation.status.toLowerCase()} mb-3">
            <div class="card-body">
                <div class="row align-items-center">
                    <div class="col-md-8">
                        <h5 class="card-title">${reservation.ride.origin} → ${reservation.ride.destination}</h5>
                        <p class="text-muted mb-1">
                            <i class="bi bi-calendar me-2"></i>${formatDateTime(reservation.ride.dateTime)}
                        </p>
                        <p class="text-muted mb-1">
                            <i class="bi bi-person me-2"></i>Шофьор: ${reservation.ride.driver.name}
                        </p>
                        <p class="text-muted mb-0">
                            <i class="bi bi-people me-2"></i>Резервирани места: ${reservation.seatsReserved}
                        </p>
                    </div>
                    <div class="col-md-4 text-md-end">
                        <span class="badge bg-${getStatusColor(reservation.status)} mb-2">
                            ${reservation.status}
                        </span>
                        <br>
                        <span class="h5">$${reservation.ride.price * reservation.seatsReserved}</span>
                        ${reservation.status === 'CONFIRMED' && new Date(reservation.ride.dateTime) > new Date() ? `
                            <br><button class="btn btn-sm btn-outline-danger mt-2" onclick="cancelReservation(${reservation.id})">
                                Отказ
                            </button>
                        ` : ''}
                        ${reservation.status === 'CONFIRMED' ? `
                            <br><button class="btn btn-sm btn-warning mt-2" onclick="showReviewModal(${reservation.ride.id}, ${reservation.ride.driver.id}, '${reservation.ride.driver.name}', '${reservation.ride.origin}', '${reservation.ride.destination}', '${reservation.ride.dateTime}')">
                                <i class="bi bi-star me-1"></i>Оцени Шофьора
                            </button>
                        ` : ''}
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

function getStatusColor(status) {
    switch (status) {
        case 'CONFIRMED': return 'success';
        case 'CANCELLED': return 'danger';
        case 'COMPLETED': return 'secondary';
        default: return 'primary';
    }
}

// Load my rides
async function loadMyRides() {
    const container = document.getElementById('myRidesContainer');
    if (!container) return;
    
    container.innerHTML = '<div class="loading">Зареждане на пътувания...</div>';
    
    try {
        const response = await authService.apiRequest('/rides/driver/my-rides');
        if (!response.ok) throw new Error('Failed to load my rides');
        
        const rides = await response.json();
        displayMyRides(rides);
    } catch (error) {
        console.error('My rides error:', error);
        container.innerHTML = '<div class="alert alert-danger">Неуспешно зареждане на пътувания.</div>';
    }
}

function displayMyRides(rides) {
    const container = document.getElementById('myRidesContainer');
    if (!container) return;
    
    if (rides.length === 0) {
        container.innerHTML = `
            <div class="text-center py-5">
                <i class="bi bi-car-front display-1 text-muted"></i>
                <h4 class="mt-3">Още няма публикувани пътувания</h4>
                <p class="text-muted">Започнете с публикуване на вашето първо пътуване!</p>
                <a href="publish-ride.html" class="btn btn-primary">Публикувай Пътуване</a>
            </div>
        `;
        return;
    }
    
    container.innerHTML = rides.map(ride => `
        <div class="card ride-card mb-3">
            <div class="ride-header">
                <div class="route-info">
                    <h5 class="mb-0">${ride.origin}</h5>
                    <i class="bi bi-arrow-right route-arrow"></i>
                    <h5 class="mb-0">${ride.destination}</h5>
                </div>
                <small class="text-light">
                    <i class="bi bi-calendar me-1"></i>${formatDateTime(ride.dateTime)}
                </small>
            </div>
            <div class="card-body">
                <div class="row align-items-center">
                    <div class="col-md-8">
                        ${ride.description ? `<p class="text-muted mb-2">${ride.description}</p>` : ''}
                        <div class="d-flex gap-3 mb-2">
                            <span class="badge bg-success">
                                <i class="bi bi-people-fill me-1"></i>${ride.availableSeats} места
                            </span>
                            <span class="badge bg-info">
                                <i class="bi bi-currency-dollar me-1"></i>$${ride.price}
                            </span>
                            <span class="badge bg-${getStatusColor(ride.status)}">
                                ${ride.status}
                            </span>
                        </div>
                    </div>
                    <div class="col-md-4 text-md-end">
                        <div class="btn-group" role="group">
                            <button class="btn btn-outline-primary btn-sm" onclick="editRide(${ride.id})">
                                <i class="bi bi-pencil"></i> Редактирай
                            </button>
                            <button class="btn btn-outline-danger btn-sm" onclick="cancelRide(${ride.id})">
                                <i class="bi bi-x-circle"></i> Отказ
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

// Edit ride (placeholder)
function editRide(rideId) {
    showAlert('Редактирането на пътувания ще бъде добавено скоро.', 'info', 'myRidesAlerts');
}

// Cancel ride
async function cancelRide(rideId) {
    if (!confirm('Сигурни ли сте, че искате да отмените това пътуване?')) return;
    
    try {
        const response = await authService.apiRequest(`/rides/${rideId}/status?status=CANCELLED`, {
            method: 'PUT'
        });
        
        if (!response.ok) throw new Error('Failed to cancel ride');
        
        showAlert('Пътуването е отменено успешно.', 'success', 'myRidesAlerts');
        setTimeout(() => loadMyRides(), 1000);
    } catch (error) {
        console.error('Cancel ride error:', error);
        showAlert('Неуспешно отменяне на пътуване.', 'danger', 'myRidesAlerts');
    }
}

// Cancel reservation
async function cancelReservation(reservationId) {
    if (!confirm('Сигурни ли сте, че искате да откажете тази резервация?')) return;
    
    try {
        const response = await authService.apiRequest(`/reservations/${reservationId}/cancel`, {
            method: 'PUT'
        });
        
        if (!response.ok) throw new Error('Failed to cancel reservation');
        
        showAlert('Резервацията е отказана успешно.', 'success', 'reservationAlerts');
        setTimeout(() => loadReservations(), 1000);
    } catch (error) {
        console.error('Cancel reservation error:', error);
        showAlert('Неуспешно отказване на резервация.', 'danger', 'reservationAlerts');
    }
}

// Load profile
async function loadProfile() {
    try {
        const response = await authService.apiRequest('/users/profile');
        if (!response.ok) throw new Error('Failed to load profile');
        
        const user = await response.json();
        displayProfile(user);
    } catch (error) {
        console.error('Profile error:', error);
        showAlert('Неуспешно зареждане на профил.', 'danger', 'profileAlerts');
    }
}

function displayProfile(user) {
    // Update profile fields
    const fields = ['name', 'email'];
    fields.forEach(field => {
        const element = document.getElementById(field);
        if (element) element.value = user[field] || '';
    });
    
    const roleElement = document.getElementById('role');
    if (roleElement) roleElement.value = user.role || 'USER';
    
    // Update profile display
    const profileName = document.getElementById('profileName');
    const profileEmail = document.getElementById('profileEmail');
    const profileRole = document.getElementById('profileRole');
    const profileRating = document.getElementById('profileRating');
    
    if (profileName) profileName.textContent = user.name;
    if (profileEmail) profileEmail.textContent = user.email;
    if (profileRole) profileRole.textContent = user.role;
    if (profileRating) profileRating.innerHTML = generateStars(user.rating || 0);
}

// Show review modal
function showReviewModal(rideId, driverId, driverName, origin, destination, dateTime) {
    // Set hidden fields
    document.getElementById('reviewRideId').value = rideId;
    document.getElementById('reviewDriverId').value = driverId;
    
    // Set ride info
    document.getElementById('reviewRideInfo').innerHTML = `
        <strong>${origin} → ${destination}</strong><br>
        Шофьор: ${driverName}<br>
        Дата: ${formatDateTime(dateTime)}
    `;
    
    // Reset form
    document.getElementById('reviewForm').reset();
    document.getElementById('rating').value = '';
    document.getElementById('ratingText').textContent = 'Изберете оценка';
    document.querySelectorAll('.star-input').forEach(star => {
        star.classList.remove('bi-star-fill');
        star.classList.add('bi-star');
    });
    
    // Show modal
    new bootstrap.Modal(document.getElementById('reviewModal')).show();
}

// Handle star rating
document.addEventListener('DOMContentLoaded', function() {
    const stars = document.querySelectorAll('.star-input');
    const ratingInput = document.getElementById('rating');
    const ratingText = document.getElementById('ratingText');
    
    if (stars.length > 0) {
        stars.forEach(star => {
            star.addEventListener('click', function() {
                const rating = parseInt(this.dataset.rating);
                ratingInput.value = rating;
                
                // Update star display
                stars.forEach((s, index) => {
                    if (index < rating) {
                        s.classList.remove('bi-star');
                        s.classList.add('bi-star-fill');
                    } else {
                        s.classList.remove('bi-star-fill');
                        s.classList.add('bi-star');
                    }
                });
                
                // Update text
                const ratingTexts = ['', 'Много лошо', 'Лошо', 'Средно', 'Добре', 'Отлично'];
                ratingText.textContent = ratingTexts[rating];
            });
            
            star.addEventListener('mouseenter', function() {
                const rating = parseInt(this.dataset.rating);
                stars.forEach((s, index) => {
                    if (index < rating) {
                        s.style.color = '#ffc107';
                    } else {
                        s.style.color = '';
                    }
                });
            });
        });
        
        document.querySelector('.star-rating').addEventListener('mouseleave', function() {
            stars.forEach(s => s.style.color = '');
        });
    }
    
    // Handle review form submission
    const reviewForm = document.getElementById('reviewForm');
    if (reviewForm) {
        reviewForm.addEventListener('submit', handleReviewSubmit);
    }
});

// Handle review submission
async function handleReviewSubmit(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const reviewData = {
        rideId: parseInt(formData.get('rideId')),
        rating: parseInt(formData.get('rating')),
        comment: formData.get('comment')
    };
    
    if (!reviewData.rating) {
        showAlert('Моля, изберете оценка.', 'danger', 'reviewAlerts');
        return;
    }
    
    showLoading('submitReviewBtn');
    
    try {
        const response = await authService.apiRequest('/reviews', {
            method: 'POST',
            body: JSON.stringify(reviewData)
        });
        
        if (!response.ok) {
            const error = await response.text();
            throw new Error(error);
        }
        
        showAlert('Оценката е изпратена успешно!', 'success', 'reviewAlerts');
        setTimeout(() => {
            bootstrap.Modal.getInstance(document.getElementById('reviewModal')).hide();
            loadReservations();
        }, 1500);
    } catch (error) {
        console.error('Review submit error:', error);
        showAlert(error.message || 'Неуспешно изпращане на оценка.', 'danger', 'reviewAlerts');
        hideLoading('submitReviewBtn', 'Изпрати Оценка');
    }
}

// Book a ride
async function bookRide(rideId, seats = 1) {
    if (!authService.requireAuth()) return;
    
    try {
        const response = await authService.apiRequest('/reservations', {
            method: 'POST',
            body: JSON.stringify({
                rideId: rideId,
                seatsReserved: seats
            })
        });
        
        if (!response.ok) {
            const error = await response.text();
            throw new Error(error);
        }
        
        showAlert('Пътуването е резервирано успешно!', 'success');
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 1500);
    } catch (error) {
        console.error('Booking error:', error);
        showAlert(error.message || 'Неуспешно резервиране на пътуване.');
    }
}
