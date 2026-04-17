import React from "react";
import Select, { type MultiValue } from "react-select";
import type { DemographicField, DemographicFilter } from "@/api/types";
import useReference from "@/api/ReferenceApi";
import DemographicFieldPicker from "../DemographicFieldPicker/DemographicFieldPicker";

interface SelectOption {
  label: string;
  value: string;
}

interface Props {
  value: DemographicFilter[];
  onChange: React.Dispatch<React.SetStateAction<DemographicFilter[]>>;
}
const DEFAUL_FILTER: DemographicFilter = { field: "Gender", values: [] };

const DemographicFilterForm: React.FC<Props> = ({ value, onChange }) => {
  const { demographicFields } = useReference();

  const [current, setCurrent] =
    React.useState<DemographicFilter>(DEFAUL_FILTER);

  const onAddFilter: React.MouseEventHandler<HTMLButtonElement> =
    React.useCallback(
      (e) => {
        e.preventDefault();
        onChange((prev) => {
          const newFilters = [
            ...prev.filter((p) => p.field !== current.field),
            current,
          ];
          return newFilters;
        });
      },
      [current, onChange],
    );

  const onDemographicFieldChange = React.useCallback(
    (field: DemographicField) => {
      const currentSpec = value.find((f) => f.field === field);
      const values: string[] = currentSpec ? currentSpec.values : [];
      setCurrent({
        field,
        values,
      });
    },
    [value],
  );

  const selectedOptions: SelectOption[] = React.useMemo(
    () => current.values.map((f) => ({ value: f, label: f })),
    [current.values],
  );
  const options: SelectOption[] = React.useMemo(() => {
    return (
      demographicFields[current.field]?.map((option) => ({
        value: option,
        label: option,
      })) || []
    );
  }, [demographicFields, current.field]);

  const onValuesChange = React.useCallback(
    (selected: MultiValue<SelectOption>) => {
      const selectedValues = selected.map(({ value }) => value);
      setCurrent((prev) => ({
        ...prev,
        values: selectedValues,
      }));
    },
    [],
  );

  const onClickRemoveField = React.useCallback(
    (toDelete: DemographicField) => {
      onChange((prev) => [...prev.filter((p) => p.field !== toDelete)]);
    },
    [onChange],
  );

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
                  <td>{filter.values.join(" or ")}</td>
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
