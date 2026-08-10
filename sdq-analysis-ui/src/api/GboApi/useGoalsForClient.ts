import { useQuery } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import { BASE_GOAL_URL, type Goal } from "./gboApi";

function useGoalsForClient(clientId: string) {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

  return useQuery<Goal[], void>({
    queryKey: ["goalsForClient", clientId],
    queryFn: async () => {
      const jobId = beginJob("Fetching goals");

      try {
        const response = await fetch(`${BASE_GOAL_URL}/forClient/${clientId}`);

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch goals: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }

        return await response.json();
      } finally {
        endJob(jobId);
      }
    },
    enabled: !!clientId,
  });
}

export default useGoalsForClient;
