import { useEffect, useState } from "react";
import { api } from "../api.js";
 
export default function Dashboard() {
  const [patients, setPatients] = useState(0);
  const [dentists, setDentists] = useState(0);
  const [today, setToday] = useState([]);
 
  useEffect(() => {
    api("/api/patients").then((list) => setPatients(list.length));
    api("/api/dentists").then((list) => setDentists(list.length));
    api("/api/appointments").then(setToday); // bez datuma = današnji termini
  }, []);
 
  return (
    <div>
      <h1>Pregled</h1>
      <div className="stats">
        <div className="card stat"><strong>{today.length}</strong>Današnji termini</div>
        <div className="card stat"><strong>{patients}</strong>Pacijenti</div>
        <div className="card stat"><strong>{dentists}</strong>Zubari</div>
      </div>
 
      <h3>Današnji termini</h3>
      <table>
        <thead>
          <tr><th>Vrijeme</th><th>Pacijent</th><th>Zubar</th><th>Usluga</th></tr>
        </thead>
        <tbody>
          {today.map((a) => (
            <tr key={a.id}>
              <td>{a.startTime.slice(11, 16)}</td>
              <td>{a.patient.lastName} {a.patient.firstName}</td>
              <td>{a.dentist.lastName}</td>
              <td>{a.treatment.name}</td>
            </tr>
          ))}
          {today.length === 0 && <tr><td colSpan={4}>Danas nema termina.</td></tr>}
        </tbody>
      </table>
    </div>
  );
}
