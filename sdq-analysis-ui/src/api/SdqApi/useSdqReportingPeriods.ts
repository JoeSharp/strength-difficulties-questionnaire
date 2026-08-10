import { useQuery } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import { BASE_SDQ_URL, type ReportingPeriod } from "./sdqApi";

function useSdqReportingPeriods(clientId: string) {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();
  return useQuery<ReportingPeriod[]>({
    queryKey: ["sdqReportingPeriods", clientId],
    queryFn: async () => {
      const jobId = beginJob("Querying SDQ summaries");

      try {
        const response = await fetch(
          `${BASE_SDQ_URL}/${clientId}/reportingPeriods`,
          {
            method: "GET",
            headers: { "Content-Type": "application/json" },
          },
        );

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to query SDQ summaries: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }

        return await response.json();
      } finally {
        endJob(jobId);
      }
    },
  });
}

export default useSdqReportingPeriods;
