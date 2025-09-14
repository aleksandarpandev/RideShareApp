# RideShare - Carpooling Web Application

A comprehensive ride-sharing web application built with **Spring Boot** (backend) and **HTML/CSS/JavaScript with Bootstrap 5** (frontend). Users can search for rides, book seats, publish rides (as drivers), and rate their experiences.

## 🚗 Features

### Core Functionality
- **User Registration & Authentication** - JWT-based secure authentication
- **Role-based Access** - USER (passengers) and DRIVER roles
- **Search Rides** - Find rides by origin, destination, and date
- **Book Seats** - Reserve seats in available rides
- **Publish Rides** - Drivers can offer rides with route, price, and seat details
- **Rate & Review** - Users can rate drivers after completing trips
- **User Profiles** - View ratings, trip history, and manage account

### User Interface
- **Responsive Design** - Mobile-first approach with Bootstrap 5
- **Modern UI/UX** - Clean, intuitive interface with smooth animations
- **Real-time Updates** - Dynamic content loading with JavaScript
- **Search Filters** - Advanced filtering options for finding rides

## 🛠 Technology Stack

### Backend
- **Java 17** - Modern Java features
- **Spring Boot 3.1.5** - Framework for rapid development
- **Spring Security** - JWT authentication & authorization
- **Spring Data JPA** - Database operations with Hibernate
- **MySQL 8** - Relational database
- **Maven** - Dependency management

### Frontend
- **HTML5** - Semantic markup
- **CSS3** - Modern styling with custom properties
- **JavaScript (ES6+)** - Interactive functionality
- **Bootstrap 5.3.2** - Responsive UI components
- **Bootstrap Icons** - Icon library

### Security
- **JWT Tokens** - Stateless authentication
- **BCrypt** - Password hashing
- **CORS Configuration** - Cross-origin resource sharing
- **Role-based Authorization** - Method-level security

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- **Modern web browser**

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Carpool
```

### 2. Database Setup
```bash
# Login to MySQL
mysql -u root -p

# Run the database setup script
source database-setup.sql
```

### 3. Configure Application
Update `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/carpool_db
    username: your_mysql_username
    password: your_mysql_password
```

### 4. Build and Run
```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

### 5. Access the Application
- **Frontend**: http://localhost:8080
- **API Base URL**: http://localhost:8080/api

## 📚 API Documentation

### Authentication Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout

### Ride Endpoints
- `GET /api/rides` - Get all active rides
- `GET /api/rides/search` - Search rides with filters
- `GET /api/rides/{id}` - Get ride details
- `POST /api/rides` - Create new ride (drivers only)
- `PUT /api/rides/{id}/status` - Update ride status

### Reservation Endpoints
- `POST /api/reservations` - Book a ride
- `GET /api/reservations/my-reservations` - Get user's reservations
- `PUT /api/reservations/{id}/cancel` - Cancel reservation

### Review Endpoints
- `POST /api/reviews` - Submit a review
- `GET /api/reviews/driver/{id}` - Get driver reviews
- `GET /api/reviews/my-reviews` - Get user's reviews

### User Endpoints
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile

## 🎨 Frontend Pages

- **Homepage** (`index.html`) - Hero section with quick search
- **Search** (`search.html`) - Advanced ride search with filters
- **Login/Register** - User authentication forms
- **Dashboard** (`dashboard.html`) - User overview and quick actions
- **Publish Ride** (`publish-ride.html`) - Create new rides (drivers)
- **Reservations** - Manage bookings
- **Profile** - User account management

## 🗄 Database Schema

### Tables
- **users** - User accounts with roles and ratings
- **rides** - Ride offers with route and pricing
- **reservations** - Seat bookings
- **reviews** - Driver ratings and comments

### Sample Data
The database setup includes sample users and rides for testing:
- **Test Users**: john.driver@example.com, sarah.passenger@example.com
- **Password**: password123 (for all test accounts)

## 🔐 Security Features

- **JWT Authentication** - Secure token-based auth
- **Password Hashing** - BCrypt encryption
- **Role-based Access Control** - USER and DRIVER permissions
- **Input Validation** - Server-side validation with Bean Validation
- **CORS Protection** - Configured for frontend-backend communication

## 🎯 Key Features Explained

### User Roles
- **USER**: Can search rides, book seats, and leave reviews
- **DRIVER**: Can publish rides, manage bookings, and receive reviews

### Search & Filtering
- Search by origin and destination (partial matching)
- Filter by date
- Sort by date or price
- Real-time availability updates

### Booking System
- Seat reservation with validation
- Automatic seat count updates
- Cancellation with seat restoration
- Booking confirmation emails (configurable)

### Rating System
- 5-star rating system
- Written reviews
- Average rating calculation
- Driver reputation tracking

## 🚀 Deployment

### Development
```bash
mvn spring-boot:run
```

### Production
```bash
# Build JAR
mvn clean package

# Run with production profile
java -jar target/carpool-app-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License.

## 📞 Support

For support or questions:
- Create an issue in the repository
- Email: support@rideshare.com

## 🔄 Future Enhancements

- **Real-time Chat** - WebSocket-based messaging
- **Payment Integration** - Stripe/PayPal integration
- **Mobile App** - React Native mobile application
- **Email Notifications** - Automated booking confirmations
- **GPS Integration** - Real-time location tracking
- **Advanced Analytics** - Trip statistics and insights

---

**Happy Coding! 🚗💨**
