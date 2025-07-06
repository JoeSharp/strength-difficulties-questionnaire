import React from "react";
import useApplicationMessageContext from "../context/ApplicationMessageContext";
import useInProgressContext from "../context/InProgressContext";
import type { DatabaseStructure } from "./types";

export const EMPTY_STRUCTURE: DatabaseStructure = {
  demographics: {},
};

export interface DatabaseApi {
  exists: boolean;
  structure: DatabaseStructure;
  createDatabase: (formData: FormData) => void;
  clearDatabase: () => void;
}

export const EMPTY_DATABASE_API: DatabaseApi = {
  exists: false,
  structure: EMPTY_STRUCTURE,
  createDatabase: () => console.error("default implementation"),
  clearDatabase: () => console.error("default implementation"),
};

const BASE_URL = "/api/database";

function useDatabaseApi(): DatabaseApi {
  const { addMessage } = useApplicationMessageContext();
  const { beginJob, endJob } = useInProgressContext();
  const [exists, setExists] = React.useState<boolean>(false);
  const [structure, setStructure] =
    React.useState<DatabaseStructure>(EMPTY_STRUCTURE);

  // Determine if database currently exists
  React.useEffect(() => {
    fetch(BASE_URL).then((response) => {
      setExists(response.ok);
    });
  }, []);

  const createDatabase = React.useCallback((formData: FormData) => {
    const jobId = beginJob("Creating database");
    fetch(BASE_URL, {
      method: "POST",
      body: formData,
    })
      .then((response) => {
        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to create database: " + response.statusText
          );
          throw new Error("Network response was not ok");
        }
        addMessage("success", response.status, "Database created succesfully");
        return response.json();
      })
      .then((s) => setStructure(s))
      .finally(() => {
        endJob(jobId);
      });
  }, []);

  const clearDatabase = React.useCallback(() => {
    const jobId = beginJob("Clearing Database");
    fetch(BASE_URL, {
      method: "DELETE",
    })
      .then((response) => {
        if (!response.ok) {
          addMessage(
            "danger",
            response.status,
            "Failed to clear database: " + response.statusText
          );
          throw new Error("Network response was not ok");
        }
        addMessage("success", response.status, "Database cleared succesfully");
      })
      .finally(() => {
        endJob(jobId);
      });
  }, []);

  return {
    exists,
    structure,
    createDatabase,
    clearDatabase,
  };
}

export default useDatabaseApi;
