import React from "react";

import TheContext from "./AppNotificationContext";

function useAppNotificationContext() {
  return React.useContext(TheContext);
}

export default useAppNotificationContext;
