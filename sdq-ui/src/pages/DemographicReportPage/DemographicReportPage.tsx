import React from "react";
import {
  DEMOGRAPHIC_FIELDS,
  type DemographicField,
} from "@/api/ReferenceApi/referenceApi";
import useDemographicReport from "@/api/ClientApi/useDemographicReport";

const OPTIONS = DEMOGRAPHIC_FIELDS.map((field) => ({
  value: field,
  label: field,
}));

function DemographicReportPage() {
  const [demographicName, setDemographicName] =
    React.useState<DemographicField>("Gender");
  const { demographicReport } = useDemographicReport(demographicName);

  const onDemographicNameChange: React.ChangeEventHandler<HTMLSelectElement> =
    React.useCallback((e) => {
      setDemographicName(e.target.value as DemographicField);
    }, []);

  return (
    <div>
      <h2>Demographic Report</h2>
      <p>
        This report lets you get a quick idea of the distribution of the various
        demographic values.
      </p>
      <div className="form-group">
        <label>Demographic</label>
        <select value={demographicName} onChange={onDemographicNameChange}>
          {OPTIONS.map(({ value, label }) => (
            <option key={value} value={value}>
              {label}
            </option>
          ))}
        </select>
      </div>

      <table>
        <thead>
          <tr>
            <th>Option</th>
            <th>Count</th>
            <th>Percentage</th>
          </tr>
        </thead>
        <tbody>
          {demographicReport.counts.map(({ option, count, percentage }) => (
            <tr key={option}>
              <td>{option}</td>
              <td>{count}</td>
              <td>{percentage} %</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default DemographicReportPage;
