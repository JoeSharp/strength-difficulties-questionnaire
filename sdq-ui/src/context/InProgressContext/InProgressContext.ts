import React from 'react';
import type { IInProgressState } from './useInProgress';

const DEFAULT_CONTEXT: IInProgressState = {
    jobs: [],
    beginJob: () => {
        console.error('Default implementation')
        return -1;
    },
    endJob: () => {
        console.error('Default implementation')
    }
}

const InProgressContext = React.createContext<IInProgressState>(DEFAULT_CONTEXT);

export default InProgressContext;
