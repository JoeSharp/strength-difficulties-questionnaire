import useApplicationMessageContext from "../../context/ApplicationMessageContext";
import ApplicationMessage from "./ApplicationMessage";

function ApplicationMessages() {
    const { messages, dismissMessage } = useApplicationMessageContext();
    return <div>
        {messages.map(e => <ApplicationMessage key={e.id} message={e} onDismiss={() => dismissMessage(e.id)}/>)}
    </div>
}

export default ApplicationMessages;