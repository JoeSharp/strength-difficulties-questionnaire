import React from "react";

export type MessageType = "danger" | "success" | "warning";

export interface IAppNotification {
  id: number;
  status: number;
  message: string;
  type: MessageType;
}

export interface IAppNotifications {
  messages: IAppNotification[];
  addMessage: (type: MessageType, status: number, e: string) => void;
  dismissMessage: (id: number) => void;
}

let nextId = 0;

function useAppNotifications(): IAppNotifications {
  const [messages, setMessages] = React.useState<IAppNotification[]>([]);
  const addMessage = React.useCallback(
    (type: MessageType, status: number, message: string) => {
      const newMessage = { id: nextId++, type, status, message };
      setMessages((p) => [...p, newMessage]);
    },
    []
  );
  const dismissMessage = React.useCallback((id: number) => {
    setMessages((p) => p.filter((e) => e.id !== id));
  }, []);

  return {
    messages,
    addMessage,
    dismissMessage,
  };
}

export default useAppNotifications;
