import ApplicationMessages from "./components/ApplicationMessages";
import FileUploadForm from "./components/FileUploadForm";
import InProgressSpinner from "./components/InProgressSpinner";
import ClientFileTable from "./components/ClientFileTable";
import SdqScoresTable from "./components/SdqScoresTable";
import DeleteAllButton from "./components/DeleteAllButton";
import ManageDatabaseForm from "./components/ManageDatabaseForm";
import useApiContext from "./context/ApiContext";

function App() {
  const {
    databaseApi: { exists },
  } = useApiContext();
  return (
    <div className="container">
      <h1>Strength & Difficulties Analysis</h1>
      {!exists && <ManageDatabaseForm />}
      {exists && (
        <>
          <ApplicationMessages />
          <InProgressSpinner />
          <DeleteAllButton />
          <FileUploadForm />
          <ClientFileTable />
          <SdqScoresTable />
        </>
      )}
    </div>
  );
}

export default App;
