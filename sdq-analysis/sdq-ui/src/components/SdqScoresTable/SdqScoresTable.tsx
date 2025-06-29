import useApiContext from '../../context/ApiContext';

const CATEGORIES: string[] = [
	'Conduct',
	'Peer',
	'HyperActivity',
	'ProSocial',
	'Emotional'
];

const POSTURES: string[] = [
	'Internalising',
	'Externalising',
	'ProSocial'
]

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
					{POSTURES.map(p => (<th key={p}>{p}</th>))}
					<th>Total Difficulties</th>
				</tr>
			</thead>
			<tbody>
				{scores.map(score => <tr key={score.uuid}>
					<td>{score.uuid}</td>
					<td>{score.period}</td>
					<td>{score.assessor}</td>
					{CATEGORIES.map(c => (<td key={c}>{score.categoryScores[c]}</td>))}
					{POSTURES.map(p => (<td key={p}>{score.postureScores[p]}</td>))}
					<td>{score.total}</td>
				</tr>)}
			</tbody>
		</table>
	</div>

}

export default UploadFileTable;
