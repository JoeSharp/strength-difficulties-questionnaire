import React from "react";
import type { IApi } from "./useApi";
import { EMPTY_FILE_API } from "../../api/useClientFileApi";
import { EMPTY_DATABASE_API } from "../../api/useDatabaseApi";
import { EMPTY_UPLOAD_API } from "../../api/useUploadApi";
import { EMPTY_REFERENCE_API } from "../../api/useReferenceApi";

const DEFAULT_CONTEXT: IApi = {
  refresh: () => console.error("default implementation"),
  clientFileApi: EMPTY_FILE_API,
  databaseApi: EMPTY_DATABASE_API,
  uploadApi: EMPTY_UPLOAD_API,
  referenceApi: EMPTY_REFERENCE_API,
};

const ApiContext = React.createContext<IApi>(DEFAULT_CONTEXT);

export default ApiContext;
