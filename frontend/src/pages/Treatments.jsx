import CrudPage from "../components/CrudPage.jsx";
 
export default function Treatments() {
  return (
    <CrudPage
      title="Usluge"
      endpoint="/api/treatments"
      fields={[
        { name: "name", label: "Naziv", required: true },
        { name: "price", label: "Cijena (KM)", type: "number", required: true },
        { name: "durationMinutes", label: "Trajanje (min)", type: "number", required: true },
        { name: "description", label: "Opis", type: "textarea" },
      ]}
      columns={[
        { key: "name", label: "Naziv" },
        { key: "price", label: "Cijena", render: (t) => `${t.price} KM` },
        { key: "durationMinutes", label: "Trajanje", render: (t) => `${t.durationMinutes} min` },
      ]}
    />
  );
}
