import { useParams } from "react-router-dom";
import useClient from "@/api/ClientApi/useClient";
import InProgressSpinner from "@/components/InProgressSpinner";

function ClientFilePage() {
  const { id } = useParams();
  const { data: client } = useClient(id);

  if (!client) {
    return <InProgressSpinner />;
  }

  return (
    <div>
      <h2>Client file {id}</h2>
      <dl className="two-columns">
        <dt>Gender</dt>
        <dd>{client.gender}</dd>
        <dt>Council</dt>
        <dd>{client.council}</dd>
        <dt>Ethnicity</dt>
        <dd>{client.ethnicity}</dd>
        <dt>EAL</dt>
        <dd>{client.englishAdditionalLanguage}</dd>
        <dt>Disability</dt>
        <dd>
          {client.disabilityStatus} - {client.disabilityType}
        </dd>
        <dt>Care Experience</dt>
        <dd>{client.careExperience}</dd>
        <dt>Intervention Types</dt>
        <dd>{client.interventionTypes.join(",")}</dd>
        <dt>ACES</dt>
        <dd>{client.aces}</dd>
        <dt>Funding Source</dt>
        <dd>{client.fundingSource}</dd>
      </dl>
    </div>
  );
}

export default ClientFilePage;
