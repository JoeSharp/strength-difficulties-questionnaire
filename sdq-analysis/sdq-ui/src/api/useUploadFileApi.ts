import React from "react";
import useInProgressContext from "../context/InProgressContext";
import useApplicationMessageContext from "../context/ApplicationMessageContext";

export interface IUploadFile {
    uuid: string,
    filename: string
}

export interface UploadFileApi {
    files: IUploadFile[];
    lastSubmission: object;
    onSubmitFile: (data: FormData) => void;
}

function useUploadFileApi(): UploadFileApi {
    const [files, setFiles] = React.useState<IUploadFile[]>([]);
    const { addMessage } = useApplicationMessageContext();
    const { beginJob, endJob } = useInProgressContext();
    const [lastSubmission, setLastSubmission] = React.useState<object>({});

    const onFetchFiles = React.useCallback(() => {
        const jobId = beginJob("Fetching files");
        fetch('/api/upload')
            .then(response => {
                if (!response.ok) {
                    addMessage('danger', response.status, "Failed to fetch file: " + response.statusText);
                    throw new Error('Network response was not ok');
                }

                return response.json();
            })
            .then(r => {
                setFiles(r);
            })
            .finally(() => {
                endJob(jobId);
            })
    }, []);

    React.useEffect(() => {
        onFetchFiles();
    }, [onFetchFiles]);

    const onSubmitFile = React.useCallback((formData: FormData) => {
        const jobId = beginJob("Submitting File");
        fetch('/api/upload', {
            method: 'POST',
            body: formData
        }).then(response => {
            if (!response.ok) {
                addMessage('danger', response.status, "Failed to submit file: " + response.statusText);
                throw new Error('Network response was not ok');
            }
            addMessage('success', response.status, 'File Submitted Successfully');
            return response.json();
        })
            .then(r => {
                setLastSubmission(r);
            })
            .finally(() => {
                endJob(jobId);
                onFetchFiles();
            })
    }, [beginJob, endJob, addMessage, onFetchFiles])


    return {
        files,
        lastSubmission,
        onSubmitFile
    }

}

export default useUploadFileApi;
