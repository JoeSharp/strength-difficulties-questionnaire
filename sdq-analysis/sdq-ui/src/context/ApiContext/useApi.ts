import React from "react";

import type { DatabaseApi } from "../../api/useDatabaseApi";
import useDatabaseApi from "../../api/useDatabaseApi";
import type { ClientFileApi } from "../../api/useClientFileApi";
import useClientFileApi from "../../api/useClientFileApi";
import type { UploadApi } from "../../api/useUploadApi";
import useUploadApi from "../../api/useUploadApi";
import type { ReferenceApi } from "../../api/useReferenceApi";
import useReferenceApi from "../../api/useReferenceApi";

export interface IApi {
  refresh: () => void;
  clientFileApi: ClientFileApi;
  databaseApi: DatabaseApi;
  uploadApi: UploadApi;
  referenceApi: ReferenceApi;
}

function useApi(): IApi {
  const clientFileApi = useClientFileApi();
  const databaseApi = useDatabaseApi();
  const uploadApi = useUploadApi();
  const referenceApi = useReferenceApi();

  const refresh = React.useCallback(() => {
    referenceApi.refresh();
    databaseApi.refresh();
  }, [referenceApi.refresh, databaseApi.refresh]);

  return {
    refresh,
    clientFileApi,
    databaseApi,
    uploadApi,
    referenceApi,
  };
}

export default useApi;
