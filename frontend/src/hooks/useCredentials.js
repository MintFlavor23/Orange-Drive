import { useState, useEffect } from "react";
import apiService from "../services/apiService";

export const useCredentials = () => {
  const [credentials, setCredentials] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchCredentials = async () => {
    try {
      setLoading(true);
      const response = await apiService.getCredentials();
      setCredentials(response);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const createCredential = async (credentialData) => {
    try {
      const response = await apiService.createCredential(credentialData);
      setCredentials((prev) => [response, ...prev]);
      return response;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const updateCredential = async (credentialId, credentialData) => {
    try {
      const response = await apiService.updateCredential(
        credentialId,
        credentialData
      );
      setCredentials((prev) =>
        prev.map((cred) => (cred.id === credentialId ? response : cred))
      );
      return response;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const deleteCredential = async (credentialId) => {
    try {
      await apiService.deleteCredential(credentialId);
      setCredentials((prev) => prev.filter((cred) => cred.id !== credentialId));
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const getDecryptedPassword = async (credentialId) => {
    try {
      return await apiService.getDecryptedPassword(credentialId);
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const searchCredentials = async (query) => {
    try {
      setLoading(true);
      const response = await apiService.searchCredentials(query);
      setCredentials(response);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCredentials();
  }, []);

  return {
    credentials,
    loading,
    error,
    createCredential,
    updateCredential,
    deleteCredential,
    getDecryptedPassword,
    searchCredentials,
    refetch: fetchCredentials,
    setCredentials, // Expose for clearing on user change
  };
};
