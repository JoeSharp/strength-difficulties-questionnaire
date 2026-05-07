import React from "react";
import { Link, useParams } from "react-router-dom";
import useClient from "@/api/ClientApi/useClient";
import InProgressSpinner from "@/components/InProgressSpinner";
import ClientCard from "@/pages/ClientFilePage/ClientListView/ClientCard";
import AssessorPicker from "@/components/AssessorPicker/AssessorPicker";
import { type Assessor } from "@/api/types";

import ClientGoals from "./ClientGoals";
import SdqReportingPeriods from "./SdqReportingPeriods";

function ClientFilePage() {
  const { id } = useParams();
  const { data: client } = useClient(id);
  const [assessor, setAssessor] = React.useState<Assessor>("School");

  if (!client) {
    return <InProgressSpinner />;
  }

  return (
    <div>
      <h2>
        <Link to="..">Client List</Link> &gt; Client file {client.codeName}
      </h2>
      <ClientCard client={client} includeHeader={false} />

      <div className="form-group">
        <label htmlFor="assessor">View as Assessor</label>
        <AssessorPicker value={assessor} onChange={setAssessor} />
      </div>

      <ClientGoals assessor={assessor} clientId={client.clientId} />
      <SdqReportingPeriods assessor={assessor} clientId={client.clientId} />
    </div>
  );
}

export default ClientFilePage;
