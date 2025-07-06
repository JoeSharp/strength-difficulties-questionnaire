import React from "react";
import useApiContext from "../../context/ApiContext";

import "./clientFileTable.css";

function UploadFileTable() {
  const {
    clientFileApi: { files },
  } = useApiContext();

  const [expanded, setExpanded] = React.useState<string | undefined>();

  return (
    <div>
      <h4>Uploaded Files</h4>
      <table className="table">
        <thead>
          <tr>
            <th>UUID</th>
            <th>Filename</th>
            <th>DOB</th>
          </tr>
        </thead>
        <tbody>
          {files.map((file) => (
            <React.Fragment key={file.uuid}>
              <tr
                className="clickable"
                onClick={() =>
                  setExpanded((e) => (e === file.uuid ? undefined : file.uuid))
                }
              >
                <td>{file.uuid}</td>
                <td>{file.filename}</td>
                <td>{file.dateOfBirth.toISOString().slice(0, 7)}</td>
              </tr>
              {expanded === file.uuid && (
                <tr>
                  <td colSpan={3}>
                    <dl className="two-columns">
                      <dt>Gender</dt>
                      <dd>{file.gender}</dd>
                      <dt>Council</dt>
                      <dd>{file.council}</dd>
                      <dt>Ethnicity</dt>
                      <dd>{file.ethnicity}</dd>
                      <dt>EAL</dt>
                      <dd>{file.englishAdditionalLanguage}</dd>
                      <dt>Disability</dt>
                      <dd>
                        {file.disabilityStatus} - {file.disabilityType}
                      </dd>
                      <dt>Care Experience</dt>
                      <dd>{file.careExperience}</dd>
                      <dt>Intervention Types</dt>
                      <dd>{file.interventionTypes.join(",")}</dd>
                      <dt>ACES</dt>
                      <dd>{file.aces}</dd>
                      <dt>Funding Source</dt>
                      <dd>{file.fundingSource}</dd>
                    </dl>
                  </td>
                </tr>
              )}
            </React.Fragment>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default UploadFileTable;
