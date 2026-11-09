import Nav from "./components/Nav";

function App({ children }: { children: React.ReactNode }) {
  return (
    <div className="app-shell">
      <header className="app-header">
        <img
          className="app-header__logo"
          src="/every-cloud-logo.png"
          alt="Every Cloud"
        />
        <div className="app-header__copy">
          <p>Strengths &amp; Difficulties Questionnaire</p>
          <h1>Client submission</h1>
        </div>
      </header>
      <Nav />
      <div className="app-content">{children}</div>
    </div>
  );
}

export default App;
