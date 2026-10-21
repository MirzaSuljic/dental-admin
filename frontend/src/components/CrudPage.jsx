import { useEffect, useState } from "react";
import { api } from "../api.js";
 
export default function CrudPage({ title, endpoint, fields, columns, searchable = false }) {
  const [items, setItems] = useState([]);
  const [form, setForm] = useState(null); // null = forma je zatvorena
  const [q, setQ] = useState("");
  const [error, setError] = useState("");
 
  const load = async (search = q) => {
    const query = searchable && search ? `?q=${encodeURIComponent(search)}` : "";
    setItems(await api(endpoint + query));
  };
 
  useEffect(() => {
    load("");
  }, []);
 
  const emptyForm = () =>
    Object.fromEntries(fields.map((f) => [f.name, f.type === "checkbox" ? true : ""]));
 
  const openNew = () => {
    setError("");
    setForm(emptyForm());
  };
 
  const openEdit = (item) => {
    setError("");
    setForm({
      id: item.id,
      ...Object.fromEntries(
        fields.map((f) => [f.name, item[f.name] ?? (f.type === "checkbox" ? false : "")])
      ),
    });
  };
 
  // prazno polje šaljemo kao null, a brojeve kao brojeve
  const toBody = () =>
    Object.fromEntries(
      fields.map((f) => {
        const v = form[f.name];
        if (f.type === "checkbox") return [f.name, v];
        if (v === "" || v === null) return [f.name, null];
        if (f.type === "number") return [f.name, Number(v)];
        return [f.name, v];
      })
    );
 
  const save = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await api(form.id ? `${endpoint}/${form.id}` : endpoint, {
        method: form.id ? "PUT" : "POST",
        body: toBody(),
      });
      setForm(null);
      load();
    } catch (err) {
      setError(err.fields ? Object.values(err.fields).join(", ") : err.message);
    }
  };
 
  const remove = async (item) => {
    if (!window.confirm("Obrisati ovaj zapis?")) return;
    setError("");
    try {
      await api(`${endpoint}/${item.id}`, { method: "DELETE" });
      load();
    } catch (err) {
      setError(err.message);
    }
  };
 
  const setField = (name, value) => setForm({ ...form, [name]: value });
 
  return (
    <div>
      <div className="page-head">
        <h1>{title}</h1>
        <button className="btn" onClick={openNew}>+ Dodaj</button>
      </div>
 
      {searchable && (
        <form className="search" onSubmit={(e) => { e.preventDefault(); load(); }}>
          <input placeholder="Pretraga..." value={q} onChange={(e) => setQ(e.target.value)} />
          <button className="btn-light">Traži</button>
        </form>
      )}
 
      {error && <p className="error">{error}</p>}
 
      {form && (
        <form className="card" onSubmit={save}>
          <h3>{form.id ? "Izmjena" : "Novi zapis"}</h3>
          <div className="grid">
            {fields.map((f) => (
              <label key={f.name}>
                {f.label}
                {f.type === "textarea" ? (
                  <textarea rows={3} value={form[f.name]}
                            onChange={(e) => setField(f.name, e.target.value)} />
                ) : f.type === "checkbox" ? (
                  <input type="checkbox" checked={form[f.name]}
                         onChange={(e) => setField(f.name, e.target.checked)} />
                ) : (
                  <input type={f.type || "text"} step={f.type === "number" ? "any" : undefined}
                         required={f.required} value={form[f.name]}
                         onChange={(e) => setField(f.name, e.target.value)} />
                )}
              </label>
            ))}
          </div>
          <button className="btn">Sačuvaj</button>{" "}
          <button type="button" className="btn-light" onClick={() => setForm(null)}>
            Odustani
          </button>
        </form>
      )}
 
      <table>
        <thead>
          <tr>
            {columns.map((c) => <th key={c.key}>{c.label}</th>)}
            <th></th>
          </tr>
        </thead>
        <tbody>
          {items.map((item) => (
            <tr key={item.id}>
              {columns.map((c) => (
                <td key={c.key}>{c.render ? c.render(item) : item[c.key]}</td>
              ))}
              <td className="actions">
                <button className="btn-light" onClick={() => openEdit(item)}>Uredi</button>{" "}
                <button className="btn-danger" onClick={() => remove(item)}>Obriši</button>
              </td>
            </tr>
          ))}
          {items.length === 0 && (
            <tr><td colSpan={columns.length + 1}>Nema podataka.</td></tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
