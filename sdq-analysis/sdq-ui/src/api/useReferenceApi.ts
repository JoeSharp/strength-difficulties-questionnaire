import React from "react";
import { EMPTY_REFERENCE_INFO, type ReferenceInfo } from "./types";

export interface ReferenceApi {
  refresh: () => void;
  referenceInfo: ReferenceInfo;
}

export const EMPTY_REFERENCE_API: ReferenceApi = {
  refresh: () => console.error("Default implementation"),
  referenceInfo: EMPTY_REFERENCE_INFO,
};

function useReferenceApi(): ReferenceApi {
  const [referenceInfo, setReferenceInfo] =
    React.useState<ReferenceInfo>(EMPTY_REFERENCE_INFO);

  const refresh = React.useCallback(() => {
    fetch("/api/reference")
      .then((response) => {
        return response.json();
      })
      .then((r) => setReferenceInfo(r))
      .finally(() => {});
  }, []);

  React.useEffect(refresh, []);

  return {
    referenceInfo,
    refresh,
  };
}

export default useReferenceApi;
