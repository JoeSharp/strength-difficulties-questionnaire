import React from "react";
import { Routes, Route } from "react-router-dom";
import InProgressSpinner from "./components/InProgressSpinner";
import ClientFilePage from "./pages/ClientFilePage";
import BulkUploadPage from "./pages/BulkUploadPage";
import HomePage from "./pages/HomePage";
import DemographicReportPage from "./pages/DemographicReportPage";
import GoalBasedOutcomePage from "./pages/GoalBasedOutcomePage";

import SdqNav from "./components/SdqNav";

const App: React.FC = () => {
  return (
    <div className="container-fluid py-3">
      <SdqNav />
      <InProgressSpinner />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/demographic-report" element={<DemographicReportPage />} />
        <Route path="/gbo" element={<GoalBasedOutcomePage />} />
        <Route path="/client/:id" element={<ClientFilePage />} />
        <Route path="/bulk-upload" element={<BulkUploadPage />} />
      </Routes>
    </div>
  );
};

export default App;
