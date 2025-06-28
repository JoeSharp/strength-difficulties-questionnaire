import React from 'react';
import type { IApi } from './useApi';

const DEFAULT_CONTEXT: IApi = {
	uploadFileApi: {
		files: [],
		lastSubmission: {},
		onSubmitFile: () => console.error('default implementation')
	}
}

const ApiContext = React.createContext<IApi>(DEFAULT_CONTEXT);

export default ApiContext;

