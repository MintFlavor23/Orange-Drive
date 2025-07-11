# Troubleshooting: "Failed to decrypt password" Error

## Overview

This error occurs when the frontend application cannot successfully decrypt a password for a stored credential. The error is thrown in the `handleTogglePassword` function when trying to retrieve a decrypted password from the backend API.

## Common Causes

### 1. Backend Server Issues

- **Backend server not running**: The backend server at `http://localhost:8080` is not started
- **Wrong port**: Backend is running on a different port than 8080
- **API endpoint not available**: The `/credentials/{id}/password` endpoint doesn't exist

### 2. Authentication Issues

- **Expired token**: Your authentication token has expired
- **Invalid token**: The stored token is corrupted or invalid
- **Missing token**: No authentication token is stored

### 3. Credential Issues

- **Credential not found**: The credential ID doesn't exist in the database
- **Permission denied**: You don't have permission to access this credential
- **Encryption key issues**: The backend cannot decrypt the password due to key problems

### 4. Network Issues

- **CORS errors**: Cross-origin request issues
- **Network connectivity**: Cannot reach the backend server
- **Firewall blocking**: Local firewall blocking the connection

## Debugging Steps

### Step 1: Check Backend Server

1. Ensure your backend server is running on port 8080
2. Test the connection using the debug panel in the app
3. Check backend logs for any errors

### Step 2: Check Authentication

1. Open browser developer tools (F12)
2. Go to Application tab → Local Storage
3. Check if `authToken` and `userData` are present
4. If missing, try logging out and logging back in

### Step 3: Check Browser Console

1. Open browser developer tools (F12)
2. Go to Console tab
3. Look for detailed error messages when trying to decrypt a password
4. The improved error handling will show specific error codes

### Step 4: Test API Connection

Run the test script to verify backend connectivity:

```bash
node test-backend.js
```

### Step 5: Use Debug Panel

1. Click the "Debug" button in the sidebar
2. Check authentication status
3. Test API connection
4. Review troubleshooting tips

## Error Code Meanings

- **401 Unauthorized**: Authentication failed - log out and log back in
- **403 Forbidden**: Permission denied - you don't have access to this credential
- **404 Not Found**: Credential doesn't exist
- **500 Internal Server Error**: Backend server error - check backend logs
- **Network Error**: Cannot connect to backend - check if server is running

## Solutions

### For Backend Issues:

1. Start the backend server: `npm start` or `node server.js`
2. Check backend logs for errors
3. Verify the API endpoints are properly configured

### For Authentication Issues:

1. Clear browser storage and log in again
2. Check if your session has expired
3. Verify the backend authentication middleware

### For Credential Issues:

1. Try creating a new credential to test
2. Check if the credential exists in the database
3. Verify encryption/decryption keys are properly configured

### For Network Issues:

1. Check if the backend URL is correct in `constants.js`
2. Verify no firewall is blocking the connection
3. Test with a different browser or incognito mode

## Prevention

1. **Regular token refresh**: Implement automatic token refresh
2. **Better error handling**: The improved error handling will help identify issues faster
3. **Health checks**: Regular API health checks
4. **User feedback**: Clear error messages for different scenarios

## Getting Help

If the issue persists:

1. Check the browser console for detailed error logs
2. Use the debug panel to gather information
3. Check backend server logs
4. Verify all environment variables are set correctly
