const API = import.meta.env.VITE_API_URL;
 
export const getToken = () => localStorage.getItem("token");
 
export const setToken = (token) => {
  if (token) localStorage.setItem("token", token);
  else localStorage.removeItem("token");
};
 
export async function api(path, options = {}) {
  const token = getToken();
  const res = await fetch(`${API}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: options.body ? JSON.stringify(options.body) : undefined,
  });
 
  // prijava je istekla ili nije ispravna: vrati na login
  if (res.status === 401 && !path.startsWith("/api/auth")) {
    setToken(null);
    window.location.href = "/login";
    return null;
  }
  if (res.status === 204) return null;
 
  const data = await res.json().catch(() => null);
  if (!res.ok) {
    const error = new Error(data?.message || "Došlo je do greške");
    error.fields = data?.fields;
    throw error;
  }
  return data;
}
