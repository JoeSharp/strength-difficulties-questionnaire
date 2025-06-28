import ApplicationMessages from './components/ApplicationMessages';
import FileUploadForm from './components/FileUploadForm';
import InProgressSpinner from './components/InProgressSpinner';

function App() {

  return (
    <div className='container'>
      <h1>Strength & Difficulties Analysis</h1>
      <ApplicationMessages />
      <InProgressSpinner />
      <FileUploadForm />
    </div>
  )
}

export default App
