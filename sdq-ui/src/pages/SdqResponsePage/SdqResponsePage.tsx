import React from "react";
import type { SdqQueryDTO } from "@/api/SdqApi/sdqApi";
import useQuerySdq from "@/api/SdqApi/useQuerySdq";
import useQuerySdqProgress from "@/api/SdqApi/useQuerySdqProgress";
import SdqQueryForm from "@/components/SdqQueryForm";
import SdqSummaryTable from "@/components/SdqSummaryTable";
import SdqProgressTable from "../../components/SdqProgressTable";

const DEFAULT_GOAL_QUERY: SdqQueryDTO = {
  assessor: "School",
  filters: [],
  from: "2024-01-01",
  to: "2026-01-01",
};
function SdqResponsePage() {
  const sdqSummaries = useQuerySdq();
  const sdqProgress = useQuerySdqProgress();
  const [sdqFilter, setSdqFilter] =
    React.useState<SdqQueryDTO>(DEFAULT_GOAL_QUERY);

  const onClickQuery = () => {
    sdqSummaries.mutate(sdqFilter);
    sdqProgress.reset();
  };

  const onClickQueryProgress = () => {
    sdqProgress.mutate(sdqFilter);
    sdqSummaries.reset();
  };

  return (
    <div>
      <h2>Strength Difficulties Responses</h2>
      <SdqQueryForm value={sdqFilter} onChange={setSdqFilter} />
      <div className="button-group">
        <button onClick={onClickQuery}>Search</button>
        <button onClick={onClickQueryProgress}>Search Progress</button>
      </div>

      {sdqSummaries.data && (
        <>
          <h3>{sdqSummaries.data.length} matching SDQ responses found</h3>
          <SdqSummaryTable summaries={sdqSummaries.data} />
        </>
      )}
      {sdqProgress.data && (
        <>
          <h3>
            {sdqProgress.data.length} matching SDQ progress summaries found
          </h3>
          <SdqProgressTable summaries={sdqProgress.data} />
        </>
      )}
    </div>
  );
}

export default SdqResponsePage;
