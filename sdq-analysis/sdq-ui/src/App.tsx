import { Routes, Route } from "react-router-dom";
import AppNotifications from "./components/AppNotifications";
import FileUploadForm from "./components/FileUploadForm";
import InProgressSpinner from "./components/InProgressSpinner";
import ClientFileTable from "./components/ClientFileTable";
import ClientFilePage from "./components/ClientFilePage";
import DemographicReport from "./components/DemographicReport";
import ExploreData from "./components/ExploreData";

function Home() {
  return (
    <>
      <FileUploadForm />
      <ExploreData />
      <DemographicReport />
      <ClientFileTable />
    </>
  );
}

function App() {
  return (
    <div className="container-fluid py-3">
      <div className="d-flex justify-content-between align-items-center">
        <h1 className="mb-0">Strength & Difficulties Analysis</h1>

        <div className="top-left-header">
          <AppNotifications />
        </div>
      </div>
      <InProgressSpinner />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/client/:id" element={<ClientFilePage />} />
      </Routes>
    </div>
  );
}

export default App;
