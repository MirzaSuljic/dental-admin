import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api, setToken } from "../api.js";
 
export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
 
  const submit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const res = await api("/api/auth/login", {
        method: "POST",
        body: { email, password },
      });
      setToken(res.token);
      navigate("/");
    } catch (err) {
      setError(err.message);
    }
  };
 
  return (
    <div className="login-wrap">
      <form className="card login" onSubmit={submit}>
        <h1>Prijava</h1>
        <label>
          Email
          <input type="email" required value={email}
                 onChange={(e) => setEmail(e.target.value)} />
        </label>
        <label>
          Lozinka
          <input type="password" required value={password}
                 onChange={(e) => setPassword(e.target.value)} />
        </label>
        {error && <p className="error">{error}</p>}
        <button className="btn">Prijavi se</button>
      </form>
    </div>
  );
}
