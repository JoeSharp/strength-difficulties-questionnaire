import useApiContext from '../../context/ApiContext';

function UploadFileTable() {
	const {
		uploadFileApi: {
			files
		}
	} = useApiContext();

	return <div>
		<h4>Uploaded Files</h4>
		<table className='table table-striped'>
			<thead>
				<tr>
					<th>UUID</th>
					<th>Filename</th>
					<th>DOB</th>
					<th>Gender</th>
					<th>Ethnicity</th>
					<th>EAL</th>
					<th>Disability</th>
					<th>Type</th>
					<th>Care</th>
					<th>Intervention</th>
					<th>ACES</th>
					<th>Funding</th>
				</tr>
			</thead>
			<tbody>
				{files.map(file => <tr key={file.uuid}>
					<td>{file.uuid}</td>
					<td>{file.filename}</td>
					<td>{file.dateOfBirth}</td>
					<td>{file.gender}</td>
					<td>{file.ethnicity}</td>
					<td>{file.englishAdditionalLanguage}</td>
					<td>{file.disabilityStatus}</td>
					<td>{file.disabilityType}</td>
					<td>{file.careExperience}</td>
					<td>{file.interventionType}</td>
					<td>{file.aces}</td>
					<td>{file.fundingSource}</td>
				</tr>)}
			</tbody>
		</table>
	</div>

}

export default UploadFileTable;
