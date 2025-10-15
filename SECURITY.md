# SafeDrive Security Implementation

## Overview

SafeDrive implements comprehensive user isolation to ensure that each user can only access their own data. This document outlines the security measures in place.

## User Isolation Architecture

### 1. Authentication Layer

- **JWT-based Authentication**: All requests require a valid JWT token
- **Token Validation**: Tokens are validated on every request through `JwtAuthenticationFilter`
- **User Context**: Authenticated user information is stored in Spring Security context

### 2. Authorization Layer

- **Controller Level**: All endpoints require authentication
- **User Extraction**: Current user is extracted from security context using `SecurityUtil`
- **Access Validation**: Double validation ensures users can only access their own data

### 3. Data Access Layer

- **Repository Level**: All queries filter by `userId`
- **Service Level**: Additional validation ensures user ownership
- **Database Level**: Foreign key constraints enforce data integrity

## Security Features

### ✅ Implemented Security Measures

1. **JWT Authentication**

   - Stateless authentication with JWT tokens
   - Token expiration and validation
   - Secure token generation and verification

2. **User Isolation**

   - All data queries filter by `userId`
   - Users can only access their own files, notes, and credentials
   - No cross-user data access possible

3. **File System Isolation**

   - Files stored in user-specific directories: `uploads/users/{userId}/`
   - Physical file system isolation prevents unauthorized access
   - Unique filenames prevent conflicts

4. **Data Encryption**

   - Credential passwords are encrypted using AES encryption
   - Encryption keys are securely managed
   - Decryption only available to authenticated users

5. **Input Validation**

   - All inputs are validated and sanitized
   - File upload validation prevents malicious files
   - SQL injection prevention through parameterized queries

6. **Access Control**
   - Double validation at service and controller layers
   - `SecurityUtil.validateUserAccess()` ensures proper authorization
   - Repository methods enforce user-specific queries

## API Security

### Authentication Required Endpoints

All endpoints except `/api/auth/**` require authentication:

```
POST /api/auth/register    - User registration
POST /api/auth/login       - User login
POST /api/auth/logout      - User logout
GET  /api/auth/security-audit - Security audit endpoint
```

### Protected Endpoints

All data endpoints require authentication and user isolation:

```
Files:
POST   /api/files/upload           - Upload file
GET    /api/files                  - List user files
GET    /api/files/{fileId}         - Download file
DELETE /api/files/{fileId}         - Delete file
GET    /api/files/search           - Search files

Notes:
POST   /api/notes                  - Create note
GET    /api/notes                  - List user notes
GET    /api/notes/{noteId}         - Get note
PUT    /api/notes/{noteId}         - Update note
DELETE /api/notes/{noteId}         - Delete note
GET    /api/notes/search           - Search notes

Credentials:
POST   /api/credentials            - Create credential
GET    /api/credentials            - List user credentials
GET    /api/credentials/{id}       - Get credential
GET    /api/credentials/{id}/password - Get decrypted password
PUT    /api/credentials/{id}       - Update credential
DELETE /api/credentials/{id}       - Delete credential
GET    /api/credentials/search     - Search credentials
```

## Database Security

### User Isolation at Database Level

```sql
-- All queries include user_id filter
SELECT * FROM files WHERE user_id = ? AND id = ?
SELECT * FROM notes WHERE user_id = ? AND id = ?
SELECT * FROM credentials WHERE user_id = ? AND id = ?

-- Foreign key constraints ensure data integrity
ALTER TABLE files ADD CONSTRAINT fk_files_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
```

### Data Cleanup

- Database migration `V2__Clean_database.sql` removes all existing data
- Physical files are cleaned from `uploads/users/` directories
- Sequence reset ensures clean state

## Security Validation

### Controller Layer

```java
// All controllers use SecurityUtil for user extraction
User user = SecurityUtil.getCurrentUser();
```

### Service Layer

```java
// Double validation ensures user ownership
SecurityUtil.validateUserAccess(userId);
return fileRepository.findByIdAndUserId(fileId, userId);
```

### Repository Layer

```java
// All queries filter by userId
Optional<FileEntity> findByIdAndUserId(Long id, Long userId);
List<FileEntity> findByUserIdOrderByUploadDateDesc(Long userId);
```

## Security Testing

### Security Audit Endpoint

```
GET /api/auth/security-audit
```

Returns security status and current user information.

### Manual Testing

1. Register two different users
2. Upload files/notes/credentials with each user
3. Verify users can only see their own data
4. Verify cross-user access is denied

## Configuration

### JWT Configuration

```yaml
app:
  jwt:
    secret: ${JWT_SECRET:myVerySecretKeyThatIsLongEnoughForHS256Algorithm}
    expiration: ${JWT_EXPIRATION:86400000}
```

### File Upload Configuration

```yaml
app:
  upload:
    dir: ${UPLOAD_DIR:uploads}
    max-size: ${MAX_FILE_SIZE:50MB}
```

### Encryption Configuration

```yaml
app:
  encryption:
    secret: ${ENCRYPTION_SECRET:MySecretKey12345}
```

## Best Practices

1. **Never expose user IDs in URLs** - Use resource IDs only
2. **Always validate user ownership** - Double-check at service layer
3. **Use parameterized queries** - Prevent SQL injection
4. **Validate file uploads** - Check file type and size
5. **Encrypt sensitive data** - Credentials are encrypted
6. **Log security events** - Monitor for suspicious activity
7. **Regular security audits** - Use the security audit endpoint

## Monitoring and Logging

### Security Logs

- Authentication attempts (success/failure)
- File access patterns
- User data access
- Security violations

### Audit Trail

- All data modifications are logged
- User actions are tracked
- File system operations are monitored

## Compliance

This implementation ensures:

- **Data Privacy**: Users can only access their own data
- **Data Integrity**: Foreign key constraints prevent orphaned data
- **Audit Trail**: All operations are logged
- **Secure Storage**: Files and credentials are properly secured
- **Access Control**: Multi-layer validation prevents unauthorized access

## Conclusion

SafeDrive implements comprehensive user isolation with multiple layers of security validation. The system ensures that users can only access their own data through authentication, authorization, and data access controls at every layer of the application.
