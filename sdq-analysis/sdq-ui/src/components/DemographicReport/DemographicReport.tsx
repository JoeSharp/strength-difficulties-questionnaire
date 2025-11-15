import React from "react";
import useApiContext from "../../context/ApiContext";
import { DEMOGRAPHIC_FIELDS, type DemographicField } from "../../api/types";

const OPTIONS = DEMOGRAPHIC_FIELDS.map((field) => ({
  value: field,
  label: field,
}));

function DemographicReport() {
  const {
    clientFileApi: { demographicReport, getDemographicReport },
  } = useApiContext();

  const [demographicName, setDemographicName] =
    React.useState<DemographicField>("Gender");
  const onDemographicNameChange: React.ChangeEventHandler<HTMLSelectElement> =
    React.useCallback((e) => {
      setDemographicName(e.target.value as DemographicField);
    }, []);
  const onClickFetch = React.useCallback(() => {
    getDemographicReport(demographicName);
  }, [demographicName, getDemographicReport]);

  return (
    <div>
      <h4>Demographic Report</h4>
      <div className="mb-3">
        <label>Demographic</label>
        <select
          className="form-control"
          value={demographicName}
          onChange={onDemographicNameChange}
        >
          {OPTIONS.map(({ value, label }) => (
            <option value={value}>{label}</option>
          ))}
        </select>
      </div>

      <button className="btn btn-primary" onClick={onClickFetch}>
        Fetch
      </button>

      <table className="table">
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

export default DemographicReport;
