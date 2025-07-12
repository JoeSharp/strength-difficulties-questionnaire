import React from "react";
import useAppNotificationContext from "../../context/AppNotificationContext";
import AppNotification from "./AppNotification";

import "./AppNotifications.css";

function AppNotifications() {
  const { messages, dismissMessage } = useAppNotificationContext();
  const [show, setShow] = React.useState<boolean>(false);

  const boxStyle = React.useMemo(() => {
    let style = "notification-list p-3";
    if (show) {
      style += " show";
    }
    return style;
  }, [show]);

  React.useEffect(() => {
    if (messages.length === 0) {
      setShow(false);
    }
  }, [messages]);

  return (
    <div className="notification-bell">
      <button
        id="bellBtn"
        className="btn btn-light position-relative"
        onClick={() => setShow((p) => !p)}
      >
        🔔
        {messages.length > 0 && (
          <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
            {messages.length}
          </span>
        )}
      </button>
      <div id="notificationBox" className={boxStyle}>
        <h6 className="border-bottom pb-2 mb-2">Notifications</h6>
        <ul className="list-unstyled mb-0">
          {messages.map((e) => (
            <AppNotification
              key={e.id}
              message={e}
              onDismiss={() => dismissMessage(e.id)}
            />
          ))}
        </ul>
      </div>
    </div>
  );
}

export default AppNotifications;
