import React from "react";
import Select, { type MultiValue } from "react-select";
import type { DemographicField, DemographicFilter } from "@/api/types";
import useReference from "@/api/ReferenceApi";
import type { Option } from "@/api/ReferenceApi/referenceApi";
import DemographicFieldPicker from "../DemographicFieldPicker/DemographicFieldPicker";

interface Props {
  value: DemographicFilter[];
  onChange: React.Dispatch<React.SetStateAction<DemographicFilter[]>>;
}
const DEFAULT_FILTER: DemographicFilter = { field: "Gender", values: [] };

const DemographicFilterForm: React.FC<Props> = ({ value, onChange }) => {
  const {
    data: { demographicFields },
    getLabelForDemographicValue,
  } = useReference();

  const [current, setCurrent] =
    React.useState<DemographicFilter>(DEFAULT_FILTER);

  const onAddFilter: React.MouseEventHandler<HTMLButtonElement> = (e) => {
    e.preventDefault();
    onChange((prev) => {
      const newFilters = [
        ...prev.filter((p) => p.field !== current.field),
        current,
      ];
      return newFilters;
    });
  };

  const onDemographicFieldChange = (field: DemographicField) => {
    const currentSpec = value.find((f) => f.field === field);
    const values: string[] = currentSpec ? currentSpec.values : [];
    setCurrent({
      field,
      values,
    });
  };

  const options: Option[] = demographicFields[current.field] || [];
  const selectedOptions: Option[] = options.filter((option) =>
    current.values.includes(option.value),
  );
  const onValuesChange = (selected: MultiValue<Option>) => {
    const selectedValues = selected.map(({ value }) => value);
    setCurrent((prev) => ({
      ...prev,
      values: selectedValues,
    }));
  };

  const onClickRemoveField = (toDelete: DemographicField) => {
    onChange((prev) => [...prev.filter((p) => p.field !== toDelete)]);
  };

  return (
    <div>
      <div className="form-horizontal">
        <div className="form-group">
          <label htmlFor="new-demographic-field">Field</label>
          <DemographicFieldPicker
            value={current.field}
            onChange={onDemographicFieldChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="filter-values">Values</label>
          <Select
            name="filter-values"
            isMulti
            options={options}
            value={selectedOptions}
            onChange={onValuesChange}
          />
        </div>
      </div>
      <button onClick={onAddFilter}>Add Filter</button>
      <div className="vert-space" />

      {value.length > 0 && (
        <>
          <table>
            <thead>
              <tr>
                <th>Field</th>
                <th>Values</th>
                <th>Remove</th>
              </tr>
            </thead>
            <tbody>
              {value.map((filter) => (
                <tr key={filter.field}>
                  <td>{filter.field}</td>
                  <td>
                    {filter.values
                      .map((v) => getLabelForDemographicValue(filter.field, v))
                      .join(" or ")}
                  </td>
                  <td>
                    <button onClick={() => onClickRemoveField(filter.field)}>
                      Remove
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="vert-space" />
        </>
      )}
    </div>
  );
};

export default DemographicFilterForm;
