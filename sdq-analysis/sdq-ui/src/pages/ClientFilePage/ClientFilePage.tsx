import React from "react";
import { useParams } from "react-router-dom";
import useApiContext from "@/context/ApiContext";
import SdqScoresTable from "@/components/SdqScoresTable";
import GboTable from "@/components/GboTable";
import type { Assessor } from "@/api/types";

function ClientFilePage() {
  const { id } = useParams();
  const {
    clientFileApi: {
      getFileByUuid,
      getScoresByUuid,
      getGboByUuid,
      file,
      scores,
      gbo,
    },
  } = useApiContext();

  React.useEffect(() => {
    if (!!id) {
      getFileByUuid(id);
      getScoresByUuid(id);
      getGboByUuid(id);
    }
  }, [id, getFileByUuid, getScoresByUuid]);

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
      <h3>SDQ Scores</h3>
      {Object.entries(scores).map(([assessor, scoreValues]) => (
        <SdqScoresTable
          key={assessor}
          assessor={assessor as Assessor}
          scores={scoreValues}
        />
      ))}
      <h3>GBO Scores</h3>
      {Object.entries(gbo).map(([assessor, gboValues]) => (
        <GboTable
          key={assessor}
          assessor={assessor as Assessor}
          scores={gboValues}
        />
      ))}
    </div>
  );
}

export default ClientFilePage;
