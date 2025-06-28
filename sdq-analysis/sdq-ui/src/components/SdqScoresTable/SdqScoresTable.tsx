import useApiContext from '../../context/ApiContext';

const CATEGORIES: string[] = [
	'Conduct',
	'Peer',
	'HyperActivity',
	'ProSocial',
	'Emotional'
];

function UploadFileTable() {
	const {
		uploadFileApi: {
			scores
		}
	} = useApiContext();

	return <div>
		<h4>SDQ Scores</h4>
		<table className='table table-striped'>
			<thead>
				<tr>
					<th>File</th>
					<th>Period</th>
					<th>Assessor</th>
					{CATEGORIES.map(c => (<th key={c}>{c}</th>))}
				</tr>
			</thead>
			<tbody>
				{scores.map(score => <tr key={score.uuid}>
					<td>{score.uuid}</td>
					<td>{score.period}</td>
					<td>{score.assessor}</td>
					<td>{score.scores['Conduct']}</td>
					<td>{score.scores['Peer']}</td>
					<td>{score.scores['HyperActivity']}</td>
					<td>{score.scores['ProSocial']}</td>
					<td>{score.scores['Emotional']}</td>
				</tr>)}
			</tbody>
		</table>
	</div>

}

export default UploadFileTable;
