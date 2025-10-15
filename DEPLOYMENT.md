# OrangeDrive Deployment Guide - Render

This guide will help you deploy OrangeDrive to Render for free.

## Prerequisites

1. **GitHub Repository**: Your code should be pushed to GitHub
2. **Render Account**: Sign up at [render.com](https://render.com)
3. **Java 17**: Render will use Java 17 for your Spring Boot app

## Deployment Steps

### Step 1: Deploy Backend (Spring Boot + PostgreSQL)

1. **Go to Render Dashboard**
   - Visit [render.com](https://render.com) and sign in
   - Click "New +" → "Web Service"

2. **Connect GitHub Repository**
   - Select "Build and deploy from a Git repository"
   - Connect your GitHub account
   - Select your OrangeDrive repository

3. **Configure Backend Service**
   - **Name**: `orangedrive-backend`
   - **Environment**: `Java` (should auto-detect now!)
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/safeDrive-0.0.1-SNAPSHOT.jar`
   - **Plan**: Free

4. **Set Environment Variables**
   - `SPRING_PROFILES_ACTIVE`: `prod`
   - `JWT_SECRET`: Generate a random secret (32+ characters)
   - `ENCRYPTION_SECRET`: Generate a random secret (32+ characters)
   - `UPLOAD_DIR`: `/opt/render/project/src/uploads`

5. **Add PostgreSQL Database**
   - Click "New +" → "PostgreSQL"
   - **Name**: `orangedrive-db`
   - **Plan**: Free
   - **Database Name**: `safedrive`
   - **Database User**: `safedrive`

6. **Connect Database to Backend**
   - Go back to your backend service
   - Add environment variable: `DATABASE_URL` (copy from PostgreSQL service)
   - Deploy the backend

### Step 2: Deploy Frontend (React)

1. **Create New Web Service**
   - Click "New +" → "Static Site"
   - Connect your GitHub repository

2. **Configure Frontend Service**
   - **Name**: `orangedrive-frontend`
   - **Build Command**: `cd frontend && npm install && npm run build`
   - **Publish Directory**: `frontend/build`
   - **Plan**: Free

3. **Set Environment Variables**
   - `REACT_APP_API_URL`: `https://your-backend-url.onrender.com/api`
   - Replace `your-backend-url` with your actual backend URL

4. **Deploy the Frontend**

### Step 3: Test Your Deployment

1. **Check Backend Health**
   - Visit: `https://your-backend-url.onrender.com/actuator/health`
   - Should return: `{"status":"UP"}`

2. **Test Frontend**
   - Visit your frontend URL
   - Try registering a new account
   - Test file upload, notes, and credentials

## Environment Variables Summary

### Backend Environment Variables
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=postgresql://user:pass@host:port/dbname
JWT_SECRET=your-32-character-secret
ENCRYPTION_SECRET=your-32-character-secret
UPLOAD_DIR=/opt/render/project/src/uploads
```

### Frontend Environment Variables
```
REACT_APP_API_URL=https://your-backend-url.onrender.com/api
```

## Troubleshooting

### Common Issues

1. **Build Fails**
   - Check build logs in Render dashboard
   - Ensure Java 17 is selected
   - Verify Maven wrapper permissions

2. **Database Connection Issues**
   - Verify `DATABASE_URL` is correctly set
   - Check PostgreSQL service is running
   - Ensure database credentials are correct

3. **Frontend Can't Connect to Backend**
   - Verify `REACT_APP_API_URL` is set correctly
   - Check CORS settings in backend
   - Ensure backend is running and healthy

4. **File Upload Issues**
   - Check upload directory permissions
   - Verify file size limits
   - Check disk space

### Getting Help

- **Render Logs**: Check service logs in Render dashboard
- **Health Check**: Visit `/actuator/health` endpoint
- **Database**: Check PostgreSQL service status

## Free Tier Limits

- **Backend**: 750 hours/month, sleeps after 15 minutes
- **Frontend**: 100GB bandwidth/month
- **Database**: 1GB storage, 1GB RAM
- **Total Cost**: $0/month (within limits)

## Next Steps

1. **Custom Domain**: Add your own domain in Render settings
2. **SSL Certificate**: Automatically provided by Render
3. **Monitoring**: Use Render's built-in monitoring
4. **Scaling**: Upgrade to paid plans if needed

Your OrangeDrive will be live at your frontend URL once deployment is complete!
