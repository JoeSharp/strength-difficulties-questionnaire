import { useMutation, useQueryClient } from "@tanstack/react-query";
import { BASE_CLIENT_URL } from "@/api/ClientApi/clientApi";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";

export function useDeleteClient() {
  const queryClient = useQueryClient();
  const { beginJob, endJob } = useInProgressContext();
  const { addMessage } = useAppNotificationContext();

  return useMutation({
    mutationFn: async (clientId: string) => {
      const jobId = beginJob("Deleting client");

      try {
        const response = await fetch(`${BASE_CLIENT_URL}/${clientId}`, {
          method: "DELETE",
        });

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to delete client: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }
      } finally {
        endJob(jobId);
      }
    },

    onSuccess: () => {
      // Invalidate any queries that depend on clients
      queryClient.invalidateQueries({ queryKey: ["clientFiles"] });
    },
  });
}
