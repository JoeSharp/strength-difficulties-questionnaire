import React from "react";

import useClients from "@/api/ClientApi/useClients";
import ClientCard from "./ClientCard";

function ClientListView() {
  const { clients } = useClients();

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
      <h2>Client List</h2>
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
      {filteredClients.map((client) => (
        <ClientCard client={client} />
      ))}
    </div>
  );
}

export default ClientListView;
