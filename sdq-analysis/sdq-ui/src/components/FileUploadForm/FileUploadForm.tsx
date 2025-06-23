function FileUploadForm() {

	return <form method="POST" encType="multipart/form-data" action="/api/upload">
		<div className='form-control'>
			<label>File</label>
			<input type='file' name='file' />
		</div>
		<input type='submit' value='Upload' />
	</form>
}

export default FileUploadForm;
