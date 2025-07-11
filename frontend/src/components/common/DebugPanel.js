import React, { useState } from "react";
import { Bug, RefreshCw, AlertTriangle } from "lucide-react";

const DebugPanel = ({ isVisible, onClose }) => {
  const [debugInfo, setDebugInfo] = useState({
    authToken: localStorage.getItem("authToken") ? "Present" : "Missing",
    userData: localStorage.getItem("userData") ? "Present" : "Missing",
    apiUrl: process.env.REACT_APP_API_URL || "http://localhost:8080/api",
  });

  const testApiConnection = async () => {
    try {
      const response = await fetch(`${debugInfo.apiUrl}/health`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        alert("✅ API connection successful!");
      } else {
        alert(
          `❌ API connection failed: ${response.status} ${response.statusText}`
        );
      }
    } catch (error) {
      alert(`❌ API connection error: ${error.message}`);
    }
  };

  const refreshDebugInfo = () => {
    setDebugInfo({
      authToken: localStorage.getItem("authToken") ? "Present" : "Missing",
      userData: localStorage.getItem("userData") ? "Present" : "Missing",
      apiUrl: process.env.REACT_APP_API_URL || "http://localhost:8080/api",
    });
  };

  if (!isVisible) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-semibold flex items-center">
            <Bug className="w-5 h-5 mr-2" />
            Debug Panel
          </h3>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700"
          >
            ✕
          </button>
        </div>

        <div className="space-y-4">
          <div className="bg-gray-50 p-4 rounded-lg">
            <h4 className="font-medium mb-2">Authentication Status</h4>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span>Auth Token:</span>
                <span
                  className={
                    debugInfo.authToken === "Present"
                      ? "text-green-600"
                      : "text-red-600"
                  }
                >
                  {debugInfo.authToken}
                </span>
              </div>
              <div className="flex justify-between">
                <span>User Data:</span>
                <span
                  className={
                    debugInfo.userData === "Present"
                      ? "text-green-600"
                      : "text-red-600"
                  }
                >
                  {debugInfo.userData}
                </span>
              </div>
              <div className="flex justify-between">
                <span>API URL:</span>
                <span className="text-blue-600">{debugInfo.apiUrl}</span>
              </div>
            </div>
          </div>

          <div className="space-y-2">
            <button
              onClick={testApiConnection}
              className="w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors flex items-center justify-center"
            >
              <RefreshCw className="w-4 h-4 mr-2" />
              Test API Connection
            </button>

            <button
              onClick={refreshDebugInfo}
              className="w-full bg-gray-600 text-white py-2 px-4 rounded-lg hover:bg-gray-700 transition-colors flex items-center justify-center"
            >
              <RefreshCw className="w-4 h-4 mr-2" />
              Refresh Debug Info
            </button>
          </div>

          <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-3">
            <div className="flex items-start">
              <AlertTriangle className="w-5 h-5 text-yellow-600 mr-2 mt-0.5" />
              <div className="text-sm text-yellow-800">
                <p className="font-medium">Troubleshooting Tips:</p>
                <ul className="mt-1 space-y-1">
                  <li>• Check if backend server is running on port 8080</li>
                  <li>• Verify your authentication token is valid</li>
                  <li>• Check browser console for detailed error logs</li>
                  <li>• Ensure the credential exists in the database</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DebugPanel;
