import useClientFileApi from "../../api/useClientFileApi";

function DeleteAllButton() {
  const { onDeleteAll } = useClientFileApi();

  return (
    <button className="btn btn-danger" onClick={onDeleteAll}>
      Delete All
    </button>
  );
}

export default DeleteAllButton;
