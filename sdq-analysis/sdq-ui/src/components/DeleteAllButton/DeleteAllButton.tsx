import useUploadFileApi from '../../api/useUploadFileApi';

function DeleteAllButton() {

	const { onDeleteAll } = useUploadFileApi();

	return <button className='btn btn-danger' onClick={onDeleteAll}>Delete All</button>
}

export default DeleteAllButton;
