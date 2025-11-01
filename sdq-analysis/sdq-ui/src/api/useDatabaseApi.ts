import React from "react";

export interface DatabaseApi {
  exists: boolean;
  refresh: () => void;
}

export const EMPTY_DATABASE_API: DatabaseApi = {
  exists: false,
  refresh: () => console.error("default implementation"),
};

const BASE_URL = "/api/database";

function useDatabaseApi(): DatabaseApi {
  const [exists, setExists] = React.useState<boolean>(false);

  const refresh = React.useCallback(() => {
    fetch(BASE_URL).then((response) => {
      setExists(response.ok);
    });
  }, []);

  // Determine if database currently exists
  React.useEffect(refresh, []);

  return {
    exists,
    refresh,
  };
}

export default useDatabaseApi;
