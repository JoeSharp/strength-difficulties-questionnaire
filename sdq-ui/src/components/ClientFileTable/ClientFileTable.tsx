import React from "react";

import { useNavigate } from "react-router-dom";
import useClients from "../../api/ClientApi/useClients";

function UploadFileTable() {
  const { clients } = useClients();

  const navigate = useNavigate();
  const [nameToSearch, setNameToSearch] = React.useState<string>("");
  const onChangeNameToSearch: React.ChangeEventHandler<HTMLInputElement> =
    React.useCallback(({ target: { value } }) => {
      setNameToSearch(value);
    }, []);

  const filteredClients = React.useMemo(
    () => clients.filter((f) => f.codeName.includes(nameToSearch)),
    [nameToSearch, clients],
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
            <th>Name</th>
            <th>DOB</th>
          </tr>
        </thead>
        <tbody>
          {filteredClients.map((client) => (
            <tr
              key={client.clientId}
              className="clickable"
              onClick={() => navigate(`/client/${client.clientId}`)}
            >
              <td>{client.codeName}</td>
              <td>{client.dateOfBirth.toISOString().slice(0, 7)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default UploadFileTable;
