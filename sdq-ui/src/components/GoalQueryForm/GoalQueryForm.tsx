import React from "react";
import type { GoalQueryDTO } from "@/api/GboApi/gboApi";

import type { Assessor, DemographicFilter } from "@/api/types";
import AssessorPicker from "../AssessorPicker";
import DemographicFilterForm from "../DemographicFilterForm";
import GoalTypePicker from "./GoalTypePicker";

interface Props {
  value: GoalQueryDTO;
  onChange: React.Dispatch<React.SetStateAction<GoalQueryDTO>>;
}

const GoalQueryForm: React.FC<Props> = ({ value, onChange }) => {
  const onGoalTypesChange = (goalTypes: string[]) => {
    onChange((prev) => ({
      ...prev,
      goalTypes,
    }));
  };

  const onMinProgressChange: React.ChangeEventHandler<HTMLInputElement> = ({
    target: { value },
  }) => {
    onChange((prev) => ({
      ...prev,
      minProgress: parseInt(value, 10),
    }));
  };

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

  const onAssessorChange = (assessor: Assessor) => {
    onChange((prev) => ({
      ...prev,
      assessor,
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
          <label htmlFor="minProgress">Minimum Progress</label>
          <input
            type="number"
            name="minProgress"
            value={value.minProgress}
            onChange={onMinProgressChange}
          />
        </div>
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
          <label htmlFor="assessor">Assessor</label>
          <AssessorPicker value={value.assessor} onChange={onAssessorChange} />
        </div>
        <div className="form-group">
          <label htmlFor="goalTypes">Goal Types</label>
          <GoalTypePicker
            value={value.goalTypes}
            onChange={onGoalTypesChange}
          />
        </div>
      </div>
      <DemographicFilterForm value={value.filters} onChange={onFiltersChange} />
    </form>
  );
};

export default GoalQueryForm;
