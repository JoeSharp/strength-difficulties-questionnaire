import React from "react";
import useInProgressContext from "../context/InProgressContext";
import useAppNotificationContext from "../context/AppNotificationContext";
import {
  EMPTY_CLIENT_FILE,
  EMPTY_GBQ as EMPTY_GBO,
  EMPTY_SDQ,
  EMPTY_DEMOGRAPHIC_REPORT,
  type DemographicReport,
  type Assessor,
  type ClientFile,
  type GboScore,
  type GboScoreByAssessor,
  type SdqScoreByAssessor,
  type DemographicField,
} from "./types";

export interface ClientFileApi {
  gbo: GboScoreByAssessor;
  scores: SdqScoreByAssessor;
  files: ClientFile[];
  file: ClientFile;
  demographicReport: DemographicReport;
  refresh: () => void;
  getDemographicReport: (demographic: DemographicField) => void;
  getFileByUuid: (uuid: string) => void;
  getScoresByUuid: (uuid: string) => void;
  getGboByUuid: (uuid: string) => void;
}

export const EMPTY_FILE_API: ClientFileApi = {
  gbo: EMPTY_GBO,
  scores: EMPTY_SDQ,
  files: [],
  file: EMPTY_CLIENT_FILE,
  demographicReport: EMPTY_DEMOGRAPHIC_REPORT,
  refresh: () => console.error("default implementation"),
  getDemographicReport: () => console.error("default implementation"),
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
function parseGbo(gbo: GboScore): GboScore {
  return {
    ...gbo,
    periodDate: new Date(gbo.periodDate),
  };
}

function parseGboSummary(gboSummary: GboScoreByAssessor): GboScoreByAssessor {
  return Object.entries(gboSummary)
    .map(([assessor, gbos]) => [assessor, gbos.map(parseGbo)])
    .reduce((acc, [k, v]) => ({ ...acc, [k as Assessor]: v }), EMPTY_GBO);
}

function useClientFileApi(): ClientFileApi {
  const [gbo, setGbo] = React.useState<GboScoreByAssessor>(EMPTY_GBO);
  const [scores, setScores] = React.useState<SdqScoreByAssessor>(EMPTY_SDQ);
  const [files, setFiles] = React.useState<ClientFile[]>([]);
  const [demographicReport, setDemographicReport] =
    React.useState<DemographicReport>(EMPTY_DEMOGRAPHIC_REPORT);
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();
  const [file, setFile] = React.useState<ClientFile>(EMPTY_CLIENT_FILE);

  const getDemographicReport = React.useCallback((tableName: string) => {
    const jobId = beginJob("Fetching demographc report");
    fetch(`${BASE_CLIENT_URL}/demographic_report/${tableName}`)
      .then((response) => {
        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch demographic report: " + response.statusText
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

  const getScoresByUuid = React.useCallback((fileId: string) => {
    const jobId = beginJob("Fetching SDQ");
    fetch(`${BASE_CLIENT_URL}/sdq/${fileId}`)
      .then((response) => {
        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch SDQ: " + response.statusText
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

  const getGboByUuid = React.useCallback((fileId: string) => {
    const jobId = beginJob("Fetching gbo");
    fetch(`${BASE_CLIENT_URL}/gbo/${fileId}`)
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
        setGbo(parseGboSummary(r));
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
    demographicReport,
    refresh: fetchFiles,
    getDemographicReport,
    getFileByUuid,
    getScoresByUuid,
    getGboByUuid,
  };
}

export default useClientFileApi;
