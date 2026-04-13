import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";

import "./styles/index.scss";

import App from "./App";
import InProgressContextProvider from "./context/InProgressContext/InProgressContextProvider";
import ApplicationErrorContextProvider from "./context/AppNotificationContext/AppNotificationContextProvider";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

const queryClient = new QueryClient();

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <BrowserRouter>
      <QueryClientProvider client={queryClient}>
        <ApplicationErrorContextProvider>
          <InProgressContextProvider>
            <App />
          </InProgressContextProvider>
        </ApplicationErrorContextProvider>
      </QueryClientProvider>
    </BrowserRouter>
  </StrictMode>,
);
