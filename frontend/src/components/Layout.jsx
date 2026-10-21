import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { setToken } from "../api.js";
 
export default function Layout() {
  const navigate = useNavigate();
 
  const logout = () => {
    setToken(null);
    navigate("/login");
  };
 
  return (
    <div className="layout">
      <aside className="sidebar">
        <h2>Ordinacija</h2>
        <nav>
          <NavLink to="/" end>Pregled</NavLink>
          <NavLink to="/appointments">Termini</NavLink>
          <NavLink to="/patients">Pacijenti</NavLink>
          <NavLink to="/dentists">Zubari</NavLink>
          <NavLink to="/treatments">Usluge</NavLink>
        </nav>
        <button className="btn-light" onClick={logout}>Odjava</button>
      </aside>
      <main className="content">
        <Outlet />
      </main>
    </div>
  );
}
