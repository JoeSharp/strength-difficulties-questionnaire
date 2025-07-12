import React from "react";
import useInProgressContext from "../context/InProgressContext";
import useAppNotificationContext from "../context/AppNotificationContext";
import {
  EMPTY_CLIENT_FILE,
  type ClientFile,
  type GboSummary,
  type SdqScoresSummary,
} from "./types";

export interface ClientFileApi {
  gbo: GboSummary[];
  scores: SdqScoresSummary[];
  files: ClientFile[];
  file: ClientFile;
  refresh: () => void;
  getFileByUuid: (uuid: string) => void;
  getScoresByUuid: (uuid: string) => void;
  getGboByUuid: (uuid: string) => void;
}

export const EMPTY_FILE_API: ClientFileApi = {
  gbo: [],
  scores: [],
  files: [],
  file: EMPTY_CLIENT_FILE,
  refresh: () => console.error("default implementation"),
  getFileByUuid: () => console.error("default implementation"),
  getScoresByUuid: () => console.error("default implementation"),
  getGboByUuid: () => console.error("default implementation"),
};

const BASE_CLIENT_URL = "/api/client";

function parseFile(file: ClientFile): ClientFile {
  return {
    ...file,
    dateOfBirth: new Date(file.dateOfBirth),
  };
}

function useClientFileApi(): ClientFileApi {
  const [gbo, setGbo] = React.useState<GboSummary[]>([]);
  const [scores, setScores] = React.useState<SdqScoresSummary[]>([]);
  const [files, setFiles] = React.useState<ClientFile[]>([]);
  const { addMessage } = useAppNotificationContext();
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
        setFiles(r.map(parseFile));
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
        setFile(parseFile(r));
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

  const getGboByUuid = React.useCallback((uuid: string) => {
    const jobId = beginJob("Fetching gbo");
    fetch(`${BASE_CLIENT_URL}/gbo/${uuid}`)
      .then((response) => {
        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch gbo: " + response.statusText
          );
          throw new Error("Network response was not ok");
        }

        return response.json();
      })
      .then((r) => {
        setGbo(r);
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
    gbo,
    refresh: fetchFiles,
    getFileByUuid,
    getScoresByUuid,
    getGboByUuid,
  };
}

export default useClientFileApi;
