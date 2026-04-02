import type React from "react";

import FileUploadForm from "../../components/FileUploadForm";

const BulkUploadPage: React.FC = () => {
  return (
    <div>
      <h2>Bulk Upload</h2>
      <p>
        Here you can upload Spreadsheets containing data that has been collected
        from outside the system.
      </p>
      <p>
        Any uploaded spreadsheets will use the name of the file as the 'name' of
        the client. This can always be edited later.
      </p>
      <FileUploadForm />
    </div>
  );
};

export default BulkUploadPage;
