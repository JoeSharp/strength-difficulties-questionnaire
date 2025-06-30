import ApplicationMessages from './components/ApplicationMessages';
import FileUploadForm from './components/FileUploadForm';
import InProgressSpinner from './components/InProgressSpinner';
import UploadFileTable from './components/UploadFileTable';
import SdqScoresTable from './components/SdqScoresTable';
import DeleteAllButton from './components/DeleteAllButton';

function App() {

  return (
    <div className='container'>
      <h1>Strength & Difficulties Analysis</h1>
      <ApplicationMessages />
      <InProgressSpinner />
      <DeleteAllButton />
      <FileUploadForm />
      <UploadFileTable />
      <SdqScoresTable />
    </div>
  )
}

export default App
