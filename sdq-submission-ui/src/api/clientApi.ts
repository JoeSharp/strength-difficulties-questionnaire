import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

export type SdqClient = {
  id: string;
  codeName: string;
  dateOfBirth: string;
  gender?: string;
  council?: string;
  ethnicity?: string;
  englishAdditionalLanguage?: string;
  disabilityStatus?: string;
  disabilityTypes?: string[];
  careExperience?: string;
  interventions?: { type: string; sessions: number }[];
  aces?: Record<string, number>;
  fundingSource?: string;
  submittedAt?: string;
};
type ClientApi = {
  clients: SdqClient[];
  createClient: ReturnType<typeof useMutation<SdqClient, Error, NewClient>>;
};

export type NewClient = Omit<SdqClient, "id" | "submittedAt">;

const EMPTY_CLIENTS: SdqClient[] = [];

async function postClient(client: NewClient): Promise<SdqClient> {
  const response = await fetch("/api/clients", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(client),
  });
  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Failed to create client (${response.status})`);
  }
  return response.json() as Promise<SdqClient>;
}

const useClientApi = (): ClientApi => {
  const queryClient = useQueryClient();
  const { data } = useQuery<SdqClient[]>({
    queryKey: ["clients"],
    queryFn: async () => {
      const response = await fetch("/api/clients");
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      return response.json() as Promise<SdqClient[]>;
    },
  });
  const createClient = useMutation({
    mutationFn: postClient,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["clients"] });
    },
  });

  return {
    clients: data || EMPTY_CLIENTS,
    createClient,
  };
};

export type Goal = {
  id: string;
  clientId: string;
  type?: string;
  description?: string;
};

export const useGoals = (clientId?: string): Goal[] => {
  const { data } = useQuery<Goal[]>({
    queryKey: ["goals", clientId],
    enabled: !!clientId,
    queryFn: async () => {
      const response = await fetch(`/api/clients/${clientId}/goals`);
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      return response.json() as Promise<Goal[]>;
    },
  });
  return data || [];
};

export const useCreateGoal = (clientId?: string) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (goal: Omit<Goal, "id" | "clientId">) => {
      const response = await fetch(`/api/clients/${clientId}/goals`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(goal),
      });
      if (!response.ok) {
        throw new Error("Failed to create goal");
      }
      return response.json() as Promise<Goal>;
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["goals", clientId] }),
  });
};

export default useClientApi;
