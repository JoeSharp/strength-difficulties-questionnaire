import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App'
import InProgressContextProvider from './context/InProgressContext/InProgressContextProvider'
import ApplicationErrorContextProvider from './context/ApplicationMessageContext/ApplicationMessageContextProvider'
import ApiContextProvider from './context/ApiContext/ApiContextProvider';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ApplicationErrorContextProvider>
      <InProgressContextProvider>
        <ApiContextProvider>
          <App />
        </ApiContextProvider>
      </InProgressContextProvider>
    </ApplicationErrorContextProvider>
  </StrictMode>,
)
