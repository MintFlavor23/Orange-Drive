# OrangeDrive Backend

Orange Drive is a secure, user-isolated file storage and password management system built with Spring Boot.

## 🚀 Features

### Security & User Isolation

- **JWT-based Authentication**: Stateless authentication with secure token management
- **Complete User Isolation**: Users can only access their own data (files, notes, credentials)
- **Data Encryption**: Credential passwords are encrypted using AES encryption
- **File System Isolation**: Files stored in user-specific directories
- **Double Validation**: Security checks at both service and controller layers

### Core Functionality

- **File Management**: Upload, download, delete, and search files
- **Secure Notes**: Create, edit, and manage private notes
- **Password Manager**: Store and manage encrypted credentials
- **User Management**: Registration, login, and user profile management

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5.3
- **Database**: PostgreSQL 16.9
- **Authentication**: JWT (JSON Web Tokens)
- **Security**: Spring Security with BCrypt password hashing
- **Database Migration**: Flyway
- **Build Tool**: Maven
- **Java Version**: 17+

## 📋 Prerequisites

- Java 17 or higher
- PostgreSQL 16.9
- Maven 3.6+

## 🚀 Quick Start

### 1. Database Setup

```bash
# Create PostgreSQL database
createdb safedrive

# Or using psql
psql -c "CREATE DATABASE safedrive;"
```

### 2. Environment Configuration

Create `application-local.yml` for local development:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/safedrive
    username: your_username
    password: your_password

app:
  jwt:
    secret: your-super-secret-jwt-key-here
    expiration: 86400000 # 24 hours
  encryption:
    secret: your-encryption-secret-key
  upload:
    dir: uploads
    max-size: 50MB
```

### 3. Build and Run

```bash
# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Authentication Endpoints

#### Register User

```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

### File Management

#### Upload File

```http
POST /api/files/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <file>
```

#### List User Files

```http
GET /api/files
Authorization: Bearer <token>
```

#### Download File

```http
GET /api/files/{fileId}
Authorization: Bearer <token>
```

#### Delete File

```http
DELETE /api/files/{fileId}
Authorization: Bearer <token>
```

#### Search Files

```http
GET /api/files/search?query=document
Authorization: Bearer <token>
```

### Notes Management

#### Create Note

```http
POST /api/notes
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "My Note",
  "content": "Note content here"
}
```

#### List User Notes

```http
GET /api/notes
Authorization: Bearer <token>
```

#### Get Note

```http
GET /api/notes/{noteId}
Authorization: Bearer <token>
```

#### Update Note

```http
PUT /api/notes/{noteId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Updated Title",
  "content": "Updated content"
}
```

#### Delete Note

```http
DELETE /api/notes/{noteId}
Authorization: Bearer <token>
```

#### Search Notes

```http
GET /api/notes/search?query=important
Authorization: Bearer <token>
```

### Credentials Management

#### Create Credential

```http
POST /api/credentials
Authorization: Bearer <token>
Content-Type: application/json

