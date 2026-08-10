import { useMutation, useQueryClient } from "@tanstack/react-query";
import { BASE_CLIENT_URL } from "@/api/ClientApi/clientApi";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";

export function useDeleteAllClients() {
  const queryClient = useQueryClient();
  const { beginJob, endJob } = useInProgressContext();
  const { addMessage } = useAppNotificationContext();

  return useMutation({
    mutationFn: async () => {
      const jobId = beginJob("Deleting all clients");

      try {
        const response = await fetch(BASE_CLIENT_URL, {
          method: "DELETE",
        });

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to delete all clients: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }
        addMessage(
          "success",
          response.status,
          "Successfully deleted all clients",
        );
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
