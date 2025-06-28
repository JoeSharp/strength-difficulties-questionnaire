import useApiContext from '../../context/ApiContext';

function UploadFileTable() {
	const {
		uploadFileApi: {
			files
		}
	} = useApiContext();

	return <table className='table table-striped'>
		<thead>
			<tr>
				<th>UUID</th>
				<th>Filename</th>
			</tr>
		</thead>
		<tbody>
			{files.map(file => <tr key={file.uuid}>
				<td>{file.uuid}</td>
				<td>{file.filename}</td>
			</tr>)}
		</tbody>
	</table>

}

export default UploadFileTable;
