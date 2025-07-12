import React from "react";
import AppNotificationContext from "./AppNotificationContext";
import useAppNotifications from "./useAppNotifications";

type WithChildren = { children: React.ReactElement };

export default function AppNotificationContextProvider({
  children,
}: WithChildren) {
  const value = useAppNotifications();

  return (
    <AppNotificationContext.Provider value={value}>
      {children}
    </AppNotificationContext.Provider>
  );
}
