import { useNavigate } from "react-router";
import "./Nav.scss";
import useClientApi from "../api/clientApi";

function Page3() {
  const { clients } = useClientApi();
  const navigate = useNavigate();

  return (
    <div>
      <h2>Page 3</h2>
      <div className="cltable">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Code Name</th>
              <th>Date of Birth</th>
              <th>Gender</th>
              <th>Council</th>
              <th>Ethnicity</th>
              <th>EAL</th>
              <th>Disability Status</th>
              <th>Care Experience</th>
              <th>Funding Source</th>
            </tr>
          </thead>
          <tbody>
            {clients.map((client) => (
              <tr
                key={client.id}
                onClick={() => navigate(`/client/${client.id}`)}
              >
                <th>{client.id}</th>
                <th>{client.codeName}</th>
                <th>{client.dateOfBirth}</th>
                <th>{client.gender}</th>
                <th>{client.council}</th>
                <th>{client.ethnicity}</th>
                <th>{client.englishAdditionalLanguage}</th>
                <th>{client.disabilityStatus}</th>
                <th>{client.careExperience}</th>
                <th>{client.fundingSource}</th>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default Page3;
