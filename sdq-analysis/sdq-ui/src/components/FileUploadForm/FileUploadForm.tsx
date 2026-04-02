import React from "react";
import useApiContext from "@/context/ApiContext";

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
      [onSubmitFile, refresh],
    );

  return (
    <>
      <form
        method="POST"
        encType="multipart/form-data"
        onSubmit={onClickSubmit}
      >
        <div className="form-group">
          <label htmlFor="sdqFiles">File</label>
          <input type="file" multiple name="sdqFiles" />
        </div>
        <input className="form-button" type="submit" value="Upload File(s)" />
      </form>
      {lastFile && (
        <p>File uploaded with name {lastFile.clientFile.filename}</p>
      )}
    </>
  );
}

export default FileUploadForm;
