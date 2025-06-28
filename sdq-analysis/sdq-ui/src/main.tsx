import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import InProgressContextProvider from './context/InProgressContext/InProgressContextProvider.tsx'
import ApplicationErrorContextProvider from './context/ApplicationMessageContext/ApplicationMessageContextProvider.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ApplicationErrorContextProvider>
      <InProgressContextProvider>
        <App />
      </InProgressContextProvider>
    </ApplicationErrorContextProvider>
  </StrictMode>,
)
