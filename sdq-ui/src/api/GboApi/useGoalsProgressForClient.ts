import { useQuery } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import { BASE_GOAL_URL, type GoalProgress } from "./gboApi";
import type { Assessor } from "../types";

function useGoalsForClient(clientId: string, assessor: Assessor) {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

  return useQuery<GoalProgress[], void>({
    queryKey: ["goalsProgressForClient", clientId, assessor],
    queryFn: async () => {
      const jobId = beginJob("Fetching goals");

      try {
        const response = await fetch(
          `${BASE_GOAL_URL}/forClient/${clientId}/progress/${assessor}`,
        );

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
    enabled: !!clientId && !!assessor,
  });
}

export default useGoalsForClient;
