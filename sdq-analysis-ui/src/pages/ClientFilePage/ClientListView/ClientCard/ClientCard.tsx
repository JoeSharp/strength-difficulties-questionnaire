import type { ClientFile } from "@/api/ClientApi/clientApi";
import useReference from "@/api/ReferenceApi";
import { DEMOGRAPHIC_FIELDS, DEMOGRAPHIC_GETTERS } from "@/api/types";
import ClientViewDetailsButton from "../ClientViewDetailsButton";
import ClientDeleteButton from "../ClientDeleteButton";

import "./ClientCard.scss";

type Props = {
  includeHeader?: boolean;
  client: ClientFile;
};
const ClientCard: React.FC<Props> = ({ includeHeader = true, client }) => {
  const { getLabelForDemographicValue } = useReference();
  return (
    <div className="client-card">
      {includeHeader && (
        <div className="client-card__header">
          <div>
            <h3>Code name: {client.codeName}</h3>
            <p className="client-card__meta">ID: {client.clientId}</p>
          </div>
          <div>
            <ClientViewDetailsButton clientId={client.clientId} />
            <ClientDeleteButton clientId={client.clientId} />
          </div>
        </div>
      )}

      <div className="client-card__grid">
        <div className="client-card__field">
          <dt>Form submitted</dt>
          <dd>
            {client.submittedAt ? (
              <time dateTime={client.submittedAt}>
                {new Date(client.submittedAt).toLocaleString()}
              </time>
            ) : (
              "Not recorded"
            )}
          </dd>
        </div>
        <div className="client-card__field">
          <dt>Date of birth</dt>
          <dd>
            <time dateTime={client.dateOfBirth.toISOString()}>
              {client.dateOfBirth.toLocaleDateString(undefined, {
                timeZone: "UTC",
              })}
            </time>
          </dd>
        </div>
        {DEMOGRAPHIC_FIELDS.map((field) => (
          <div className="client-card__field" key={field}>
            <dt>{field}</dt>
            <dd>
              {
                DEMOGRAPHIC_GETTERS[field](client, getLabelForDemographicValue)
              }
            </dd>
          </div>
        ))}
      </div>
    </div>
  );
};
export default ClientCard;
