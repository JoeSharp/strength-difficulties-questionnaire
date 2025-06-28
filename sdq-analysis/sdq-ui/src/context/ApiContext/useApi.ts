import type { UploadFileApi } from '../../api/useUploadFileApi';
import useUploadFileApi from '../../api/useUploadFileApi';

export interface IApi {
    uploadFileApi: UploadFileApi
}

function useApi(): IApi {
    const uploadFileApi = useUploadFileApi();

    return {
        uploadFileApi
    }
}

export default useApi;

