import React from "react";

import DemographicPicker from "@/components/DemographicPicker";
import {
  DEMOGRAPHIC_FIELDS,
  EMPTY_DEMOGRAPHIC_FILTER,
  type DemographicFilter,
} from "@/api/ReferenceApi/referenceApi";

function ExploreDataPage() {
  const [filter, setFilter] = React.useState<DemographicFilter>(
    EMPTY_DEMOGRAPHIC_FILTER,
  );

  const onChange = React.useMemo(
    () =>
      DEMOGRAPHIC_FIELDS.map((field) => (newValue: string) => {
        setFilter((prev) => ({ ...prev, [field]: newValue }));
      }),
    [],
  );

  return (
    <div>
      <h2>Explore Data</h2>
      {DEMOGRAPHIC_FIELDS.map((field, i) => (
        <DemographicPicker
          key={field}
          value={filter[field]}
          onChange={onChange[i]}
          demographic={field}
        />
      ))}
    </div>
  );
}

export default ExploreDataPage;
