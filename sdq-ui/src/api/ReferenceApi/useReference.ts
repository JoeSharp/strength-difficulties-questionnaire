import { useQuery } from "@tanstack/react-query";

import { EMPTY_REFERENCE_INFO, type ReferenceInfo } from "./referenceApi";

function useReference(): ReferenceInfo {
  const query = useQuery({
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

  return query.data ?? EMPTY_REFERENCE_INFO;
}

export default useReference;
