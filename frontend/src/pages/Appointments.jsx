import { useEffect, useState } from "react";
import { api } from "../api.js";
 
const STATUS = { SCHEDULED: "Zakazan", COMPLETED: "Završen", CANCELLED: "Otkazan" };
const today = () => new Date().toLocaleDateString("sv-SE"); // oblik 2026-10-20
const time = (s) => s.slice(11, 16); // iz 2026-10-20T09:30:00 uzmi 09:30
 
function Select({ label, value, onChange, options }) {
  return (
    <label>
      {label}
      <select required value={value} onChange={(e) => onChange(e.target.value)}>
        <option value="">Izaberi...</option>
        {options.map((o) => (
          <option key={o.id} value={o.id}>{o.text}</option>
        ))}
      </select>
    </label>
  );
}
 
export default function Appointments() {
  const [date, setDate] = useState(today());
  const [items, setItems] = useState([]);
  const [patients, setPatients] = useState([]);
  const [dentists, setDentists] = useState([]);
  const [treatments, setTreatments] = useState([]);
  const [form, setForm] = useState(null);
  const [error, setError] = useState("");
 
  const load = async (d = date) => {
    setItems(await api(`/api/appointments?date=${d}`));
  };
 
  useEffect(() => {
    api("/api/patients").then(setPatients);
    api("/api/dentists").then((list) => setDentists(list.filter((d) => d.active)));
    api("/api/treatments").then(setTreatments);
  }, []);
 
  useEffect(() => {
    load(date);
  }, [date]);
 
  const openNew = () => {
    setError("");
    setForm({ patientId: "", dentistId: "", treatmentId: "", startTime: `${date}T09:00`, note: "" });
  };
 
  const openEdit = (a) => {
    setError("");
    setForm({
      id: a.id,
      patientId: a.patient.id,
      dentistId: a.dentist.id,
      treatmentId: a.treatment.id,
      startTime: a.startTime.slice(0, 16),
      note: a.note ?? "",
    });
  };
 
  const save = async (e) => {
    e.preventDefault();
    setError("");
    const body = {
      patientId: Number(form.patientId),
      dentistId: Number(form.dentistId),
      treatmentId: Number(form.treatmentId),
      startTime: form.startTime,
      note: form.note || null,
    };
    try {
      await api(form.id ? `/api/appointments/${form.id}` : "/api/appointments", {
        method: form.id ? "PUT" : "POST",
        body,
      });
      const savedDate = form.startTime.slice(0, 10);
      setForm(null);
      if (savedDate === date) load();
      else setDate(savedDate); // prebaci prikaz na dan novog termina
    } catch (err) {
      setError(err.fields ? Object.values(err.fields).join(", ") : err.message);
    }
  };
 
  const changeStatus = async (a, status) => {
    setError("");
    try {
      await api(`/api/appointments/${a.id}/status`, { method: "PATCH", body: { status } });
      load();
    } catch (err) {
      setError(err.message);
    }
  };
 
  const remove = async (a) => {
    if (!window.confirm("Obrisati termin?")) return;
    await api(`/api/appointments/${a.id}`, { method: "DELETE" });
    load();
  };
 
  const setField = (name, value) => setForm({ ...form, [name]: value });
 
  return (
    <div>
      <div className="page-head">
        <h1>Termini</h1>
        <div>
          <input type="date" value={date} onChange={(e) => setDate(e.target.value)} />{" "}
          <button className="btn" onClick={openNew}>+ Novi termin</button>
        </div>
      </div>
 
      {error && <p className="error">{error}</p>}
 
      {form && (
        <form className="card" onSubmit={save}>
          <h3>{form.id ? "Izmjena termina" : "Novi termin"}</h3>
          <div className="grid">
            <Select label="Pacijent" value={form.patientId}
                    onChange={(v) => setField("patientId", v)}
                    options={patients.map((p) => ({ id: p.id, text: `${p.lastName} ${p.firstName}` }))} />
            <Select label="Zubar" value={form.dentistId}
                    onChange={(v) => setField("dentistId", v)}
                    options={dentists.map((d) => ({ id: d.id, text: `${d.lastName} ${d.firstName}` }))} />
            <Select label="Usluga" value={form.treatmentId}
                    onChange={(v) => setField("treatmentId", v)}
                    options={treatments.map((t) => ({ id: t.id, text: `${t.name} (${t.durationMinutes} min)` }))} />
            <label>
              Početak
              <input type="datetime-local" required value={form.startTime}
                     onChange={(e) => setField("startTime", e.target.value)} />
            </label>
            <label>
              Napomena
              <input value={form.note} onChange={(e) => setField("note", e.target.value)} />
            </label>
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
            <th>Vrijeme</th><th>Pacijent</th><th>Zubar</th><th>Usluga</th><th>Status</th><th></th>
          </tr>
        </thead>
        <tbody>
          {items.map((a) => (
            <tr key={a.id}>
              <td>{time(a.startTime)} - {time(a.endTime)}</td>
              <td>{a.patient.lastName} {a.patient.firstName}</td>
              <td>{a.dentist.lastName}</td>
              <td>{a.treatment.name}</td>
              <td>
                <select value={a.status} onChange={(e) => changeStatus(a, e.target.value)}>
                  {Object.entries(STATUS).map(([value, text]) => (
                    <option key={value} value={value}>{text}</option>
                  ))}
                </select>
              </td>
              <td className="actions">
                <button className="btn-light" onClick={() => openEdit(a)}>Uredi</button>{" "}
                <button className="btn-danger" onClick={() => remove(a)}>Obriši</button>
              </td>
            </tr>
          ))}
          {items.length === 0 && <tr><td colSpan={6}>Nema termina za ovaj dan.</td></tr>}
        </tbody>
      </table>
    </div>
  );
}
