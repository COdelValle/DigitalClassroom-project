/**
 * Configuración base de la API
 * Define URLs base para cada microservicio usando variables de entorno
 */

const API_CONFIG = {
  ASSESSMENT_MANAGER: import.meta.env.VITE_ASSESSMENT_MANAGER_URL || "http://localhost:8083/api/v1",
  CLASSROOM_MANAGER: import.meta.env.VITE_CLASSROOM_MANAGER_URL || "http://localhost:8084/api/v1",
  STUDENT_MANAGER: import.meta.env.VITE_STUDENT_MANAGER_URL || "http://localhost:8081/api/v1",
  BFF_WEB: import.meta.env.VITE_BFF_WEB_URL || "http://localhost:8080/api/v1/bff",
};

/**
 * Obtiene el JWT token del localStorage
 */
const getAuthToken = () => {
  return localStorage.getItem("authToken");
};

/**
 * Obtiene los headers comunes con autenticación JWT
 */
const getHeaders = (includeContentType = true) => {
  const headers = {};
  
  if (includeContentType) {
    headers["Content-Type"] = "application/json";
  }
  
  const token = getAuthToken();
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  
  return headers;
};

/**
 * Manejador de errores centralizado
 */
const handleError = (error) => {
  if (error.response) {
    const status = error.response.status;
    const data = error.response.data;
    
    // Manejar 401 - Token expirado o inválido
    if (status === 401) {
      localStorage.removeItem("authToken");
      window.location.href = "/login";
      throw new Error("Sesión expirada. Por favor, inicia sesión nuevamente.");
    }
    
    // Manejar 403 - Sin permisos
    if (status === 403) {
      throw new Error("No tienes permiso para realizar esta acción");
    }
    
    console.error("Error API:", status, data);
    throw new Error(
      data?.message || data?.error || `Error ${status}`
    );
  } else if (error.request) {
    console.error("No hay respuesta del servidor:", error.request);
    throw new Error("No hay conexión con el servidor");
  } else {
    console.error("Error:", error.message);
    throw error;
  }
};

/**
 * Realiza una petición GET
 */
export const get = async (url) => {
  try {
    const response = await fetch(url, {
      method: "GET",
      headers: getHeaders(false),
    });
    
    if (!response.ok) {
      throw { response };
    }
    
    // Algunos endpoints podrían no devolver JSON
    const contentType = response.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
      return await response.json();
    }
    return response;
  } catch (error) {
    handleError(error);
  }
};

/**
 * Realiza una petición POST
 */
export const post = async (url, data) => {
  try {
    const response = await fetch(url, {
      method: "POST",
      headers: getHeaders(true),
      body: JSON.stringify(data),
    });
    
    if (!response.ok) {
      throw { response };
    }
    
    return await response.json();
  } catch (error) {
    handleError(error);
  }
};

/**
 * Realiza una petición PUT
 */
export const put = async (url, data) => {
  try {
    const response = await fetch(url, {
      method: "PUT",
      headers: getHeaders(true),
      body: JSON.stringify(data),
    });
    
    if (!response.ok) {
      throw { response };
    }
    
    return await response.json();
  } catch (error) {
    handleError(error);
  }
};

/**
 * Realiza una petición DELETE
 */
export const apiDelete = async (url) => {
  try {
    const response = await fetch(url, {
      method: "DELETE",
      headers: getHeaders(false),
    });
    
    if (!response.ok) {
      throw { response };
    }
    
    // DELETE a veces no devuelve contenido (204 No Content)
    if (response.status === 204 || response.headers.get("content-length") === "0") {
      return null;
    }
    
    return await response.json();
  } catch (error) {
    handleError(error);
  }
};

/**
 * Realiza una petición PATCH
 */
export const patch = async (url, data) => {
  try {
    const response = await fetch(url, {
      method: "PATCH",
      headers: getHeaders(true),
      body: JSON.stringify(data),
    });
    
    if (!response.ok) {
      throw { response };
    }
    
    return await response.json();
  } catch (error) {
    handleError(error);
  }
};

/**
 * Guarda un JWT token en localStorage
 */
export const setAuthToken = (token) => {
  if (token) {
    localStorage.setItem("authToken", token);
  }
};

/**
 * Elimina el JWT token del localStorage
 */
export const clearAuthToken = () => {
  localStorage.removeItem("authToken");
};

export default API_CONFIG;
