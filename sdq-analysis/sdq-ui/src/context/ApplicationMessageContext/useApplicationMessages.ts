import React from "react";

export type MessageType = 'danger' | 'success' | 'warning';

export interface IApplicationMessage {
    id: number,
    status: number,
    message: string,
    type: MessageType
}

export interface IApplicationMessages {
    messages: IApplicationMessage[];
    addMessage: (type: MessageType, status: number, e: string) => void;
    dismissMessage: (id: number) => void;
}

let nextId = 0;

function useApplicationMessages(): IApplicationMessages {
    const [messages, setMessages] = React.useState<IApplicationMessage[]>([]);
    const addMessage = React.useCallback((type: MessageType, status: number, message: string) => {
        const newMessage = {id: nextId++, type, status, message};
        setMessages(p => ([...p, newMessage]));
    }, []);
    const dismissMessage = React.useCallback((id: number) => {
        setMessages(p => p.filter(e => e.id !== id));
    }, []);

    return {
        messages,
        addMessage,
        dismissMessage
    }
}

export default useApplicationMessages;

