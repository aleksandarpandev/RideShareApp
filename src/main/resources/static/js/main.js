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
        showAlert('Failed to load search results. Please try again.', 'danger', 'searchAlerts');
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
                <h4>No rides found</h4>
                <p>Try adjusting your search criteria or check back later for new rides.</p>
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
                                <i class="bi bi-people-fill me-1"></i>${ride.availableSeats} seats
                            </span>
                            <span class="h4 text-primary mb-0">$${ride.price}</span>
                        </div>
                        <button class="btn btn-primary" onclick="viewRideDetails(${ride.id})">
                            View Details
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

// View ride details
function viewRideDetails(rideId) {
    window.location.href = `ride-details.html?id=${rideId}`;
}

// Handle login
async function handleLogin(event) {
    event.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    showLoading('loginBtn');
    
    try {
        await authService.login(email, password);
        showAlert('Login successful! Redirecting...', 'success');
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 1500);
    } catch (error) {
        showAlert(error.message || 'Login failed. Please try again.');
        hideLoading('loginBtn', 'Sign In');
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
        showAlert('Passwords do not match.');
        return;
    }
    
    showLoading('registerBtn');
    
    try {
        await authService.register(userData);
        showAlert('Registration successful! Redirecting...', 'success');
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 1500);
    } catch (error) {
        showAlert(error.message || 'Registration failed. Please try again.');
        hideLoading('registerBtn', 'Create Account');
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
        showAlert('Ride published successfully!', 'success');
        setTimeout(() => {
            window.location.href = 'my-rides.html';
        }, 1500);
    } catch (error) {
        console.error('Publish ride error:', error);
        showAlert(error.message || 'Failed to publish ride. Please try again.');
        hideLoading('publishBtn', 'Publish Ride');
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
        showAlert('Failed to load dashboard data.', 'warning', 'dashboardAlerts');
    }
}

function displayDashboardStats(rides, reservations) {
    const user = authService.getCurrentUser();
    
    // Update welcome message
    const welcomeMsg = document.getElementById('welcomeMessage');
    if (welcomeMsg) {
        welcomeMsg.textContent = `Welcome back, ${user.name}!`;
    }
    
    // Update stats
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
    
    container.innerHTML = '<div class="loading">Loading reservations...</div>';
    
    try {
        const response = await authService.apiRequest('/reservations/my-reservations');
        if (!response.ok) throw new Error('Failed to load reservations');
        
        const reservations = await response.json();
        displayReservations(reservations);
    } catch (error) {
        console.error('Reservations error:', error);
        container.innerHTML = '<div class="alert alert-danger">Failed to load reservations.</div>';
    }
}

function displayReservations(reservations) {
    const container = document.getElementById('reservationsContainer');
    if (!container) return;
    
    if (reservations.length === 0) {
        container.innerHTML = `
            <div class="text-center py-5">
                <i class="bi bi-calendar-x display-1 text-muted"></i>
                <h4 class="mt-3">No reservations yet</h4>
                <p class="text-muted">Start by searching for rides!</p>
                <a href="search.html" class="btn btn-primary">Search Rides</a>
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
                            <i class="bi bi-person me-2"></i>Driver: ${reservation.ride.driver.name}
                        </p>
                        <p class="text-muted mb-0">
                            <i class="bi bi-people me-2"></i>Seats reserved: ${reservation.seatsReserved}
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
                                Cancel
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

// Cancel reservation
async function cancelReservation(reservationId) {
    if (!confirm('Are you sure you want to cancel this reservation?')) return;
    
    try {
        const response = await authService.apiRequest(`/reservations/${reservationId}/cancel`, {
            method: 'PUT'
        });
        
        if (!response.ok) throw new Error('Failed to cancel reservation');
        
        showAlert('Reservation cancelled successfully.', 'success', 'reservationAlerts');
        setTimeout(() => loadReservations(), 1000);
    } catch (error) {
        console.error('Cancel reservation error:', error);
        showAlert('Failed to cancel reservation.', 'danger', 'reservationAlerts');
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
        showAlert('Failed to load profile.', 'danger', 'profileAlerts');
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
        
        showAlert('Ride booked successfully!', 'success');
        setTimeout(() => {
            window.location.href = 'reservations.html';
        }, 1500);
    } catch (error) {
        console.error('Booking error:', error);
        showAlert(error.message || 'Failed to book ride.');
    }
}
