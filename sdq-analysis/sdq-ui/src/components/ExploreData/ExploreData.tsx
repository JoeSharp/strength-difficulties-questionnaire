import React from "react";

import {
  DEMOGRAPHIC_FIELDS,
  EMPTY_DEMOGRAPHIC_FILTER,
  type DemographicFilter,
} from "../../api/types";
import DemographicPicker from "../DemographicPicker";

function ExploreData() {
  const [filter, setFilter] = React.useState<DemographicFilter>(
    EMPTY_DEMOGRAPHIC_FILTER
  );

  const onChange = React.useMemo(
    () =>
      DEMOGRAPHIC_FIELDS.map((field) => (newValue: string) => {
        setFilter((prev) => ({ ...prev, [field]: newValue }));
      }),
    []
  );

  return (
    <div>
      <h4>Explore Data</h4>
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

export default ExploreData;
