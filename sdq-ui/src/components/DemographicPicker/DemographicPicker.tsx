import React from "react";
import type { DemographicField } from "@/api/ReferenceApi/referenceApi";
import useReference from "../../api/ReferenceApi";

interface Props {
  demographic: DemographicField;
  value?: string;
  onChange: (newValue: string) => void;
}

function DemographicPicker({ demographic, value, onChange }: Props) {
  const { demographicFields } = useReference();

  const options = React.useMemo(() => {
    return (
      demographicFields[demographic]?.map((option) => ({
        value: option,
        label: option,
      })) || []
    );
  }, [demographicFields, demographic]);

  const onSelectChange: React.ChangeEventHandler<HTMLSelectElement> =
    React.useCallback(
      ({ target: { value } }) => {
        onChange(value);
      },
      [onChange],
    );

  return (
    <div className="form-group">
      <label htmlFor={demographic}>{demographic}</label>
      <select value={value} onChange={onSelectChange} name={demographic}>
        <option value="">Any</option>
        {options.map(({ value, label }) => (
          <option key={value} value={value}>
            {label}
          </option>
        ))}
      </select>
    </div>
  );
}

export default DemographicPicker;
