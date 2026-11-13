import { Link, useParams } from "react-router";
import useClientApi, { useCreateGoal, useGoals } from "../api/clientApi";

function ClientPage() {
  const { id } = useParams();
  const { clients } = useClientApi();
  const goals = useGoals(id);
  const createGoal = useCreateGoal(id);
  const client = clients.find((c) => c.id === id);

  if (!client) {
    return <p>Loading...</p>;
  }

  return (
    <div>
      <h2>
        <Link to="/clients">Clients</Link> &gt; {client.codeName}
      </h2>
      <p>ID: {client.id}</p>
      <p>Date of birth: {client.dateOfBirth}</p>
      <p>Gender: {client.gender}</p>
      <p>Council: {client.council}</p>
      <p>Ethnicity: {client.ethnicity}</p>
      <p>EAL: {client.englishAdditionalLanguage}</p>
      <p>Disability status: {client.disabilityStatus}</p>
      <p>Care experience: {client.careExperience}</p>
      <p>Funding source: {client.fundingSource}</p>
      <h3>Goals</h3>
      <ul>
        {goals.map((g) => (
          <li key={g.id}>
            {g.type}: {g.description}
          </li>
        ))}
      </ul>
      <form
        onSubmit={(e) => {
          e.preventDefault();
          const form = e.currentTarget;
          const data = new FormData(form);
          createGoal.mutate(
            { type: data.get("type") as string, description: data.get("description") as string },
            { onSuccess: () => form.reset() },
          );
        }}
      >
        <input name="type" placeholder="Type" required />
        <input name="description" placeholder="Description" required />
        <button type="submit" disabled={createGoal.isPending}>Add goal</button>
        {createGoal.isError && <p>{createGoal.error.message}</p>}
      </form>
    </div>
  );
}

export default ClientPage;
