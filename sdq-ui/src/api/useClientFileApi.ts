import React from "react";
import useInProgressContext from "../context/InProgressContext";
import useAppNotificationContext from "../context/AppNotificationContext";
import {
  EMPTY_CLIENT_FILE,
  EMPTY_DEMOGRAPHIC_REPORT,
  type DemographicReport,
  type ClientFile,
  type DemographicField,
} from "./types";

export interface ClientFileApi {
  clients: ClientFile[];
  client: ClientFile;
  demographicReport: DemographicReport;
  refresh: () => void;
  getDemographicReport: (demographic: DemographicField) => void;
  getClientById: (uuid: string) => void;
}

export const EMPTY_FILE_API: ClientFileApi = {
  clients: [],
  client: EMPTY_CLIENT_FILE,
  demographicReport: EMPTY_DEMOGRAPHIC_REPORT,
  refresh: () => console.error("default implementation"),
  getDemographicReport: () => console.error("default implementation"),
  getClientById: () => console.error("default implementation"),
};

const BASE_CLIENT_URL = "/api/client";

function parseFile(file: ClientFile): ClientFile {
  return {
    ...file,
    dateOfBirth: new Date(file.dateOfBirth),
  };
}

function useClientFileApi(): ClientFileApi {
  const [clients, setFiles] = React.useState<ClientFile[]>([]);
  const [demographicReport, setDemographicReport] =
    React.useState<DemographicReport>(EMPTY_DEMOGRAPHIC_REPORT);
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();
  const [client, setFile] = React.useState<ClientFile>(EMPTY_CLIENT_FILE);

  const getDemographicReport = React.useCallback((tableName: string) => {
    const jobId = beginJob("Fetching demographc report");
    fetch(`${BASE_CLIENT_URL}/demographic_report/${tableName}`)
      .then((response) => {
        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch demographic report: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }

        return response.json();
      })
      .then((r) => {
        setDemographicReport(r);
      })
      .finally(() => {
        endJob(jobId);
      });
  }, []);

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

  const getFileByUuid = React.useCallback((uuid: string) => {
    const jobId = beginJob("Fetching file");
    fetch(`${BASE_CLIENT_URL}/${uuid}`)
      .then((response) => {
        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch scores: " + response.statusText,
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

  React.useEffect(() => {
    fetchFiles();
  }, [fetchFiles]);

  return {
    clients,
    client,
    demographicReport,
    refresh: fetchFiles,
    getDemographicReport,
    getClientById: getFileByUuid,
  };
}

export default useClientFileApi;
