import { Link, useParams } from "react-router";
import useClientApi from "../api/clientApi";

function ClientPage() {
  const { id } = useParams();
  const { clients } = useClientApi();
  const client = clients.find((c) => c.id === id);

  if (!client) {
    return <p>Loading...</p>;
  }

  return (
    <div>
      <h2>
        <Link to="/page3">Clients</Link> &gt; {client.codeName}
      </h2>
      <p>ID: {client.id}</p>
      <p>Date of birth: {client.dateOfBirth}</p>
      <p>Gender: {client.gender}</p>
      <p>Council: {client.council}</p>
      <p>Ethnicity: {client.ethnicity}</p>
      <p>EAL: {client.englishAdditionalLanguage}</p>
      <p>Disability status: {client.disabilityStatus}</p>
      <p>Care experience: {client.careExperience}</p>
      <p>Funding source: {client.fundingSource}</p>
    </div>
  );
}

export default ClientPage;
