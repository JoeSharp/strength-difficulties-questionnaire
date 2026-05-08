import React from "react";

import useClients from "@/api/ClientApi/useClients";
import ClientCard from "./ClientCard";
import ClientDeleteAllButton from "../ClientDeleteAllButton";

function ClientListView() {
  const { clients } = useClients();

  const [nameToSearch, setNameToSearch] = React.useState<string>("");
  const onChangeNameToSearch: React.ChangeEventHandler<HTMLInputElement> = ({
    target: { value },
  }) => {
    setNameToSearch(value);
  };

  const filteredClients = clients.filter((f) =>
    f.codeName.includes(nameToSearch),
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
        <ClientDeleteAllButton />
      </form>
      {filteredClients.map((client) => (
        <ClientCard key={client.clientId} client={client} />
      ))}
    </div>
  );
}

export default ClientListView;
