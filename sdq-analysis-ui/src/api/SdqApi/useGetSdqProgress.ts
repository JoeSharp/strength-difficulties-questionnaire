import { useQuery } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import { BASE_SDQ_URL, type SdqProgressSummary } from "./sdqApi";
import type { Assessor } from "../types";

function useGetSdqProgress(clientId: string, assessor: Assessor) {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();
  return useQuery<SdqProgressSummary>({
    queryKey: ["sdqProgressForClient", clientId, assessor],
    queryFn: async () => {
      const jobId = beginJob("Querying SDQ summaries");

      try {
        const response = await fetch(
          `${BASE_SDQ_URL}/${clientId}/${assessor}/progress`,
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

export default useGetSdqProgress;
