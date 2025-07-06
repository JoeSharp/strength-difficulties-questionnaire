import React from "react";
import useApiContext from "../../context/ApiContext";
import JsonDisplay from "../JsonDisplay";

function ManageDatabaseForm() {
  const {
    databaseApi: { createDatabase, exists, structure },
  } = useApiContext();

  const onClickSubmit: React.FormEventHandler<HTMLFormElement> =
    React.useCallback(
      (e) => {
        e.preventDefault();
        const formData = new FormData(e.currentTarget);

        createDatabase(formData);
      },
      [createDatabase]
    );

  return (
    <>
      <form
        method="POST"
        encType="multipart/form-data"
        onSubmit={onClickSubmit}
      >
        <div className="form-control mb-3">
          <label>File</label>
          <input type="file" multiple name="sdqFile" />
        </div>
        <input className="btn btn-primary" type="submit" value="Upload" />
      </form>
      <div className="mb-3"></div>
      <p>Database {exists ? "exists" : "does not exist"}</p>
      <JsonDisplay
        id="dbStructure"
        title="Database Structure"
        value={structure}
      />
    </>
  );
}

export default ManageDatabaseForm;
