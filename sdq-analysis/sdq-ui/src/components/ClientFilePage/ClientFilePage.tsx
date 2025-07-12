import React from "react";
import { Link, useParams } from "react-router-dom";
import useApiContext from "../../context/ApiContext";
import SdqScoresTable from "../SdqScoresTable";

function ClientFilePage() {
  const { id } = useParams();
  const {
    clientFileApi: { getFileByUuid, getScoresByUuid, file, scores },
  } = useApiContext();

  React.useEffect(() => {
    if (!!id) {
      getFileByUuid(id);
      getScoresByUuid(id);
    }
  }, [id, getFileByUuid, getScoresByUuid]);

  return (
    <div>
      <nav>
        <Link className="btn btn-primary" to="/">
          Home
        </Link>
      </nav>
      <h3>Client file {id}</h3>
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
      <SdqScoresTable scores={scores} />
    </div>
  );
}

export default ClientFilePage;
