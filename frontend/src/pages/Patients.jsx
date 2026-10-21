import CrudPage from "../components/CrudPage.jsx";
 
export default function Patients() {
  return (
    <CrudPage
      title="Pacijenti"
      endpoint="/api/patients"
      searchable
      fields={[
        { name: "firstName", label: "Ime", required: true },
        { name: "lastName", label: "Prezime", required: true },
        { name: "phone", label: "Telefon", required: true },
        { name: "email", label: "Email", type: "email" },
        { name: "dateOfBirth", label: "Datum rođenja", type: "date" },
        { name: "note", label: "Napomena", type: "textarea" },
      ]}
      columns={[
        { key: "lastName", label: "Prezime" },
        { key: "firstName", label: "Ime" },
        { key: "phone", label: "Telefon" },
        { key: "email", label: "Email" },
        { key: "dateOfBirth", label: "Datum rođenja" },
      ]}
    />
  );
}
