import { useMutation } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import {
  BASE_CLIENT_URL,
  parseFile,
  type ClientFile,
  type ClientQueryDTO,
} from "@/api/ClientApi/clientApi";

function useSearchClients() {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

  return useMutation<ClientFile[], Error, ClientQueryDTO>({
    mutationKey: ["clientFilteredFiles"],
    mutationFn: async (query) => {
      const jobId = beginJob("Fetching files");

      try {
        const response = await fetch(`${BASE_CLIENT_URL}/search`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(query),
        });

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch file: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }

        const json = await response.json();
        return json.map(parseFile);
      } finally {
        endJob(jobId);
      }
    },
  });
}

export default useSearchClients;
