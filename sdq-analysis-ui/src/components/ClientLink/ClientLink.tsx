import { Link } from "react-router-dom";

type Props = {
  clientId: string;
};
const ClientLink: React.FC<Props> = ({ clientId }) => {
  return <Link to={`/client/${clientId}`}>{clientId}</Link>;
};
export default ClientLink;
