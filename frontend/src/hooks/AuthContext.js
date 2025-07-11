import React, { createContext, useContext, useState, useEffect } from "react";
import apiService from "../services/apiService";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const currentUser = apiService.getCurrentUser();
    if (currentUser) setUser(currentUser);
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    const response = await apiService.login(credentials);
    setUser(response.user);
    return response;
  };

  const logout = () => {
    apiService.logout();
    setUser(null);
  };

  const register = async (userData) => {
    const response = await apiService.register(userData);
    setUser(response.user);
    return response;
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, register, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
