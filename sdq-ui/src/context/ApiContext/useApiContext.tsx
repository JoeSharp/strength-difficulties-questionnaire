import React from 'react';

import TheContext from './ApiContext';

function useApiContext() {
    return React.useContext(TheContext);
}

export default useApiContext;
