import type { ClientFile } from "@/api/ClientApi/clientApi";
import ClientViewDetailsButton from "../ClientViewDetailsButton";
import ClientDeleteButton from "../ClientDeleteButton";

import "./ClientCard.scss";

type Props = {
  includeHeader?: boolean;
  client: ClientFile;
};
const ClientCard: React.FC<Props> = ({ includeHeader = true, client }) => {
  return (
    <div className="client-card">
      {includeHeader && (
        <div className="client-card__header">
          <div>
            <h3>{client.codeName}</h3>
            <p className="client-card__meta">ID: {client.clientId}</p>
          </div>
          <div>
            <ClientViewDetailsButton clientId={client.clientId} />
            <ClientDeleteButton clientId={client.clientId} />
          </div>
        </div>
      )}

      <dl className="client-card__grid">
        <div className="client-card__field">
          <dt>Gender</dt>
          <dd>{client.gender}</dd>
        </div>
        <div className="client-card__field">
          <dt>Council</dt>
          <dd>{client.council}</dd>
        </div>
        <div className="client-card__field">
          <dt>Ethnicity</dt>
          <dd>{client.ethnicity}</dd>
        </div>
        <div className="client-card__field">
          <dt>EAL</dt>
          <dd>{client.englishAdditionalLanguage}</dd>
        </div>
        <div className="client-card__field">
          <dt>Disability</dt>
          <dd>
            {client.disabilityStatus} - {client.disabilityType}
          </dd>
        </div>
        <div className="client-card__field">
          <dt>Care experience</dt>
          <dd>{client.careExperience}</dd>
        </div>
        <div className="client-card__field">
          <dt>Intervention types</dt>
          <dd>{client.interventionTypes.join(", ")}</dd>
        </div>
        <div className="client-card__field">
          <dt>ACES</dt>
          <dd>{client.aces}</dd>
        </div>
        <div className="client-card__field">
          <dt>Funding source</dt>
          <dd>{client.fundingSource}</dd>
        </div>
      </dl>
    </div>
  );
};

export default ClientCard;
