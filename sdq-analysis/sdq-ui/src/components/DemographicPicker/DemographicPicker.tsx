import React from "react";
import type { DemographicField } from "../../api/types";
import useApiContext from "../../context/ApiContext";

interface Props {
  demographic: DemographicField;
  value?: string;
  onChange: (newValue: string) => void;
}

function DemographicPicker({ demographic, value, onChange }: Props) {
  const {
    referenceApi: {
      referenceInfo: { demographicFields },
    },
  } = useApiContext();

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
      [onChange]
    );

  return (
    <div className="mb-3">
      <label htmlFor={demographic}>{demographic}</label>
      <select
        value={value}
        onChange={onSelectChange}
        className="form-control"
        name={demographic}
      >
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
