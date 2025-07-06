import React from "react";
import { EMPTY_REFERENCE_INFO, type ReferenceInfo } from "./types";

export interface ReferenceApi {
  referenceInfo: ReferenceInfo;
}

export const EMPTY_REFERENCE_API: ReferenceApi = {
  referenceInfo: EMPTY_REFERENCE_INFO,
};

function useReferenceApi(): ReferenceApi {
  const [referenceInfo, setReferenceInfo] =
    React.useState<ReferenceInfo>(EMPTY_REFERENCE_INFO);

  React.useEffect(() => {
    fetch("/api/reference")
      .then((response) => {
        return response.json();
      })
      .then((r) => setReferenceInfo(r))
      .finally(() => {});
  }, []);

  return {
    referenceInfo,
  };
}

export default useReferenceApi;
