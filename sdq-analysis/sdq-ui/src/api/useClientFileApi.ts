import React from "react";
import useInProgressContext from "../context/InProgressContext";
import useApplicationMessageContext from "../context/ApplicationMessageContext";
import {
  EMPTY_CLIENT_FILE,
  type ClientFile,
  type SdqScoresSummary,
} from "./types";

export interface ClientFileApi {
  scores: SdqScoresSummary[];
  files: ClientFile[];
  file: ClientFile;
  getFileByUuid: (uuid: string) => void;
  getScoresByUuid: (uuid: string) => void;
}

export const EMPTY_FILE_API: ClientFileApi = {
  scores: [],
  files: [],
  file: EMPTY_CLIENT_FILE,
  getFileByUuid: () => console.error("default implementation"),
  getScoresByUuid: () => console.error("default implementation"),
};

const BASE_CLIENT_URL = "/api/client";

function useClientFileApi(): ClientFileApi {
  const [scores, setScores] = React.useState<SdqScoresSummary[]>([]);
  const [files, setFiles] = React.useState<ClientFile[]>([]);
  const { addMessage } = useApplicationMessageContext();
  const { beginJob, endJob } = useInProgressContext();
  const [file, setFile] = React.useState<ClientFile>(EMPTY_CLIENT_FILE);

  const fetchFiles = React.useCallback(() => {
    const jobId = beginJob("Fetching files");
    fetch(BASE_CLIENT_URL)
      .then((response) => {
        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch file: " + response.statusText
          );
          throw new Error("Network response was not ok");
        }

        return response.json();
      })
      .then((r) => {
        setFiles(r);
      })
      .finally(() => {
        endJob(jobId);
      });
  }, []);

  const getFileByUuid = React.useCallback((uuid: string) => {
    const jobId = beginJob("Fetching file");
    fetch(`${BASE_CLIENT_URL}/${uuid}`)
      .then((response) => {
        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch scores: " + response.statusText
          );
          throw new Error("Network response was not ok");
        }

        return response.json();
      })
      .then((r) => {
        setFile(r);
      })
      .finally(() => {
        endJob(jobId);
      });
  }, []);

  const getScoresByUuid = React.useCallback((uuid: string) => {
    const jobId = beginJob("Fetching scores");
    fetch(`${BASE_CLIENT_URL}/scores/${uuid}`)
      .then((response) => {
        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch scores: " + response.statusText
          );
          throw new Error("Network response was not ok");
        }

        return response.json();
      })
      .then((r) => {
        setScores(r);
      })
      .finally(() => {
        endJob(jobId);
      });
  }, []);

  React.useEffect(() => {
    fetchFiles();
  }, [fetchFiles]);

  return {
    files,
    file,
    scores,
    getFileByUuid,
    getScoresByUuid,
  };
}

export default useClientFileApi;
