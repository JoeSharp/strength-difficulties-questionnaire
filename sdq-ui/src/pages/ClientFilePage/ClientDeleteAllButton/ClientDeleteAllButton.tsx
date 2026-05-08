import React from "react";
import { useDeleteAllClients } from "@/api/ClientApi/useDeleteAllClients";

const ClientDeleteAllButton: React.FC = () => {
  const deleteClient = useDeleteAllClients();
  const onClickDelete = () => {
    if (
      window.confirm(
        "Are you sure you want to delete all clients? This action cannot be undone. I hope this is Beth or Joe...having said that, if you have all the spreadsheets, then you can recreate this stuff with bulk upload...go ahead, knock yourself out",
      )
    ) {
      deleteClient.mutate();
    }
  };

  return (
    <button
      type="button"
      className="client-card__button danger"
      onClick={onClickDelete}
    >
      Delete all clients
    </button>
  );
};

export default ClientDeleteAllButton;
