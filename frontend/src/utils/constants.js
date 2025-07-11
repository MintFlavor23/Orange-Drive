export const API_BASE_URL =
  process.env.REACT_APP_API_URL || "http://localhost:8080/api";

export const STORAGE_KEYS = {
  AUTH_TOKEN: "authToken",
  USER_DATA: "userData",
};

export const NOTIFICATION_TYPES = {
  SUCCESS: "success",
  ERROR: "error",
  INFO: "info",
};
