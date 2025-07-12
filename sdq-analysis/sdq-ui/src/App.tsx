import { Routes, Route, Link } from "react-router-dom";
import AppNotifications from "./components/AppNotifications";
import FileUploadForm from "./components/FileUploadForm";
import InProgressSpinner from "./components/InProgressSpinner";
import ClientFileTable from "./components/ClientFileTable";
import DeleteDatabaseButton from "./components/DeleteDatabaseButton";
import useApiContext from "./context/ApiContext";
import ClientFilePage from "./components/ClientFilePage";

function Home() {
  return (
    <>
      <FileUploadForm />
      <ClientFileTable />
    </>
  );
}

function DbExistsUi() {
  return (
    <div>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/client/:id" element={<ClientFilePage />} />
      </Routes>
    </div>
  );
}

function DbDoesNotExist() {
  return (
    <div>
      <p>The database currently does not exist</p>
      <p>
        Upload some files and the system will create the database and put the
        first data items in.
      </p>
      <FileUploadForm />
    </div>
  );
}

function App() {
  const {
    databaseApi: { exists },
  } = useApiContext();
  return (
    <div className="container-fluid py-3">
      <div className="d-flex justify-content-between align-items-center">
        <h1 className="mb-0">Strength & Difficulties Analysis</h1>

        <div className="top-left-header">
          {exists && <DeleteDatabaseButton />}
          <AppNotifications />
        </div>
      </div>
      <InProgressSpinner />
      {exists ? <DbExistsUi /> : <DbDoesNotExist />}
    </div>
  );
}

export default App;
