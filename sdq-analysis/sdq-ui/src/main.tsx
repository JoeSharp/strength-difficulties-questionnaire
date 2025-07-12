import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import "./index.css";
import App from "./App";
import InProgressContextProvider from "./context/InProgressContext/InProgressContextProvider";
import ApplicationErrorContextProvider from "./context/AppNotificationContext/AppNotificationContextProvider";
import ApiContextProvider from "./context/ApiContext/ApiContextProvider";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <BrowserRouter>
      <ApplicationErrorContextProvider>
        <InProgressContextProvider>
          <ApiContextProvider>
            <App />
          </ApiContextProvider>
        </InProgressContextProvider>
      </ApplicationErrorContextProvider>
    </BrowserRouter>
  </StrictMode>
);
