import React from "react";
import type { SdqFilterDTO } from "@/api/SdqApi/sdqApi";
import useFilterSdqSummaries from "@/api/SdqApi/useFilterSdqSummaries";
import SdqQueryForm from "@/components/SdqQueryForm";
import SdqSummaryTable from "@/components/SdqSummaryTable";

const DEFAULT_GOAL_QUERY: SdqFilterDTO = {
  assessor: "School",
  filters: [],
  from: "2024-01-01",
  to: "2026-01-01",
};
function SdqResponsePage() {
  const querySdqSummaries = useFilterSdqSummaries();
  const [sdqFilter, setSdqFilter] =
    React.useState<SdqFilterDTO>(DEFAULT_GOAL_QUERY);

  const onClickQuery = React.useCallback(() => {
    querySdqSummaries.mutate(sdqFilter);
  }, [sdqFilter, querySdqSummaries]);

  return (
    <div>
      <h2>Strength Difficulties Responses</h2>
      <SdqQueryForm value={sdqFilter} onChange={setSdqFilter} />
      <button onClick={onClickQuery}>Search</button>

      {querySdqSummaries.data && (
        <>
          <h3>{querySdqSummaries.data.length} matching SDQ responses found</h3>
          <SdqSummaryTable submissions={querySdqSummaries.data} />
        </>
      )}
    </div>
  );
}

export default SdqResponsePage;
