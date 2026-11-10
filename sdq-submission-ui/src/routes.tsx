import { Outlet, type RouteObject } from "react-router";

import App from "./App";
import Page1 from "./components/Page1";
import Page2 from "./components/Page2";
import Page3 from "./components/Page3";
import CreateClient from "./components/CreateClient";
import SubmitSDQ from "./components/SubmitSDQ";
import ClientPage from "./components/ClientPage";

const ROUTES: RouteObject[] = [
  {
    path: "/",
    element: (
      <App>
        <Outlet />
      </App>
    ),
    children: [
      {
        path: "/page1",
        element: <Page1 />,
      },
      {
        path: "/page2",
        element: <Page2 />,
      },
      {
        path: "/page3",
        element: <Page3 />,
      },
      {
        path: "/createClient",
        element: <CreateClient />,
      },
      {
        path: "/sdq",
        element: <SubmitSDQ />,
      },
      {
        path: "/client/:id",
        element: <ClientPage />,
      },
      { path: "*", element: <h2>404 - Page Not Found</h2> },
    ],
  },
];

export default ROUTES;
