import React from "react";

import type { IUploadFileApi } from '../../api/useUploadFileApi';
import useUploadFileApi from '../../api/useUploadFileApi';

export interface IApi {
    uploadFileApi: IUploadFileApi
}

function useApi(): IApi {
    const uploadFileApi = useUploadFileApi();

    return {
        uploadFileApi
    }
}

export default useApi;

