import type { RouteObject } from "react-router";


import Page1 from "./components/Page1"; import Page2 from "./components/Page2"; import Page3 from "./components/Page3"; import App from "./App";

const ROUTES: RouteObject[] = [ { path: "/", element: <App />, children: [ { index: true, element: <Page1 />, }, { path: "page1", element: <Page1 />, }, { path: "page2", element: <Page2 />, }, { path: "page3", element: <Page3 />, }, { path: "*", element: <h2>404 - Page Not Found</h2>, }, ], }, ];

export default ROUTES;