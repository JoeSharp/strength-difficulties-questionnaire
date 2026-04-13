import React from "react";
import useInProgressContext from "../context/InProgressContext";
import useAppNotificationContext from "../context/AppNotificationContext";
import {
  BASE_CLIENT_URL,
  parseFile,
  type ClientFile,
} from "./ClientApi/clientApi";

export interface ClientFileApi {
  clients: ClientFile[];
  refresh: () => void;
}

export const EMPTY_FILE_API: ClientFileApi = {
  clients: [],
  refresh: () => console.error("default implementation"),
};

function useClientFileApi(): ClientFileApi {
  const [clients, setFiles] = React.useState<ClientFile[]>([]);
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

  const fetchFiles = React.useCallback(() => {
    const jobId = beginJob("Fetching files");
    fetch(BASE_CLIENT_URL)
      .then((response) => {
        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch file: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }

        return response.json();
      })
      .then((r) => {
        setFiles(r.map(parseFile));
      })
      .finally(() => {
        endJob(jobId);
      });
  }, []);

  React.useEffect(() => {
    fetchFiles();
  }, [fetchFiles]);

  return {
    clients,
    refresh: fetchFiles,
  };
}

export default useClientFileApi;
