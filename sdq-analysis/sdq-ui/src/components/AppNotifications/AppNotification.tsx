import type { IAppNotification } from "../../context/AppNotificationContext/useAppNotifications";

interface Props {
  message: IAppNotification;
  onDismiss: () => void;
}

function AppNotification({ message, onDismiss }: Props) {
  return (
    <div
      className={`alert alert-${message.type} alert-dismissible`}
      role="alert"
    >
      <strong>HTTP Status: {message.status}</strong> {message.message}
      <button type="button" className="btn-close" onClick={onDismiss} />
    </div>
  );
}

export default AppNotification;
