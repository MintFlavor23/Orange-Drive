import { API_BASE_URL, STORAGE_KEYS } from "../utils/constants";

class ApiService {
  constructor() {
    this.baseURL = API_BASE_URL;
  }

  getAuthHeaders() {
    const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
    console.log(`🔑 getAuthHeaders called`);
    console.log(`🔑 Token from localStorage:`, token ? "Present" : "Missing");
    console.log(`🔑 Token value:`, token);

    const headers = {
      "Content-Type": "application/json",
      ...(token && { Authorization: `Bearer ${token}` }),
    };

    console.log(`🔑 Final headers:`, headers);
    return headers;
  }

  async request(endpoint, options = {}) {
    const config = {
      ...options,
      headers: {
        ...this.getAuthHeaders(),
        ...options.headers,
      },
    };

    try {
      console.log(
        `[apiService] 🌐 Making API request to: ${this.baseURL}${endpoint}`
      );
      console.log(`[apiService] 📋 Request config:`, {
        method: config.method || "GET",
        headers: config.headers,
        body: config.body ? config.body : "None",
      });

      const response = await fetch(`${this.baseURL}${endpoint}`, config);

      console.log(`[apiService] 📡 Response received:`, {
        status: response.status,
        statusText: response.statusText,
        ok: response.ok,
        headers: Object.fromEntries(response.headers.entries()),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        console.error(
          `[apiService] ❌ HTTP Error ${response.status}:`,
          errorData
        );
        const error = new Error(
          errorData.message || `HTTP error! status: ${response.status}`
        );
        error.status = response.status;
        error.statusText = response.statusText;
        error.data = errorData;
        console.error(
          `[apiService] ❌ API request failed: ${response.status} ${response.statusText}`,
          errorData
        );
        throw error;
      }

      let data;
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        data = await response.json();
      } else {
        data = await response.text();
      }
      console.log(
        `[apiService] ✅ API request successful: ${this.baseURL}${endpoint}`
      );
      console.log(`[apiService] 📄 Response data:`, data);
      return data;
    } catch (error) {
      console.error(`[apiService] ❌ API request failed:`, error);
      console.error(`[apiService] ❌ Error stack:`, error.stack);
      throw error;
    }
  }

  // Auth methods
  async login(credentials) {
    const response = await this.request("/auth/login", {
      method: "POST",
      body: JSON.stringify(credentials),
    });

    if (response.token) {
      localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, response.token);
      localStorage.setItem(
        STORAGE_KEYS.USER_DATA,
        JSON.stringify(response.user)
      );
    }

    return response;
  }

  async register(userData) {
    console.log("[apiService] register called with:", userData);
    const response = await this.request("/auth/register", {
      method: "POST",
      body: JSON.stringify(userData),
    });
    console.log("[apiService] register response:", response);

    if (response.token) {
      localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, response.token);
      localStorage.setItem(
        STORAGE_KEYS.USER_DATA,
        JSON.stringify(response.user)
      );
    }

    return response;
  }

  logout() {
    localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER_DATA);
  }

  getCurrentUser() {
    const userData = localStorage.getItem(STORAGE_KEYS.USER_DATA);
    return userData ? JSON.parse(userData) : null;
  }

  // File methods
  async uploadFile(file) {
    const formData = new FormData();
    formData.append("file", file);

    const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
    const response = await fetch(`${this.baseURL}/files/upload`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      body: formData,
    });

    if (!response.ok) throw new Error("Upload failed");
    return response.json();
  }

  async getFiles() {
    return this.request("/files");
  }

  async deleteFile(fileId) {
    return this.request(`/files/${fileId}`, { method: "DELETE" });
  }

  async searchFiles(query) {
    return this.request(`/files/search?query=${encodeURIComponent(query)}`);
  }

  // Note methods
  async getNotes() {
    return this.request("/notes");
  }

  async createNote(noteData) {
    return this.request("/notes", {
      method: "POST",
      body: JSON.stringify(noteData),
    });
  }

  async updateNote(noteId, noteData) {
    return this.request(`/notes/${noteId}`, {
      method: "PUT",
      body: JSON.stringify(noteData),
    });
  }

  async deleteNote(noteId) {
    return this.request(`/notes/${noteId}`, { method: "DELETE" });
  }

  async searchNotes(query) {
    return this.request(`/notes/search?query=${encodeURIComponent(query)}`);
  }

  // Credential methods
  async getCredentials() {
    return this.request("/credentials");
  }

  async createCredential(credentialData) {
    return this.request("/credentials", {
      method: "POST",
      body: JSON.stringify(credentialData),
    });
  }

  async updateCredential(credentialId, credentialData) {
    return this.request(`/credentials/${credentialId}`, {
      method: "PUT",
      body: JSON.stringify(credentialData),
    });
  }

  async deleteCredential(credentialId) {
    return this.request(`/credentials/${credentialId}`, { method: "DELETE" });
  }

  async getDecryptedPassword(credentialId) {
    try {
      console.log(
        `🔍 Making API request to decrypt password for credential ID: ${credentialId}`
      );
      console.log(
        `📍 Full URL: ${this.baseURL}/credentials/${credentialId}/password`
      );
      console.log(`🔑 Auth headers:`, this.getAuthHeaders());

      const response = await this.request(
        `/credentials/${credentialId}/password`
      );

      console.log(
        `✅ Password decryption API call successful for credential ID: ${credentialId}`
      );
      console.log(`📄 Response type:`, typeof response);
      console.log(`📄 Response:`, response);

      // Check if response is a string or object
      if (typeof response === "string") {
        return response;
      } else if (response && typeof response === "object") {
        // If response is an object, try to extract the password
        if (response.password) {
          return response.password;
        } else if (response.data) {
          return response.data;
        } else {
          console.warn(
            "⚠️ Response is an object but no password field found:",
            response
          );
          return JSON.stringify(response);
        }
      } else {
        console.warn("⚠️ Unexpected response type:", typeof response, response);
        return String(response);
      }
    } catch (error) {
      console.error(
        `❌ Password decryption API call failed for credential ID: ${credentialId}:`,
        error
      );
      console.error(`❌ Error details:`, {
        message: error.message,
        status: error.status,
        statusText: error.statusText,
        stack: error.stack,
      });
      throw error;
    }
  }

  async searchCredentials(query) {
    return this.request(
      `/credentials/search?query=${encodeURIComponent(query)}`
    );
  }
}

export default new ApiService();
