import React from "react";
import useUploadApi from "@/api/UploadApi/useUploadApi";

function FileUploadForm() {
  const { onSubmitFile } = useUploadApi();

  const onClickSubmit: React.FormEventHandler<HTMLFormElement> =
    React.useCallback(
      (e) => {
        e.preventDefault();
        const formData = new FormData(e.currentTarget);

        onSubmitFile(formData);
      },
      [onSubmitFile],
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
    </>
  );
}

export default FileUploadForm;
