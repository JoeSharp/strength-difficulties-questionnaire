import React from "react";

import type { ClientFileApi } from "../../api/useClientFileApi";
import useClientFileApi from "../../api/useClientFileApi";
import type { UploadApi } from "../../api/useUploadApi";
import useUploadApi from "../../api/useUploadApi";
import type { ReferenceApi } from "../../api/useReferenceApi";
import useReferenceApi from "../../api/useReferenceApi";

export interface IApi {
  refresh: () => void;
  clientFileApi: ClientFileApi;
  uploadApi: UploadApi;
  referenceApi: ReferenceApi;
}

function useApi(): IApi {
  const clientFileApi = useClientFileApi();
  const uploadApi = useUploadApi();
  const referenceApi = useReferenceApi();

  const refresh = React.useCallback(() => {
    clientFileApi.refresh();
    referenceApi.refresh();
  }, [referenceApi.refresh]);

  return {
    refresh,
    clientFileApi,
    uploadApi,
    referenceApi,
  };
}

export default useApi;
