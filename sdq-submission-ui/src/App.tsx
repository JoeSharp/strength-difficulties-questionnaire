import Nav from "./components/Nav";

function App({ children }: { children: React.ReactNode }) {
  return (
    <main>
      <h1>SDQ Submission UI</h1>
      <p> Hello this is another test </p>
      <Nav />
      {children}
    </main>
  );
}

export default App;
