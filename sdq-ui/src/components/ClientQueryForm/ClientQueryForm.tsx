import React from "react";

import type { DemographicFilter } from "@/api/types";
import DemographicFilterForm from "@/components/DemographicFilterForm";
import type { ClientQueryDTO } from "@/api/ClientApi/clientApi";

interface Props {
  value: ClientQueryDTO;
  onChange: React.Dispatch<React.SetStateAction<ClientQueryDTO>>;
}

const ClientQueryForm: React.FC<Props> = ({ value, onChange }) => {
  const onPartialNameChange: React.ChangeEventHandler<HTMLInputElement> = ({
    target: { value },
  }) => {
    onChange((prev) => ({
      ...prev,
      partialName: value,
    }));
  };

  const onFiltersChange = (
    filtersChange: React.SetStateAction<DemographicFilter[]>,
  ) => {
    onChange((prev) => {
      const filters =
        typeof filtersChange === "function"
          ? filtersChange(prev.filters)
          : filtersChange;

      return {
        ...prev,
        filters,
      };
    });
  };

  return (
    <form>
      <div className="form-horizontal">
        <div className="form-group">
          <label htmlFor="dateFrom">Code Name</label>
          <input
            type="text"
            name="codeName"
            value={value.partialName}
            onChange={onPartialNameChange}
          />
        </div>
      </div>
      <DemographicFilterForm value={value.filters} onChange={onFiltersChange} />
    </form>
  );
};

export default ClientQueryForm;
