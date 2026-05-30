import React from "react";
import type { SdqQueryDTO } from "@/api/SdqApi/sdqApi";

import type { Assessor, DemographicFilter } from "@/api/types";
import DemographicFilterForm from "../DemographicFilterForm";
import AssessorMultiPicker from "../AssessorMultiPicker";

interface Props {
  value: SdqQueryDTO;
  onChange: React.Dispatch<React.SetStateAction<SdqQueryDTO>>;
}

const SdqQueryForm: React.FC<Props> = ({ value, onChange }) => {
  const onDateFromChange: React.ChangeEventHandler<HTMLInputElement> = ({
    target: { value },
  }) => {
    onChange((prev) => ({
      ...prev,
      from: value,
    }));
  };
  const onDateToChange: React.ChangeEventHandler<HTMLInputElement> = ({
    target: { value },
  }) => {
    onChange((prev) => ({
      ...prev,
      to: value,
    }));
  };

  const onAssessorsChange: (assessors: Assessor[]) => void = (
    assessors: Assessor[],
  ) => {
    onChange((prev) => ({
      ...prev,
      assessors,
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
          <label htmlFor="dateFrom">Date From</label>
          <input
            type="date"
            name="dateFrom"
            value={value.from}
            onChange={onDateFromChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="dateTo">Date To</label>
          <input
            type="date"
            name="dateTo"
            value={value.to}
            onChange={onDateToChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="assessors">Assessors</label>
          <AssessorMultiPicker
            value={value.assessors}
            onChange={onAssessorsChange}
          />
        </div>
      </div>
      <DemographicFilterForm value={value.filters} onChange={onFiltersChange} />
    </form>
  );
};

export default SdqQueryForm;
