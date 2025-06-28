import ApplicationMessages from './components/ApplicationMessages';
import FileUploadForm from './components/FileUploadForm';
import InProgressSpinner from './components/InProgressSpinner';
import UploadFileTable from './components/UploadFileTable';

function App() {

  return (
    <div className='container'>
      <h1>Strength & Difficulties Analysis</h1>
      <ApplicationMessages />
      <InProgressSpinner />
      <FileUploadForm />
      <UploadFileTable />
    </div>
  )
}

export default App
