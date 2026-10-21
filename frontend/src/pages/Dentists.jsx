import CrudPage from "../components/CrudPage.jsx";
 
export default function Dentists() {
  return (
    <CrudPage
      title="Zubari"
      endpoint="/api/dentists"
      fields={[
        { name: "firstName", label: "Ime", required: true },
        { name: "lastName", label: "Prezime", required: true },
        { name: "specialization", label: "Specijalizacija" },
        { name: "phone", label: "Telefon" },
        { name: "email", label: "Email", type: "email" },
        { name: "active", label: "Aktivan", type: "checkbox" },
      ]}
      columns={[
        { key: "lastName", label: "Prezime" },
        { key: "firstName", label: "Ime" },
        { key: "specialization", label: "Specijalizacija" },
        { key: "phone", label: "Telefon" },
        { key: "active", label: "Aktivan", render: (d) => (d.active ? "Da" : "Ne") },
      ]}
    />
  );
}
