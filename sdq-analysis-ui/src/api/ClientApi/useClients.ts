import { useQuery } from "@tanstack/react-query";
import useInProgressContext from "@/context/InProgressContext";
import useAppNotificationContext from "@/context/AppNotificationContext";
import {
  BASE_CLIENT_URL,
  parseFile,
  type ClientFile,
} from "@/api/ClientApi/clientApi";

interface IUseClients {
  clients: ClientFile[];
  refresh: () => void;
}

function useClients(): IUseClients {
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();

  const query = useQuery<ClientFile[]>({
    queryKey: ["clientFiles"],
    queryFn: async () => {
      const jobId = beginJob("Fetching files");

      try {
        const response = await fetch(BASE_CLIENT_URL);

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

  return {
    clients: query.data ?? [],
    refresh: query.refetch,
  };
}

export default useClients;
