import React from "react";
import useApiContext from '../../context/ApiContext';
import JsonDisplay from "../JsonDisplay";

function FileUploadForm() {
	const {
		uploadFileApi: {
			onSubmitFile, 
			lastSubmission
		} 
	}= useApiContext();

	const onClickSubmit: React.FormEventHandler<HTMLFormElement> = React.useCallback((e) => {
		e.preventDefault();
		const formData = new FormData(e.currentTarget);

		onSubmitFile(formData);
	}, [onSubmitFile]);

	return <><form method="POST" encType="multipart/form-data" onSubmit={onClickSubmit}>
		<div className='form-control mb-3'>
			<label>File</label>
			<input type='file' multiple name='sdqFiles' />
		</div>
		<input className="btn btn-primary" type='submit' value='Upload' />
	</form>
	<div className='mb-3'></div>
	<JsonDisplay id='lastSubmission' title="Last Submission" value={lastSubmission} />
	</>
}

export default FileUploadForm;
