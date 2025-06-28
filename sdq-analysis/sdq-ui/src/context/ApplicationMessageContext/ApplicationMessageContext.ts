import React from 'react';
import type { IApplicationMessages } from './useApplicationMessages';

const DEFAULT_CONTEXT: IApplicationMessages = {
    messages: [],
    addMessage: () => {
        console.error('Default implementation')
    },
    dismissMessage: () => {
        console.error('Default implementation')
    }
}

const ApplicationErrorContext = React.createContext<IApplicationMessages>(DEFAULT_CONTEXT);

export default ApplicationErrorContext;
