import React from "react";
import type { GoalQueryDTO } from "../../api/GboApi/gboApi";

import "./GoalQueryForm.scss";

interface Props {
  value: GoalQueryDTO;
  onChange: React.Dispatch<React.SetStateAction<GoalQueryDTO>>;
}

const GoalQueryForm: React.FC<Props> = ({ value, onChange }) => {
  const onMinProgressChange: React.ChangeEventHandler<HTMLInputElement> =
    React.useCallback(
      ({ target: { value } }) => {
        onChange((prev) => ({
          ...prev,
          minProgress: parseInt(value, 10),
        }));
      },
      [onChange],
    );

  const onDateFromChange: React.ChangeEventHandler<HTMLInputElement> =
    React.useCallback(
      ({ target: { value } }) => {
        onChange((prev) => ({
          ...prev,
          from: value,
        }));
      },
      [onChange],
    );
  const onDateToChange: React.ChangeEventHandler<HTMLInputElement> =
    React.useCallback(
      ({ target: { value } }) => {
        onChange((prev) => ({
          ...prev,
          to: value,
        }));
      },
      [onChange],
    );

  return (
    <form>
      <div className="goal-query-form-top">
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
      </div>
    </form>
  );
};

export default GoalQueryForm;
