import React from "react";
import type { Assessor } from "@/api/types";

interface Props {
  value: Assessor;
  onChange: (v: Assessor) => void;
}

const OPTIONS: Assessor[] = ["Child", "School", "Parent1", "Parent2"];

const AssessorPicker: React.FC<Props> = ({ value, onChange }) => {
  const onInternalChange: React.ChangeEventHandler<HTMLSelectElement> =
    React.useCallback(({ target: { value } }) => {
      onChange(value as Assessor);
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

export default AssessorPicker;
