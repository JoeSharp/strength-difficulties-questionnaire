import React from "react";
import { ASSESSORS, type Assessor } from "@/api/types";

interface Props {
  value: Assessor;
  onChange: (v: Assessor) => void;
}

const AssessorPicker: React.FC<Props> = ({ value, onChange }) => {
  const onInternalChange: React.ChangeEventHandler<HTMLSelectElement> = ({
    target: { value },
  }) => {
    onChange(value as Assessor);
  };

  return (
    <select value={value} onChange={onInternalChange}>
      {ASSESSORS.map((option) => (
        <option key={option} value={option}>
          {option}
        </option>
      ))}
    </select>
  );
};

export default AssessorPicker;
