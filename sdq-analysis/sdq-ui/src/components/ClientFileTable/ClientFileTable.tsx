import React from "react";
import useApiContext from "@/context/ApiContext";

import { useNavigate } from "react-router-dom";

function UploadFileTable() {
  const {
    clientFileApi: { files },
  } = useApiContext();

  const navigate = useNavigate();
  const [nameToSearch, setNameToSearch] = React.useState<string>("");
  const onChangeNameToSearch: React.ChangeEventHandler<HTMLInputElement> =
    React.useCallback(({ target: { value } }) => {
      setNameToSearch(value);
    }, []);

  const filteredFiles = React.useMemo(
    () => files.filter((f) => f.codeName.includes(nameToSearch)),
    [nameToSearch, files],
  );

  return (
    <div>
      <h4>Uploaded Files</h4>
      <form>
        <div className="form-group">
          <label htmlFor="name-to-search">Name</label>
          <input
            name="name-to-search"
            value={nameToSearch}
            onChange={onChangeNameToSearch}
          />
        </div>
      </form>
      <table className="table">
        <thead>
          <tr>
            <th>UUID</th>
            <th>Filename</th>
            <th>DOB</th>
          </tr>
        </thead>
        <tbody>
          {filteredFiles.map((file) => (
            <tr
              key={file.clientId}
              className="clickable"
              onClick={() => navigate(`/client/${file.clientId}`)}
            >
              <td>{file.clientId}</td>
              <td>{file.codeName}</td>
              <td>{file.dateOfBirth.toISOString().slice(0, 7)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default UploadFileTable;
