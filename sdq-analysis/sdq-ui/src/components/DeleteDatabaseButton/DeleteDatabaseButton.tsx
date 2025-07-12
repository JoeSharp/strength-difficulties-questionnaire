import useApiContext from "../../context/ApiContext";

function DeleteDatabaseButton() {
  const {
    databaseApi: { deleteDatabase },
  } = useApiContext();

  return (
    <button className="btn btn-danger mb-3" onClick={deleteDatabase}>
      Delete Database
    </button>
  );
}

export default DeleteDatabaseButton;
