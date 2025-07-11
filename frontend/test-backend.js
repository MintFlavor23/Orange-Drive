const fetch = require("node-fetch");

const API_BASE_URL =
  process.env.REACT_APP_API_URL || "http://localhost:8080/api";

async function testBackendConnection() {
  console.log("🔍 Testing backend connection...");
  console.log(`API URL: ${API_BASE_URL}`);

  try {
    // Test basic connectivity
    console.log("\n1. Testing basic connectivity...");
    const response = await fetch(`${API_BASE_URL.replace("/api", "")}/health`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (response.ok) {
      console.log("✅ Backend server is running");
    } else {
      console.log(
        `❌ Backend server responded with: ${response.status} ${response.statusText}`
      );
    }
  } catch (error) {
    console.log(`❌ Cannot connect to backend: ${error.message}`);
    console.log("\n💡 Troubleshooting tips:");
    console.log("• Make sure the backend server is running on port 8080");
    console.log("• Check if there are any firewall issues");
    console.log("• Verify the API_BASE_URL environment variable");
  }

  try {
    // Test API endpoint
    console.log("\n2. Testing API endpoint...");
    const apiResponse = await fetch(`${API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: "test@example.com",
        password: "testpassword",
      }),
    });

    if (apiResponse.status === 401) {
      console.log(
        "✅ API endpoint is accessible (401 is expected for invalid credentials)"
      );
    } else {
      console.log(
        `📝 API endpoint responded with: ${apiResponse.status} ${apiResponse.statusText}`
      );
    }
  } catch (error) {
    console.log(`❌ API endpoint test failed: ${error.message}`);
  }
}

testBackendConnection();
