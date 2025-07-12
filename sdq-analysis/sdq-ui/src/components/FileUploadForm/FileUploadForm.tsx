import React from "react";
import useApiContext from "../../context/ApiContext";
import JsonDisplay from "../JsonDisplay";

function FileUploadForm() {
  const {
    refresh,
    uploadApi: { onSubmitFile, lastFile },
  } = useApiContext();

  const onClickSubmit: React.FormEventHandler<HTMLFormElement> =
    React.useCallback(
      (e) => {
        e.preventDefault();
        const formData = new FormData(e.currentTarget);

        onSubmitFile(formData).then(() => refresh());
      },
      [onSubmitFile, refresh]
    );

  return (
    <>
      <form
        method="POST"
        encType="multipart/form-data"
        onSubmit={onClickSubmit}
      >
        <div className="mb-3">
          <label>File</label>
          <input
            className="form-control"
            type="file"
            multiple
            name="sdqFiles"
          />
        </div>
        <input
          className="btn btn-primary"
          type="submit"
          value="Upload File(s)"
        />
      </form>
      <div className="mb-3"></div>
      <JsonDisplay
        id="lastSubmission"
        title="Debug - Last Submission"
        value={lastFile}
      />
    </>
  );
}

export default FileUploadForm;
