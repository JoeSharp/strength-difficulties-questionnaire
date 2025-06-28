import React from 'react';
import InProgressContext from './InProgressContext';
import useInProgress from './useInProgress';

type WithChildren = { children: React.ReactElement };

export default function InProgressContextProvider({ children }: WithChildren) {
    const value = useInProgress();

    return <InProgressContext.Provider value={value}>
        {children}
    </InProgressContext.Provider>
}
