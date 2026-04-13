import React from "react";
import { useMutation } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import { EMPTY_PARSED_FILE, type ParsedFile } from "./uploadApi";

interface IUseUploadApi {
  lastFile: ParsedFile;
  onSubmitFile: (formData: FormData) => void;
}

function useUploadApi(): IUseUploadApi {
  const [lastFile, setLastFile] = React.useState<ParsedFile>(EMPTY_PARSED_FILE);
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

  const mutation = useMutation({
    mutationFn: async (formData: FormData) => {
      const jobId = beginJob("Submitting File");

      try {
        const response = await fetch("/api/upload", {
          method: "POST",
          body: formData,
        });

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to submit file: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }

        addMessage("success", response.status, "File Submitted Successfully");
        return await response.json();
      } finally {
        endJob(jobId);
      }
    },
    onSuccess: (parsed: ParsedFile) => {
      setLastFile(parsed);
    },
  });

  return {
    lastFile,
    onSubmitFile: mutation.mutateAsync,
  };
}

export default useUploadApi;
