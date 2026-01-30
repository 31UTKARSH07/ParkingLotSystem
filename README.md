# 🚗 Parking Lot Management System

A comprehensive Spring Boot REST API application for managing parking lot operations, vehicle tracking, and parking ticket generation with JWT-based authentication.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Environment Variables](#environment-variables)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Usage Examples](#usage-examples)
- [Contributing](#contributing)

## 🎯 Overview

This Parking Lot Management System is a robust backend solution designed to streamline parking operations. It handles vehicle entry/exit, spot allocation, ticket generation, payment calculation, and provides comprehensive dashboard analytics. The system supports multiple parking lots, floors, and different vehicle types with dynamic pricing strategies.

## ✨ Features

### Core Functionality
- **Vehicle Management**: Register and track vehicles by license plate
- **Smart Spot Allocation**: Automatic assignment of parking spots based on vehicle type
- **Ticket Management**: Generate unique tickets for entry/exit tracking
- **Dynamic Pricing**: Configurable hourly rates for different vehicle types
- **Grace Period**: Configurable free parking period
- **Multi-Floor Support**: Manage parking lots with multiple floors
- **Real-time Availability**: Track available and occupied spots in real-time

### Security & Authentication
- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: USER and ADMIN roles with different permissions
- **Password Encryption**: BCrypt password hashing
- **Stateless Sessions**: RESTful design with no server-side sessions

### Dashboard & Analytics
- **Occupancy Statistics**: Real-time parking lot occupancy rates
- **Revenue Tracking**: Daily, weekly, monthly, and yearly revenue reports
- **Floor-wise Analytics**: Individual floor statistics and utilization
- **Active Vehicle Count**: Current vehicles parked in the system
- **Payment Status**: Track paid and unpaid tickets

### Advanced Features
- **Vehicle Location Finder**: Locate any parked vehicle by license plate
- **Date Range Queries**: Filter tickets and revenue by custom date ranges
- **Spot Type Filtering**: Query available spots by type (Small, Medium, Large, Handicapped)
- **Exception Handling**: Comprehensive error handling with meaningful messages

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Database**: MongoDB (NoSQL)
- **Security**: Spring Security + JWT
- **Authentication**: JSON Web Tokens (jjwt)
- **Build Tool**: Maven
- **Validation**: Jakarta Bean Validation
- **Lombok**: Code generation for models

## 📋 Prerequisites

Before running this application, ensure you have:

- Java JDK 17 or higher
- Maven 3.6+
- MongoDB 4.4+ (local or Atlas)
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd ParkingLotApplication
```

### 2. Create Environment File

Create a `.env` file in the root directory with the following variables:

```env
MONGOURI=mongodb://localhost:27017/parking_lot_db
SERVER_PORT=8080
JWTSECRET=your-base64-encoded-secret-key-here-minimum-256-bits
JWTEXPIRATION=86400000
```

**Note**: Generate a secure JWT secret using:
```bash
openssl rand -base64 64
```

### 3. Install Dependencies

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 🔐 Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `MONGOURI` | MongoDB connection string | - | Yes |
| `SERVER_PORT` | Application server port | 8080 | No |
| `JWTSECRET` | Base64 encoded JWT signing key | - | Yes |
| `JWTEXPIRATION` | Token expiration time in milliseconds | 86400000 (24h) | No |

### Optional Pricing Configuration

You can override default pricing in `application.properties`:

```properties
parking.rate.bike=10.0
parking.rate.car=20.0
parking.rate.truck=30.0
parking.rate.van=25.0
parking.grace.period=15
```

## 📡 API Endpoints

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "roles": ["ROLE_USER"]
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

### Parking Operations

#### Park Vehicle
```http
POST /api/parking/park
Authorization: Bearer <token>
Content-Type: application/json

{
  "licensePlate": "ABC1234",
  "vehicleType": "CAR",
  "color": "Red",
  "ownerName": "John Doe",
  "ownerPhone": "+1234567890",
  "parkingLotId": "lot123",
  "preferredFloor": 1
}
```

#### Exit Vehicle
```http
POST /api/parking/exit
Authorization: Bearer <token>
Content-Type: application/json

{
  "ticketNumber": "TKT-A1B2C3D4"
}
```

### Parking Lot Management

```http
GET    /api/parking-lots              # Get all parking lots
POST   /api/parking-lots              # Create parking lot
GET    /api/parking-lots/{id}         # Get specific lot
GET    /api/parking-lots/name/{name}  # Find by name
PUT    /api/parking-lots/{id}         # Update lot
DELETE /api/parking-lots/{id}         # Delete lot
```

### Parking Spot Queries

```http
GET /api/spots/available/{parkingLotId}                    # All available spots
GET /api/spots/available/count/{parkingLotId}              # Count available
GET /api/spots/available/{parkingLotId}/type/{spotType}    # By type
GET /api/spots/floor/{parkingLotId}/{floor}                # By floor
GET /api/spots/{spotNumber}                                # Specific spot
GET /api/spots/statistics/{parkingLotId}                   # Spot statistics
```

### Vehicle Management

```http
GET    /api/vehicles                        # All vehicles
POST   /api/vehicles                        # Register vehicle
GET    /api/vehicles/{id}                   # Get by ID
GET    /api/vehicles/license/{licensePlate} # Find by plate
GET    /api/vehicles/type/{vehicleType}     # Filter by type
GET    /api/vehicles/owner/{phone}          # By owner phone
GET    /api/vehicles/location/{licensePlate}# Find location
PUT    /api/vehicles/{id}                   # Update vehicle
DELETE /api/vehicles/{id}                   # Delete vehicle
```

### Ticket Management

```http
GET /api/tickets                           # All tickets
GET /api/tickets/{id}                      # Get by ID
GET /api/tickets/number/{ticketNumber}     # Find by number
GET /api/tickets/vehicle/{vehicleId}       # Vehicle tickets
GET /api/tickets/parking-lot/{lotId}       # Lot tickets
GET /api/tickets/unpaid                    # Unpaid tickets
GET /api/tickets/paid                      # Paid tickets
GET /api/tickets/active/{parkingLotId}     # Active count
GET /api/tickets/date-range?start=<>&end=<># Date range filter
```

### Dashboard & Analytics

```http
GET /api/dashboard/overview/{parkingLotId}              # Complete overview
GET /api/dashboard/revenue/{parkingLotId}?period=month  # Revenue stats
GET /api/dashboard/floor-wise/{parkingLotId}            # Floor statistics
GET /api/dashboard/summary                              # System summary
```

### Test Endpoints

```http
GET /api/public                    # Public (no auth)
GET /api/user/profile              # User/Admin only
GET /api/admin/dashboard           # Admin only
```

## 📁 Project Structure

```
src/main/java/com/parkinglot/ParkingLotApplication/
├── config/                    # Configuration classes
│   ├── DotenvProcessor.java      # Environment variable loader
│   ├── JwtAuthenticationFilter.java
│   ├── JwtUtil.java
│   └── SecurityConfig.java
├── controller/                # REST Controllers
│   ├── AuthController.java
│   ├── DashboardController.java
│   ├── ParkingController.java
│   ├── ParkingLotController.java
│   ├── ParkingSpotController.java
│   ├── TicketController.java
│   └── VehicleController.java
├── dto/                       # Data Transfer Objects
│   ├── JwtResponse.java
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   ├── ParkingRequest.java
│   ├── ParkingResponse.java
│   ├── ExitRequest.java
│   └── ExitResponse.java
├── exception/                 # Custom Exceptions
│   ├── GlobalExceptionHandler.java
│   ├── NoSpotAvailableException.java
│   ├── ParkingLotNotFoundException.java
│   └── TicketNotFoundException.java
├── model/                     # Domain Models
│   ├── ParkingLot.java
│   ├── ParkingSpot.java
│   ├── Ticket.java
│   ├── User.java
│   ├── Vehicle.java
│   └── enums/
│       ├── SpotStatus.java
│       ├── SpotType.java
│       └── VehicleType.java
├── repository/                # MongoDB Repositories
│   ├── ParkingLotRepository.java
│   ├── ParkingSpotRepository.java
│   ├── TicketRepository.java
│   ├── UserRepository.java
│   └── VehicleRepository.java
└── services/                  # Business Logic
    ├── AuthService.java
    ├── CustomUserDetailsService.java
    ├── ParkingService.java
    ├── ParkingServiceImpl.java
    └── PricingStrategy.java
```

## 💡 Usage Examples

### Complete Workflow Example

1. **Register as Admin**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@parking.com",
    "password": "admin123",
    "roles": ["ROLE_ADMIN"]
  }'
```

2. **Login and Get Token**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

3. **Create Parking Lot**
```bash
curl -X POST http://localhost:8080/api/parking-lots \
  -H "Authorization: Bearer <your-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Downtown Parking",
    "address": "123 Main St",
    "totalFloors": 3,
    "capacityPerFloor": {
      "BIKE": 20,
      "CAR": 50,
      "TRUCK": 10
    },
    "hourlyRates": {
      "BIKE": 10.0,
      "CAR": 20.0,
      "TRUCK": 30.0
    }
  }'
```

4. **Park a Vehicle**
```bash
curl -X POST http://localhost:8080/api/parking/park \
  -H "Authorization: Bearer <your-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "licensePlate": "ABC1234",
    "vehicleType": "CAR",
    "parkingLotId": "<lot-id>",
    "ownerName": "John Doe"
  }'
```

5. **Check Dashboard**
```bash
curl -X GET http://localhost:8080/api/dashboard/overview/<lot-id> \
  -H "Authorization: Bearer <your-token>"
```

6. **Exit Vehicle**
```bash
curl -X POST http://localhost:8080/api/parking/exit \
  -H "Authorization: Bearer <your-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "ticketNumber": "TKT-A1B2C3D4"
  }'
```

## 🔧 Configuration Details

### Vehicle Types
- `BIKE` - Motorcycles, scooters
- `CAR` - Standard cars
- `TRUCK` - Large vehicles
- `VAN` - Medium-sized vehicles

### Spot Types
- `SMALL` - For bikes
- `MEDIUM` - For cars
- `LARGE` - For trucks and vans
- `HANDICAPPED` - Accessible spots

### Spot Status
- `AVAILABLE` - Open for parking
- `OCCUPIED` - Currently in use
- `RESERVED` - Reserved for specific users
- `OUT_OF_SERVICE` - Maintenance mode

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🐛 Known Issues

- Grace period applies globally (not per parking lot)
- No payment gateway integration yet
- Email notifications not implemented

## 🚀 Future Enhancements

- [ ] Real-time WebSocket notifications
- [ ] Payment gateway integration
- [ ] Email/SMS notifications
- [ ] QR code ticket generation
- [ ] Mobile app support
- [ ] Advanced reservation system
- [ ] Vehicle image upload
- [ ] Loyalty program
- [ ] Admin web dashboard
- [ ] Reporting and analytics exports

## 📞 Support

For support, please open an issue in the GitHub repository or contact the development team.

---

**Built with ❤️ using Spring Boot and MongoDB**