{
  "service": "Gmail",
  "username": "user@gmail.com",
  "password": "mypassword",
  "url": "https://gmail.com",
  "notes": "Work email"
}
```

#### List User Credentials

```http
GET /api/credentials
Authorization: Bearer <token>
```

#### Get Credential

```http
GET /api/credentials/{credentialId}
Authorization: Bearer <token>
```

#### Get Decrypted Password

```http
GET /api/credentials/{credentialId}/password
Authorization: Bearer <token>
```

#### Update Credential

```http
PUT /api/credentials/{credentialId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "service": "Updated Service",
  "username": "newuser@gmail.com",
  "password": "newpassword",
  "url": "https://updated.com",
  "notes": "Updated notes"
}
```

#### Delete Credential

```http
DELETE /api/credentials/{credentialId}
Authorization: Bearer <token>
```

#### Search Credentials

```http
GET /api/credentials/search?query=gmail
Authorization: Bearer <token>
```

## 🔒 Security Features

### User Isolation

- All data queries filter by `userId`
- Users can only access their own files, notes, and credentials
- Physical file system isolation prevents unauthorized access
- Database-level foreign key constraints ensure data integrity

### Data Protection

- Credential passwords encrypted using AES encryption
- JWT tokens with configurable expiration
- BCrypt password hashing for user passwords
- Input validation and sanitization
- SQL injection prevention through parameterized queries

### Access Control

- Double validation at service and controller layers
- `SecurityUtil.validateUserAccess()` ensures proper authorization
- Repository methods enforce user-specific queries

## 🗄️ Database Schema

### Users Table

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER' NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Files Table

```sql
CREATE TABLE files (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    upload_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
```

### Notes Table

```sql
CREATE TABLE notes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
```

### Credentials Table

```sql
CREATE TABLE credentials (
    id BIGSERIAL PRIMARY KEY,
    service VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    encrypted_password TEXT NOT NULL,
    url VARCHAR(500),
    notes TEXT,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
```

## 🧪 Testing

### Manual Testing

#### Test Registration

```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"password123"}'
```

#### Test Login

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

#### Test File Upload (with token)

```bash
curl -X POST "http://localhost:8080/api/files/upload" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -F "file=@/path/to/your/file.txt"
```

### Debug Endpoints

#### Test Encryption

```http
GET /api/auth/debug/encryption?text=testpassword
```

## 📁 Project Structure

```
src/main/java/com/safedrive/
├── config/
│   ├── SecurityConfig.java          # Security configuration
│   ├── JwtAuthenticationFilter.java # JWT authentication filter
│   └── CacheControlFilter.java      # Cache control headers
├── controller/
│   ├── AuthController.java          # Authentication endpoints
│   ├── FileController.java          # File management
│   ├── NoteController.java          # Notes management
│   └── CredentialController.java    # Credentials management
├── service/
│   ├── UserService.java             # User management
│   ├── FileService.java             # File operations
│   ├── NoteService.java             # Notes operations
│   └── CredentialService.java       # Credentials operations
├── repository/
│   ├── UserRepository.java          # User data access
│   ├── FileRepository.java          # File data access
│   ├── NoteRepository.java          # Notes data access
│   └── CredentialRepository.java    # Credentials data access
├── entity/
│   ├── User.java                    # User entity
│   ├── FileEntity.java              # File entity
│   ├── Note.java                    # Note entity
│   └── Credential.java              # Credential entity
├── dto/
│   ├── request/                     # Request DTOs
│   └── response/                    # Response DTOs
├── exception/                       # Custom exceptions
├── util/
│   ├── JwtUtil.java                # JWT utilities
│   ├── EncryptionUtil.java          # Encryption utilities
│   └── SecurityUtil.java            # Security utilities
└── SafeDriveApplication.java        # Main application class
```

## 🔧 Configuration

### Application Properties

Key configuration options in `application.yml`:

```yaml
app:
  jwt:
    secret: ${JWT_SECRET:myVerySecretKeyThatIsLongEnoughForHS256Algorithm}
    expiration: ${JWT_EXPIRATION:86400000} # 24 hours
  encryption:
    secret: ${ENCRYPTION_SECRET:MySecretKey12345}
  upload:
    dir: ${UPLOAD_DIR:uploads}
    max-size: ${MAX_FILE_SIZE:50MB}

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/safedrive
    username: ${DB_USERNAME:safedrive}
    password: ${DB_PASSWORD:password}
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB
```

## 🚨 Troubleshooting

### Common Issues

1. **Database Connection Failed**

   - Ensure PostgreSQL is running
   - Check database credentials in `application.yml`
   - Verify database exists: `createdb safedrive`

2. **JWT Token Issues**

   - Check JWT secret configuration
   - Verify token expiration settings
   - Ensure proper Authorization header format

3. **File Upload Issues**

   - Check upload directory permissions
   - Verify file size limits
   - Ensure multipart configuration

4. **Encryption Errors**
   - Verify encryption secret is consistent
   - Check for corrupted credential data
   - Test encryption with debug endpoint

### Logs

Application logs are written to `logs/safe-drive.log` by default.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🔗 Related

- Frontend Repository: [Link to frontend repo]
- API Documentation: [Link to API docs]
- Deployment Guide: [Link to deployment guide]
