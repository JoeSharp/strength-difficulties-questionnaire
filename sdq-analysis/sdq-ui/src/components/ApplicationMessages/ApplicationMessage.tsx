import type { IApplicationMessage } from "../../context/ApplicationMessageContext/useApplicationMessages";

interface Props {
    message: IApplicationMessage
    onDismiss: () => void;
}

function ApplicationMessage({message, onDismiss}: Props) {
    return <div className={`alert alert-${message.type} alert-dismissible`} role='alert'>
        <strong>HTTP Status: {message.status}</strong> {message.message}
        <button type="button" className="btn-close" onClick={onDismiss} />
    </div>
}

export default ApplicationMessage;
