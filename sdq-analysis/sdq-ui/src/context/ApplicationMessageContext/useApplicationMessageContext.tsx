import React from 'react';

import TheContext from './ApplicationMessageContext';

function useApplicationMessageContext() {
    return React.useContext(TheContext);
}

export default useApplicationMessageContext;