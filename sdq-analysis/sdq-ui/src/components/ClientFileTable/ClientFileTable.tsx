import useApiContext from "../../context/ApiContext";

import "./clientFileTable.css";
import { useNavigate } from "react-router-dom";

function UploadFileTable() {
  const {
    clientFileApi: { files },
  } = useApiContext();

  const navigate = useNavigate();

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
            <tr
              key={file.fileId}
              className="clickable"
              onClick={() => navigate(`/client/${file.fileId}`)}
            >
              <td>{file.fileId}</td>
              <td>{file.filename}</td>
              <td>{file.dateOfBirth.toISOString().slice(0, 7)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default UploadFileTable;
