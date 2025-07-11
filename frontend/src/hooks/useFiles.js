import { useState, useEffect } from "react";
import apiService from "../services/apiService";

export const useFiles = (user) => {
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchFiles = async () => {
    if (!user) return; // Only fetch if user is present
    try {
      setLoading(true);
      const response = await apiService.getFiles();
      setFiles(response);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Only fetch files if user is present
  useEffect(() => {
    if (user) {
      fetchFiles();
    } else {
      setFiles([]);
    }
  }, [user]);

  return {
    files,
    loading,
    error,
    uploadFile: async (file) => {
      try {
        const response = await apiService.uploadFile(file);
        setFiles((prev) => [response, ...prev]);
        return response;
      } catch (err) {
        setError(err.message);
        throw err;
      }
    },
    deleteFile: async (fileId) => {
      try {
        await apiService.deleteFile(fileId);
        setFiles((prev) => prev.filter((file) => file.id !== fileId));
      } catch (err) {
        setError(err.message);
        throw err;
      }
    },
    searchFiles: async (query) => {
      try {
        setLoading(true);
        const response = await apiService.searchFiles(query);
        setFiles(response);
        setError(null);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    },
    refetch: fetchFiles,
    setFiles,
  };
};
