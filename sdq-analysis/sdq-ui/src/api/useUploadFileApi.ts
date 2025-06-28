import React from "react";
import useInProgressContext from "../context/InProgressContext";
import useApplicationMessageContext from "../context/ApplicationMessageContext";

interface UploadFileApi {
    lastSubmission: object;
    onSubmitFile: (data: FormData) => void;
}

function useUploadFileApi(): UploadFileApi {
    const { addMessage } = useApplicationMessageContext();
    const { beginJob, endJob } = useInProgressContext();
    const [lastSubmission, setLastSubmission] = React.useState<object>({});

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
            })
    }, [beginJob, endJob, addMessage])


    return {
        lastSubmission,
        onSubmitFile
    }

}

export default useUploadFileApi;