import React from "react";
import type { DemographicField, DemographicFilter } from "@/api/types";
import DemographicFieldPicker from "../DemographicFieldPicker/DemographicFieldPicker";

interface Props {
  value: DemographicFilter[];
  onChange: (v: DemographicFilter[]) => void;
}

const DemographicFilterForm: React.FC<Props> = ({ value, onChange }) => {
  const [filters, setFilters] = React.useState<DemographicFilter[]>(value);

  const [demographicField, setDemographicField] =
    React.useState<DemographicField>("Gender");

  const onAddFilter: React.MouseEventHandler<HTMLButtonElement> =
    React.useCallback(
      (e) => {
        e.preventDefault();
        setFilters((prev) => {
          const newFilters = [
            ...prev.filter((p) => p.field !== demographicField),
            {
              field: demographicField,
              values: [],
            },
          ];
          onChange(newFilters);
          return newFilters;
        });
      },
      [demographicField, onChange],
    );

  return (
    <div>
      <div className="form-group">
        <label htmlFor="new-demographic-field">Field</label>
        <DemographicFieldPicker
          value={demographicField}
          onChange={setDemographicField}
        />
      </div>
      <button className="form-button" onClick={onAddFilter}>
        Add Filter
      </button>
      <div className="vert-space" />

      {filters.map((filter) => (
        <div>{filter.field}</div>
      ))}
    </div>
  );
};

export default DemographicFilterForm;
