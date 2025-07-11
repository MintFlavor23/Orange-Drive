import { useState, useEffect } from "react";
import apiService from "../services/apiService";

export const useNotes = (user) => {
  const [notes, setNotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchNotes = async () => {
    if (!user) return; // Only fetch if user is present
    try {
      setLoading(true);
      const response = await apiService.getNotes();
      setNotes(response);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const createNote = async (noteData) => {
    try {
      const response = await apiService.createNote(noteData);
      setNotes((prev) => [response, ...prev]);
      return response;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const updateNote = async (noteId, noteData) => {
    try {
      const response = await apiService.updateNote(noteId, noteData);
      setNotes((prev) =>
        prev.map((note) => (note.id === noteId ? response : note))
      );
      return response;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const deleteNote = async (noteId) => {
    try {
      await apiService.deleteNote(noteId);
      setNotes((prev) => prev.filter((note) => note.id !== noteId));
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const searchNotes = async (query) => {
    try {
      setLoading(true);
      const response = await apiService.searchNotes(query);
      setNotes(response);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user) {
      fetchNotes();
    } else {
      setNotes([]);
    }
  }, [user]);

  return {
    notes,
    loading,
    error,
    createNote,
    updateNote,
    deleteNote,
    searchNotes,
    refetch: fetchNotes,
    setNotes, // Expose for clearing on user change
  };
};
