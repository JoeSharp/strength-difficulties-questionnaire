import React from "react";
import useInProgressContext from "../context/InProgressContext";
import useApplicationMessageContext from "../context/ApplicationMessageContext";

export interface IUploadFile {
    uuid: string,
    filename: string
}

export interface ISdqScore {
    uuid: string,
    period: number,
    assessor: string,
    categoryScores: Record<string, number>
    postureScores: Record<string, number>
    total: number,
}

export interface UploadFileApi {
    scores: ISdqScore[];
    files: IUploadFile[];
    lastSubmission: object;
    onSubmitFile: (data: FormData) => void;
}

function useUploadFileApi(): UploadFileApi {
    const [scores, setScores] = React.useState<ISdqScore[]>([]);
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

    const onFetchScores = React.useCallback(() => {
        const jobId = beginJob("Fetching scores");
        fetch('/api/upload/scores')
            .then(response => {
                if (!response.ok) {
                    addMessage('danger', response.status, "Failed to fetch scores: " + response.statusText);
                    throw new Error('Network response was not ok');
                }

                return response.json();
            })
            .then(r => {
                setScores(r);
            })
            .finally(() => {
                endJob(jobId);
            })
    }, []);

    const onFetchBoth = React.useCallback(() => {
        onFetchFiles();
        onFetchScores();
    }, [onFetchFiles, onFetchScores]);

    React.useEffect(() => {
        onFetchBoth();
    }, [onFetchBoth]);

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
                onFetchBoth();
            })
    }, [beginJob, endJob, addMessage, onFetchBoth])


    return {
        files,
        scores,
        lastSubmission,
        onSubmitFile
    }

}

export default useUploadFileApi;
