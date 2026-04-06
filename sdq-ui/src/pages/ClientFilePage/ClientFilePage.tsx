import React from "react";
import { useParams } from "react-router-dom";
import useApiContext from "@/context/ApiContext";

function ClientFilePage() {
  const { id } = useParams();
  const {
    clientFileApi: { getClientById, client: file },
  } = useApiContext();

  React.useEffect(() => {
    if (!!id) {
      getClientById(id);
    }
  }, [id, getClientById]);

  return (
    <div>
      <h2>Client file {id}</h2>
      <dl className="two-columns">
        <dt>Gender</dt>
        <dd>{file.gender}</dd>
        <dt>Council</dt>
        <dd>{file.council}</dd>
        <dt>Ethnicity</dt>
        <dd>{file.ethnicity}</dd>
        <dt>EAL</dt>
        <dd>{file.englishAdditionalLanguage}</dd>
        <dt>Disability</dt>
        <dd>
          {file.disabilityStatus} - {file.disabilityType}
        </dd>
        <dt>Care Experience</dt>
        <dd>{file.careExperience}</dd>
        <dt>Intervention Types</dt>
        <dd>{file.interventionTypes.join(",")}</dd>
        <dt>ACES</dt>
        <dd>{file.aces}</dd>
        <dt>Funding Source</dt>
        <dd>{file.fundingSource}</dd>
      </dl>
    </div>
  );
}

export default ClientFilePage;
