import type { DatabaseApi } from "../../api/useDatabaseApi";
import useDatabaseApi from "../../api/useDatabaseApi";
import type { ClientFileApi } from "../../api/useClientFileApi";
import useClientFileApi from "../../api/useClientFileApi";
import type { UploadApi } from "../../api/useUploadApi";
import useUploadApi from "../../api/useUploadApi";
import type { ReferenceApi } from "../../api/useReferenceApi";
import useReferenceApi from "../../api/useReferenceApi";

export interface IApi {
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

  return {
    clientFileApi,
    databaseApi,
    uploadApi,
    referenceApi,
  };
}

export default useApi;
