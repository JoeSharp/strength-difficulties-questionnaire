import React from 'react';
import ApplicationMessageContext from './ApplicationMessageContext';
import useApplicationMessages from './useApplicationMessages';

type WithChildren = { children: React.ReactElement };

export default function ApplicationMessageContextProvider({ children }: WithChildren) {
    const value = useApplicationMessages();

    return <ApplicationMessageContext.Provider value={value}>
        {children}
    </ApplicationMessageContext.Provider>
}
