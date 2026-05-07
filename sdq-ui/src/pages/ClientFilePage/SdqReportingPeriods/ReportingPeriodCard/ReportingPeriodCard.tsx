import type { ReportingPeriod } from "@/api/SdqApi/sdqApi";
import useQuerySdqSummary from "@/api/SdqApi/useQuerySdqSummary";
import type { Assessor } from "@/api/types";
import KeyedCountCell from "@/components/SdqSummaryTable/KeyedCountCell";

type Props = {
  assessor: Assessor;
  reportingPeriod: ReportingPeriod;
};

const ReportingPeriodCard: React.FC<Props> = ({
  reportingPeriod,
  assessor,
}) => {
  const { data: sdqSummary } = useQuerySdqSummary(
    reportingPeriod.periodId,
    assessor,
  );
  return (
    <div className="reporting-period-card">
      <h3>{reportingPeriod.period}</h3>
      {sdqSummary && (
        <div>
          <KeyedCountCell data={sdqSummary.categorySubTotals} />
          <KeyedCountCell data={sdqSummary.postureSubTotals} />
          <p>Total Difficulties: {sdqSummary.totalDifficulties}</p>
        </div>
      )}
    </div>
  );
};

export default ReportingPeriodCard;
