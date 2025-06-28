import React from 'react';

import TheContext from './InProgressContext';

function useInProgressContext() {
    return React.useContext(TheContext);
}

export default useInProgressContext;