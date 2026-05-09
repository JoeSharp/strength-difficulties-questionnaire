import React from "react";
import Select, { type MultiValue } from "react-select";

import useReference from "@/api/ReferenceApi";
import type { Option } from "@/api/ReferenceApi/referenceApi";

interface Props {
  value: string[];
  onChange: (v: string[]) => void;
}

const GoalTypePicker: React.FC<Props> = ({ value, onChange }) => {
  const {
    data: { goalTypes },
  } = useReference();

  const selected = goalTypes.filter((option) => value.includes(option.value));
  const onValuesChange = (selected: MultiValue<Option>) => {
    const selectedValues = selected.map(({ value }) => value);
    onChange(selectedValues);
  };

  return (
    <Select
      name="goal-types"
      isMulti
      options={goalTypes}
      onChange={onValuesChange}
      value={selected}
    />
  );
};

export default GoalTypePicker;
