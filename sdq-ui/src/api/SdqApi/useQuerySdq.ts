import { useMutation } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import {
  BASE_SDQ_URL,
  type SdqFilterDTO,
  type SdqSubmissionSummary,
} from "./sdqApi";

function useQuerySdq() {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();
  return useMutation<SdqSubmissionSummary[], Error, SdqFilterDTO>({
    mutationFn: async (query) => {
      const jobId = beginJob("Querying SDQ summaries");

      try {
        const response = await fetch(`${BASE_SDQ_URL}/query`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(query),
        });

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

export default useQuerySdq;
