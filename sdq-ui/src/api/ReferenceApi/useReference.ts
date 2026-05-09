import { useQuery } from "@tanstack/react-query";

import { EMPTY_REFERENCE_INFO, type ReferenceInfo } from "./referenceApi";
import type { DemographicField } from "../types";

type RefInfoApi = {
  data: ReferenceInfo;
  getLabelForDemographicValue: (
    field: DemographicField,
    value: string,
  ) => string;
  getLabelForGoalType: (value: string) => string;
};

function useReference(): RefInfoApi {
  const query = useQuery<ReferenceInfo>({
    queryKey: ["referenceInfo"],
    queryFn: async () => {
      const response = await fetch("/api/reference");
      if (!response.ok) {
        throw new Error("Failed to fetch reference info");
      }
      const json = await response.json();
      return json;
    },
  });

  const getLabelForDemographicValue = (
    field: DemographicField,
    value: string,
  ) => {
    const options = query.data?.demographicFields[field];
    const option = options?.find((o) => o.value === value);
    return option ? option.label : value;
  };

  const getLabelForGoalType = (value: string) => {
    const options = query.data?.goalTypes;
    const option = options?.find((o) => o.value === value);
    return option ? option.label : value;
  };

  return {
    data: query.data ?? EMPTY_REFERENCE_INFO,
    getLabelForDemographicValue,
    getLabelForGoalType,
  };
}

export default useReference;
