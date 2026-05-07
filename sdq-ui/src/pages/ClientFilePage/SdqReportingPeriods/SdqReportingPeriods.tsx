import useSdqReportingPeriods from "@/api/SdqApi/useSdqReportingPeriods";
import type { Assessor } from "@/api/types";
import ReportingPeriodCard from "./ReportingPeriodCard";

import "./SdqReportingPeriods.scss";
import SdqProgressCard from "../SdqProgressCard/SdqProgressCard";

type Props = {
  clientId: string;
  assessor: Assessor;
};

const SdqReportingPeriods: React.FC<Props> = ({ clientId, assessor }) => {
  const { data: reportingPeriods } = useSdqReportingPeriods(clientId);
  return (
    <div>
      <h2>SDQ Reporting Periods</h2>
      <div className="reporting-periods-container">
        {reportingPeriods ? (
          reportingPeriods.map((period) => (
            <ReportingPeriodCard
              key={period.periodId}
              reportingPeriod={period}
              assessor={assessor}
            />
          ))
        ) : (
          <p>No reporting periods found.</p>
        )}
        <SdqProgressCard assessor={assessor} clientId={clientId} />
      </div>
    </div>
  );
};

export default SdqReportingPeriods;
