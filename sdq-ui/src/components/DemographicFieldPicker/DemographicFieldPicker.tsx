import React from "react";
import type { DemographicField } from "@/api/types";

interface Props {
  value: DemographicField;
  onChange: (v: DemographicField) => void;
}

const OPTIONS: DemographicField[] = [
  "Gender",
  "Council",
  "Ethnicity",
  "EAL",
  "DisabilityStatus",
  "DisabilityType",
  "CareExperience",
  "InterventionType",
  "ACES",
  "FundingSource",
];

const DemographicFieldPicker: React.FC<Props> = ({ value, onChange }) => {
  const onInternalChange: React.ChangeEventHandler<HTMLSelectElement> =
    React.useCallback(({ target: { value } }) => {
      onChange(value as DemographicField);
    }, []);

  return (
    <select value={value} onChange={onInternalChange}>
      {OPTIONS.map((option) => (
        <option key={option} value={option}>
          {option}
        </option>
      ))}
    </select>
  );
};

export default DemographicFieldPicker;
