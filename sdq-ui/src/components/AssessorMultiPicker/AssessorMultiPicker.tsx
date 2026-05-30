import React from "react";
import Select, { type MultiValue } from "react-select";
import { ASSESSORS, type Assessor } from "@/api/types";
import type { Option } from "@/api/ReferenceApi/referenceApi";

type Props = {
  value: Assessor[];
  onChange: (assessors: Assessor[]) => void;
};

const ASSESSOR_OPTIONS: Option<Assessor>[] = ASSESSORS.map((assessor) => ({
  value: assessor,
  label: assessor,
}));

const AssessorMultiPicker: React.FC<Props> = ({ value, onChange }) => {
  const selected = ASSESSOR_OPTIONS.filter(
    (option) => value && value.includes(option.value),
  );
  const onValuesChange = (selected: MultiValue<Option<Assessor>>) => {
    const selectedValues: Assessor[] = selected.map(({ value }) => value);
    onChange(selectedValues);
  };

  return (
    <Select
      name="goal-types"
      isMulti
      options={ASSESSOR_OPTIONS}
      onChange={onValuesChange}
      value={selected}
    />
  );
};

export default AssessorMultiPicker;
