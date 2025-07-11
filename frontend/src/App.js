import React, { useState, useEffect, useRef } from "react";
import { Plus, Search, Bug } from "lucide-react";

// Import components
import LoginForm from "./components/auth/LoginForm";
import RegisterForm from "./components/auth/RegisterForm";
import Sidebar from "./components/common/Sidebar";
import Notification from "./components/common/Notification";
import DebugPanel from "./components/common/DebugPanel";
import FileUpload from "./components/files/FileUpload";
import FileList from "./components/files/FileList";
import NoteList from "./components/notes/NoteList";
import NoteModal from "./components/notes/NoteModal";
import CredentialList from "./components/credentials/CredentialList";
import CredentialModal from "./components/credentials/CredentialModal";
import FileSection from "./components/files/FileSection";

// Import hooks
import { useAuth } from "./hooks/AuthContext";
import { useFiles } from "./hooks/useFiles";
import { useNotes } from "./hooks/useNotes";
import { useCredentials } from "./hooks/useCredentials";

// Import services
import apiService from "./services/apiService";

const App = () => {
  const { user, login, logout, register, loading: authLoading } = useAuth();
  const {
    files,
    uploadFile,
    deleteFile,
    loading: filesLoading,
    setFiles,
    refetch,
  } = useFiles(user);
  const {
    notes,
    createNote,
    updateNote,
    deleteNote,
    loading: notesLoading,
    setNotes, // Add this
  } = useNotes(user);
  const {
    credentials,
    createCredential,
    updateCredential,
    deleteCredential,
    getDecryptedPassword,
    loading: credentialsLoading,
    setCredentials, // Add this
  } = useCredentials();

  const [isLoginMode, setIsLoginMode] = useState(true);
  const [activeTab, setActiveTab] = useState("files");
  const [notification, setNotification] = useState(null);
  const [showNoteModal, setShowNoteModal] = useState(false);
  const [showCredentialModal, setShowCredentialModal] = useState(false);
  const [showDebugPanel, setShowDebugPanel] = useState(false);
  const [editingNote, setEditingNote] = useState(null);
  const [editingCredential, setEditingCredential] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [visiblePasswords, setVisiblePasswords] = useState({});

  const showNotification = (message, type = "success") => {
    setNotification({ message, type });
  };

  // Clear files on logout
  const handleLogout = () => {
    logout();
    setFiles([]); // Clear files immediately on logout
    setVisiblePasswords({});
    setNotification({
      message: "You have been logged out successfully.",
      type: "success",
    });
  };

  // Login: log user state after login
  const handleLogin = async (credentials) => {
    try {
      await login(credentials);
      showNotification("Welcome back! Login successful.");
      window.location.reload(); // Force a full page reload after login
    } catch (error) {
      showNotification("Login failed. Please check your credentials.", "error");
      throw error;
    }
  };

  const handleRegister = async (userData) => {
    console.log("[App] handleRegister called with:", userData);
    try {
      await register(userData);
      showNotification("Account created successfully! Welcome to Safe Drive.");
      window.location.reload(); // Force a full page reload after registration
    } catch (error) {
      showNotification("Registration failed. Please try again.", "error");
      throw error;
    }
  };

  const handleFileUpload = async (file) => {
    try {
      await uploadFile(file);
      showNotification(`"${file.name}" uploaded successfully!`);
    } catch (error) {
      showNotification(`Failed to upload "${file.name}"`, "error");
    }
  };

  const handleFileDelete = async (fileId) => {
    if (window.confirm("Are you sure you want to delete this file?")) {
      try {
        await deleteFile(fileId);
        showNotification("File deleted successfully!");
      } catch (error) {
        showNotification("Failed to delete file", "error");
      }
    }
  };

  const handleFileDownload = (file) => {
    const token = localStorage.getItem("authToken");
    const url = `${apiService.baseURL}/files/${file.id}`;

    // Create a temporary link to download the file
    const link = document.createElement("a");
    link.href = url;
    link.target = "_blank";

    // Add authorization header by opening in new window
    window.open(url, "_blank");
  };

  const handleNoteCreate = () => {
    setEditingNote(null);
    setShowNoteModal(true);
  };

  const handleNoteEdit = (note) => {
    setEditingNote(note);
    setShowNoteModal(true);
  };

  const handleNoteSave = async (noteData) => {
    try {
      if (editingNote) {
        await updateNote(editingNote.id, noteData);
        showNotification("Note updated successfully!");
      } else {
        await createNote(noteData);
        showNotification("Note created successfully!");
      }
      setShowNoteModal(false);
      setEditingNote(null);
    } catch (error) {
      showNotification("Failed to save note", "error");
      throw error;
    }
  };

  const handleNoteDelete = async (noteId) => {
    if (window.confirm("Are you sure you want to delete this note?")) {
      try {
        await deleteNote(noteId);
        showNotification("Note deleted successfully!");
      } catch (error) {
        showNotification("Failed to delete note", "error");
      }
    }
  };

  const handleCredentialCreate = () => {
    setEditingCredential(null);
    setShowCredentialModal(true);
  };

  const handleCredentialEdit = (credential) => {
    setEditingCredential(credential);
    setShowCredentialModal(true);
  };

  const handleCredentialSave = async (credentialData) => {
    try {
      if (editingCredential) {
        await updateCredential(editingCredential.id, credentialData);
        showNotification("Credential updated successfully!");
      } else {
        await createCredential(credentialData);
        showNotification("Credential saved successfully!");
      }
      setShowCredentialModal(false);
      setEditingCredential(null);
    } catch (error) {
      showNotification("Failed to save credential", "error");
      throw error;
    }
  };

  const handleCredentialDelete = async (credentialId) => {
    if (window.confirm("Are you sure you want to delete this credential?")) {
      try {
        await deleteCredential(credentialId);
        showNotification("Credential deleted successfully!");
      } catch (error) {
        showNotification("Failed to delete credential", "error");
      }
    }
  };

  const handleTogglePassword = async (credentialId) => {
    console.log("App.js: handleTogglePassword called with", credentialId);
    console.log(
      `🔍 handleTogglePassword called for credential ID: ${credentialId}`
    );
    console.log(`👁️ Current visible passwords:`, visiblePasswords);

    if (visiblePasswords[credentialId]) {
      // Hide password
      console.log(`🙈 Hiding password for credential ID: ${credentialId}`);
      setVisiblePasswords((prev) => ({ ...prev, [credentialId]: null }));
    } else {
      // Show password
      console.log(
        `👁️ Attempting to show password for credential ID: ${credentialId}`
      );
      try {
        console.log(
          `🔐 Calling getDecryptedPassword for credential ID: ${credentialId}`
        );
        const password = await getDecryptedPassword(credentialId);
        console.log(
          `✅ Password received for credential ID: ${credentialId}:`,
          typeof password,
          password
        );
        setVisiblePasswords((prev) => ({ ...prev, [credentialId]: password }));
        console.log(
          `✅ Password visibility updated for credential ID: ${credentialId}`
        );
      } catch (error) {
        console.error(
          `❌ Password decryption failed for credential ID: ${credentialId}:`,
          error
        );
        console.error(`❌ Error details:`, {
          message: error.message,
          status: error.status,
          statusText: error.statusText,
          credentialId: credentialId,
        });

        // Provide more specific error messages
        let errorMessage = "Failed to decrypt password";
        if (error.message.includes("401")) {
          errorMessage = "Authentication failed. Please log in again.";
        } else if (error.message.includes("404")) {
          errorMessage = "Credential not found.";
        } else if (error.message.includes("500")) {
          errorMessage = "Server error. Please try again later.";
        } else if (error.message.includes("403")) {
          errorMessage =
            "Access denied. You don't have permission to view this password.";
        } else if (error.message.includes("NetworkError")) {
          errorMessage = "Network error. Please check your connection.";
        } else if (error.message.includes("Failed to fetch")) {
          errorMessage =
            "Cannot connect to server. Please check if the backend is running.";
        }

        console.error(`❌ Showing error notification: ${errorMessage}`);
        showNotification(errorMessage, "error");
      }
    }
  };

  // Always clear and refetch files when user changes
  const prevUserRef = useRef();
  useEffect(() => {
    if (prevUserRef.current !== user) {
      setFiles([]);
      setCredentials([]);
      setNotes([]);
      if (user) {
        refetch();
        // Optionally, refetch credentials and notes
      }
      prevUserRef.current = user;
    }
  }, [user, refetch]);

  // Filter data based on search query
  const filteredFiles = files.filter((file) =>
    file.originalName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const filteredNotes = notes.filter(
    (note) =>
      note.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      note.content.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const filteredCredentials = credentials.filter(
    (credential) =>
      credential.service.toLowerCase().includes(searchQuery.toLowerCase()) ||
      credential.username.toLowerCase().includes(searchQuery.toLowerCase())
  );

  // Key for file section to force remount on user change
  const filesKey = user
    ? `files-${user.id || user.email || user.name}`
    : "files-guest";

  if (authLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading Safe Drive...</p>
        </div>
      </div>
    );
  }

  if (!user) {
    return isLoginMode ? (
      <LoginForm
        onLogin={handleLogin}
        onToggleMode={() => setIsLoginMode(false)}
        loading={authLoading}
      />
    ) : (
      <RegisterForm
        onRegister={handleRegister}
        onToggleMode={() => setIsLoginMode(true)}
        loading={authLoading}
      />
    );
  }

  return (
    <div className="h-screen bg-gray-50 flex">
      <Sidebar
        user={user}
        activeTab={activeTab}
        onTabChange={setActiveTab}
        onLogout={handleLogout}
        onDebug={() => setShowDebugPanel(true)}
      />

      <div className="flex-1 flex flex-col">
        <main className="flex-1 overflow-auto p-8">
          {/* Search Bar */}
          <div className="mb-6">
            <div className="relative max-w-md">
              <Search className="w-5 h-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
              <input
                type="text"
                placeholder={`Search ${activeTab}...`}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent w-full"
              />
            </div>
          </div>

          {/* Files Tab */}
          {activeTab === "files" && (
            <FileSection
              key={filesKey}
              user={user}
              showNotification={showNotification}
            />
          )}

          {/* Notes Tab */}
          {activeTab === "notes" && (
            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <h2 className="text-2xl font-bold text-gray-800">
                  Notes ({filteredNotes.length})
                </h2>
                <button
                  onClick={handleNoteCreate}
                  className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2"
                >
                  <Plus className="w-5 h-5" />
                  <span>New Note</span>
                </button>
              </div>

              {notesLoading ? (
                <div className="flex justify-center py-8">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                </div>
              ) : (
                <NoteList
                  notes={filteredNotes}
                  onEditNote={handleNoteEdit}
                  onDeleteNote={handleNoteDelete}
                />
              )}
            </div>
          )}

          {/* Credentials Tab */}
          {activeTab === "credentials" && (
            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <h2 className="text-2xl font-bold text-gray-800">
                  Credentials ({filteredCredentials.length})
                </h2>
                <button
                  onClick={handleCredentialCreate}
                  className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2"
                >
                  <Plus className="w-5 h-5" />
                  <span>New Credential</span>
                </button>
              </div>

              <div className="bg-white rounded-lg shadow-sm border">
                <div className="p-6">
                  {credentialsLoading ? (
                    <div className="flex justify-center py-8">
                      <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                    </div>
                  ) : (
                    <CredentialList
                      credentials={filteredCredentials}
                      onEditCredential={handleCredentialEdit}
                      onDeleteCredential={handleCredentialDelete}
                      onTogglePassword={handleTogglePassword}
                      visiblePasswords={visiblePasswords}
                    />
                  )}
                </div>
              </div>
            </div>
          )}
        </main>
      </div>

      {/* Modals */}
      <NoteModal
        isOpen={showNoteModal}
        onClose={() => {
          setShowNoteModal(false);
          setEditingNote(null);
        }}
        onSave={handleNoteSave}
        note={editingNote}
      />

      <CredentialModal
        isOpen={showCredentialModal}
        onClose={() => {
          setShowCredentialModal(false);
          setEditingCredential(null);
        }}
        onSave={handleCredentialSave}
        credential={editingCredential}
      />

      <DebugPanel
        isVisible={showDebugPanel}
        onClose={() => setShowDebugPanel(false)}
      />

      {notification && (
        <Notification
          notification={notification}
          onClose={() => setNotification(null)}
        />
      )}
    </div>
  );
};

export default App;
