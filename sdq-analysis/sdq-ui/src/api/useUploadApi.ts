import React from "react";
import useInProgressContext from "../context/InProgressContext";
import useAppNotificationContext from "../context/AppNotificationContext";
import { EMPTY_PARSED_FILE, type ParsedFile } from "./types";

export interface UploadApi {
  lastFile: ParsedFile;
  onSubmitFile: (data: FormData) => Promise<void>;
}

export const EMPTY_UPLOAD_API: UploadApi = {
  lastFile: EMPTY_PARSED_FILE,
  onSubmitFile: () => Promise.reject("default implementation"),
};

function useUploadApi(): UploadApi {
  const [lastFile, setLastFile] = React.useState<ParsedFile>(EMPTY_PARSED_FILE);
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

  const onSubmitFile = React.useCallback(
    (formData: FormData) => {
      const jobId = beginJob("Submitting File");
      return fetch("/api/upload", {
        method: "POST",
        body: formData,
      })
        .then((response) => {
          if (!response.ok) {
            addMessage(
              "danger",
              response.status,
              "Failed to submit file: " + response.statusText
            );
            throw new Error("Network response was not ok");
          }
          addMessage("success", response.status, "File Submitted Successfully");
          return response.json();
        })
        .then((r) => {
          setLastFile(r);
        })
        .finally(() => {
          endJob(jobId);
        });
    },
    [beginJob, endJob, addMessage]
  );

  return {
    lastFile,
    onSubmitFile,
  };
}

export default useUploadApi;
