import React from "react";
import useUploadFileApi from "../../api/useUploadFileApi";
import JsonDisplay from "../JsonDisplay";

function FileUploadForm() {
	const {onSubmitFile, lastSubmission} = useUploadFileApi();

	const onClickSubmit: React.FormEventHandler<HTMLFormElement> = React.useCallback((e) => {
		e.preventDefault();
		const formData = new FormData(e.currentTarget);

		onSubmitFile(formData);
	}, [onSubmitFile]);

	return <><form method="POST" encType="multipart/form-data" onSubmit={onClickSubmit}>
		<div className='form-control mb-3'>
			<label>File</label>
			<input type='file' name='file' />
		</div>
		<input className="btn btn-primary" type='submit' value='Upload' />
	</form>
	<div className='mb-3'></div>
	<JsonDisplay id='lastSubmission' title="Last Submission" value={lastSubmission} />
	</>
}

export default FileUploadForm;
