import Nav from "./components/Nav"; import { Outlet } from "react-router";

function App() { return ( <main> <h1>SDQ Submission UI</h1> <Nav /> <Outlet /> </main> ); }

export default App;
