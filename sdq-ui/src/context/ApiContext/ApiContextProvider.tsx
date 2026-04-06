import React from 'react';
import ApiContext from './ApiContext';
import useApi from './useApi';

type WithChildren = { children: React.ReactElement };

export default function ApiContextProvider({ children }: WithChildren) {
    const value = useApi();

    return <ApiContext.Provider value={value}>
        {children}
    </ApiContext.Provider>
}
