import React from "react";
import useInProgressContext from "../context/InProgressContext";
import useAppNotificationContext from "../context/AppNotificationContext";
import {
  EMPTY_DEMOGRAPHIC_REPORT,
  type DemographicReport,
  type ClientFile,
} from "./types";
import { BASE_CLIENT_URL, parseFile } from "./ClientApi/clientApi";
import type { DemographicField } from "./ReferenceApi/referenceApi";

export interface ClientFileApi {
  clients: ClientFile[];
  demographicReport: DemographicReport;
  refresh: () => void;
  getDemographicReport: (demographic: DemographicField) => void;
}

export const EMPTY_FILE_API: ClientFileApi = {
  clients: [],
  demographicReport: EMPTY_DEMOGRAPHIC_REPORT,
  refresh: () => console.error("default implementation"),
  getDemographicReport: () => console.error("default implementation"),
};

function useClientFileApi(): ClientFileApi {
  const [clients, setFiles] = React.useState<ClientFile[]>([]);
  const [demographicReport, setDemographicReport] =
    React.useState<DemographicReport>(EMPTY_DEMOGRAPHIC_REPORT);
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

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

  React.useEffect(() => {
    fetchFiles();
  }, [fetchFiles]);

  return {
    clients,
    demographicReport,
    refresh: fetchFiles,
    getDemographicReport,
  };
}

export default useClientFileApi;
