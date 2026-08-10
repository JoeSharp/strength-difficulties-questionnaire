import React from "react";
import type { IAppNotifications } from "./useAppNotifications";

const DEFAULT_CONTEXT: IAppNotifications = {
  messages: [],
  addMessage: () => {
    console.error("Default implementation");
  },
  dismissMessage: () => {
    console.error("Default implementation");
  },
};

const ApplicationErrorContext =
  React.createContext<IAppNotifications>(DEFAULT_CONTEXT);

export default ApplicationErrorContext;
