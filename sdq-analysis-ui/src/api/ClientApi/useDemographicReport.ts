import { useQuery } from "@tanstack/react-query";
import useAppNotificationContext from "@/context/AppNotificationContext";
import useInProgressContext from "@/context/InProgressContext";
import {
  BASE_CLIENT_URL,
  EMPTY_DEMOGRAPHIC_REPORT,
  type DemographicReport,
} from "./clientApi";

interface IUseDemographicReport {
  demographicReport: DemographicReport;
  refresh: () => void;
}

function useDemographicReport(tableName: string): IUseDemographicReport {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();
  const query = useQuery<DemographicReport, Error, void>({
    queryKey: ["demographicReport", tableName],
    queryFn: async () => {
      const jobId = beginJob("Fetching demographic report");

      try {
        const response = await fetch(
          `${BASE_CLIENT_URL}/demographic_report/${tableName}`,
        );

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch demographic report: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }

        return await response.json();
      } finally {
        endJob(jobId);
      }
    },
    enabled: !!tableName, // only fetch when tableName is defined
  });

  return {
    demographicReport: query.data ?? EMPTY_DEMOGRAPHIC_REPORT,
    refresh: query.refetch,
  };
}

export default useDemographicReport;
