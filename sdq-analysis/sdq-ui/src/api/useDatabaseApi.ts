import React from "react";
import useAppNotificationContext from "../context/AppNotificationContext";
import useInProgressContext from "../context/InProgressContext";
import { useNavigate } from "react-router-dom";

export interface DatabaseApi {
  exists: boolean;
  refresh: () => void;
  deleteDatabase: () => void;
}

export const EMPTY_DATABASE_API: DatabaseApi = {
  exists: false,
  refresh: () => console.error("default implementation"),
  deleteDatabase: () => console.error("default implementation"),
};

const BASE_URL = "/api/database";

function useDatabaseApi(): DatabaseApi {
  const navigate = useNavigate();
  const { addMessage } = useAppNotificationContext();
  const { beginJob, endJob } = useInProgressContext();
  const [exists, setExists] = React.useState<boolean>(false);

  const refresh = React.useCallback(() => {
    fetch(BASE_URL).then((response) => {
      setExists(response.ok);
    });
  }, []);

  // Determine if database currently exists
  React.useEffect(refresh, []);

  const deleteDatabase = React.useCallback(() => {
    const jobId = beginJob("Deleting Database");
    fetch(BASE_URL, {
      method: "DELETE",
    })
      .then((response) => {
        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to delete database: " + response.statusText
          );
          throw new Error("Network response was not ok");
        }
        addMessage("success", response.status, "Database deleted succesfully");
        setExists(false);
        navigate("/");
      })
      .finally(() => {
        endJob(jobId);
      });
  }, [endJob, beginJob, addMessage, navigate]);

  return {
    exists,
    refresh,
    deleteDatabase,
  };
}

export default useDatabaseApi;
