import React from "react";
import useApiContext from "../../context/ApiContext";

function DemographicReport() {
  const {
    clientFileApi: { demographicReport, getDemographicReport },
  } = useApiContext();

  const [demographicName, setDemographicName] = React.useState<string>("");
  const onDemographicNameChange: React.ChangeEventHandler<HTMLSelectElement> =
    React.useCallback((e) => {
      setDemographicName(e.target.value);
    }, []);
  const onClickFetch = React.useCallback(() => {
    getDemographicReport(demographicName);
  }, [demographicName, getDemographicReport]);

  return (
    <div>
      <div className="mb-3">
        <label>Demographic</label>
        <select
          className="form-control"
          value={demographicName}
          onChange={onDemographicNameChange}
        >
          <option value="gender">Gender</option>
          <option value="council">Council</option>
          <option value="ethnicity">Ethnicity</option>
          <option value="disability_status">Disability Status</option>
          <option value="disability_type">Disability Type</option>
          <option value="care_experience">Care Experience</option>
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
