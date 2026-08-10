import { useMutation } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import { BASE_GOAL_URL, type GboSubmission } from "./gboApi";

function useSubmitGbo() {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

  return useMutation<void, Error, GboSubmission>({
    mutationFn: async (gboSubmission) => {
      const jobId = beginJob("Submitting score");

      try {
        const response = await fetch(`${BASE_GOAL_URL}/score`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(gboSubmission),
        });

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to submit score: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }

        // No body expected (201 Created)
      } finally {
        endJob(jobId);
      }
    },
  });
}

export default useSubmitGbo;
