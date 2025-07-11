# Safe Drive Frontend

React frontend application for connecting to a Java backend API.

## 🏗️ Project Structure

```
src/
├── components/
│   ├── auth/           # Authentication components
│   ├── common/         # Shared UI components
│   ├── credentials/    # Credential management
│   ├── files/         # File management
│   └── notes/         # Notes management
├── hooks/             # Custom React hooks
├── services/          # API service layer
└── utils/            # Utilities and constants
```

## 🚀 Getting Started

### Prerequisites

- Node.js (v14 or higher)
- npm or yarn
- Java backend server running on `http://localhost:8080`

### Installation

1. **Install dependencies**

   ```bash
   npm install
   ```

2. **Configure backend connection**

   The frontend is configured to connect to a Java backend at `http://localhost:8080`.

   To change the backend URL, update `src/utils/constants.js`:

   ```javascript
   export const API_BASE_URL =
     process.env.REACT_APP_API_URL || "http://localhost:8080/api";
   ```

   Or set the environment variable:

   ```env
   REACT_APP_API_URL=http://localhost:8080/api
   ```

3. **Start the frontend**

   ```bash
   npm start
   ```

4. **Access the application**
   Open [http://localhost:3000](http://localhost:3000) in your browser

## 🔧 Backend Connection

### API Configuration

- **Base URL**: `http://localhost:8080/api`
- **Proxy**: Development server proxies requests to backend
- **Authentication**: JWT token-based

### Required Backend Endpoints

The frontend expects these Java backend endpoints:

- **Authentication**: `/auth/login`, `/auth/register`
- **Files**: `/files`, `/files/upload`, `/files/{id}`
- **Notes**: `/notes`, `/notes/{id}`
- **Credentials**: `/credentials`, `/credentials/{id}`, `/credentials/{id}/password`

### CORS Configuration

Ensure your Java backend allows CORS requests from `http://localhost:3000`:

```java
// Example Spring Boot CORS configuration
@CrossOrigin(origins = "http://localhost:3000")
```

## 📋 Available Scripts

- `npm start` - Start development server
- `npm test` - Run tests
- `npm run build` - Build for production
- `npm run eject` - Eject from Create React App

## 🐛 Troubleshooting

### Backend Connection Issues

1. **Backend not running**: Ensure Java backend is started on port 8080
2. **CORS errors**: Check backend CORS configuration
3. **API errors**: Verify backend endpoints match expected structure

### Debug Tools

- Use browser developer tools to check network requests
- Check console for API error messages
- Verify authentication tokens in localStorage

## 📦 Build for Production

```bash
npm run build
```

The build artifacts will be in the `build/` directory.
