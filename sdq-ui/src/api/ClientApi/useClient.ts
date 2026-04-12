import { useQuery } from "@tanstack/react-query";
import useAppNotificationContext from "../../context/AppNotificationContext";
import useInProgressContext from "../../context/InProgressContext";
import { BASE_CLIENT_URL, parseFile } from "./clientApi";

function useClient(clientId?: string) {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

  return useQuery({
    queryKey: ["clientFile", clientId],
    queryFn: async () => {
      const jobId = beginJob("Fetching file");

      try {
        const response = await fetch(`${BASE_CLIENT_URL}/${clientId}`);

        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to fetch scores: " + response.statusText,
          );
          throw new Error("Network response was not ok");
        }

        const json = await response.json();
        return parseFile(json);
      } finally {
        endJob(jobId);
      }
    },
    enabled: !!clientId, // only run when uuid is defined
  });
}

export default useClient;
