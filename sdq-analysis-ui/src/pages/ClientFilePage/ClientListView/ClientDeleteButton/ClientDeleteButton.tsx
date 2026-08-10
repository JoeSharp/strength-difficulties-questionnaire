import React from "react";
import { useDeleteClient } from "@/api/ClientApi/useDeleteClient";

type Props = {
  clientId: string;
};

const ClientDeleteButton: React.FC<Props> = ({ clientId }) => {
  const deleteClient = useDeleteClient();
  const onClickDelete = () => {
    if (
      window.confirm(
        `Are you sure you want to delete this client? This action cannot be undone.`,
      )
    ) {
      deleteClient.mutate(clientId);
    }
  };

  return (
    <button
      type="button"
      className="client-card__button danger"
      onClick={onClickDelete}
    >
      Delete client
    </button>
  );
};

export default ClientDeleteButton;
