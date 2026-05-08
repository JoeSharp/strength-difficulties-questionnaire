import React from "react";
import AppNotificationContext from "./AppNotificationContext";
import useAppNotifications from "./useAppNotifications";

export default function AppNotificationContextProvider({
  children,
}: React.PropsWithChildren) {
  const value = useAppNotifications();

  return (
    <AppNotificationContext.Provider value={value}>
      {children}
    </AppNotificationContext.Provider>
  );
}
