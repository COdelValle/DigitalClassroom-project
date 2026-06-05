/**
 * Servicio de Autenticación
 * Maneja login, logout, y almacenamiento de tokens JWT
 */

import { setAuthToken, clearAuthToken, post } from "./api";

const API_CONFIG = {
  BFF_WEB: import.meta.env.VITE_BFF_WEB_URL || "http://localhost:8080/api/v1/bff",
};

/**
 * Realiza login y guarda el token JWT
 * @param {string} username - Usuario
 * @param {string} password - Contraseña
 * @returns {Promise<{token, user}>} Token y datos del usuario
 */
export const login = async (username, password) => {
  try {
    // Este endpoint necesita existir en BFF_Web
    const response = await post(`${API_CONFIG.BFF_WEB}/auth/login`, {
      username,
      password,
    });
    
    if (response.token) {
      setAuthToken(response.token);
    }
    
    return response;
  } catch (error) {
    throw new Error(`Login failed: ${error.message}`);
  }
};

/**
 * Realiza logout eliminando el token
 */
export const logout = () => {
  clearAuthToken();
  // Redirigir a login si estamos en una ruta protegida
  if (window.location.pathname !== "/login") {
    window.location.href = "/login";
  }
};

/**
 * Obtiene el token actual
 */
export const getAuthToken = () => {
  return localStorage.getItem("authToken");
};

/**
 * Verifica si el usuario está autenticado
 */
export const isAuthenticated = () => {
  const token = getAuthToken();
  return !!token;
};

/**
 * Registra un nuevo usuario
 * @param {Object} userData - {username, email, password, firstName, lastName, role}
 */
export const register = async (userData) => {
  try {
    const response = await post(`${API_CONFIG.BFF_WEB}/auth/register`, userData);
    
    if (response.token) {
      setAuthToken(response.token);
    }
    
    return response;
  } catch (error) {
    throw new Error(`Registration failed: ${error.message}`);
  }
};

/**
 * Refresca el token JWT
 */
export const refreshToken = async () => {
  try {
    const response = await post(`${API_CONFIG.BFF_WEB}/auth/refresh`, {});
    
    if (response.token) {
      setAuthToken(response.token);
    }
    
    return response;
  } catch (error) {
    // Si falla, se requiere nuevo login
    logout();
    throw new Error("Token refresh failed");
  }
};

export default {
  login,
  logout,
  register,
  refreshToken,
  getAuthToken,
  isAuthenticated,
};
