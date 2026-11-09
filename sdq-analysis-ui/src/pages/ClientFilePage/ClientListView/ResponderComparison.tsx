import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import type { ClientFile } from "@/api/ClientApi/clientApi";
import useSdqReportingPeriods from "@/api/SdqApi/useSdqReportingPeriods";
import type {
  ReportingPeriod,
  SdqSubmissionSummary,
} from "@/api/SdqApi/sdqApi";
import { ASSESSORS, type Assessor } from "@/api/types";

import "./ResponderComparison.scss";

type Props = {
  clients: ClientFile[];
};

function isAssessor(value: string): value is Assessor {
  return (ASSESSORS as readonly string[]).includes(value);
}

function ResponderScore({
  summary,
  assessor,
}: {
  summary: SdqSubmissionSummary;
  assessor: Assessor;
}) {
  return (
    <div className="responder-comparison__score">
      <h4>{assessor}</h4>
      <p>
        <strong>Total difficulties:</strong> {summary.totalDifficulties}
      </p>
      <div>
        <strong>Categories</strong>
        <ul>
          {Object.entries(summary.categorySubTotals).map(([category, score]) => (
            <li key={category}>
              {category}: {score}
            </li>
          ))}
        </ul>
      </div>
      <div>
        <strong>Postures</strong>
        <ul>
          {Object.entries(summary.postureSubTotals).map(([posture, score]) => (
            <li key={posture}>
              {posture}: {score}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

function ComparablePeriod({
  period,
  firstSummary,
  secondSummary,
  firstAssessor,
  secondAssessor,
}: {
  period: ReportingPeriod;
  firstSummary: SdqSubmissionSummary;
  secondSummary: SdqSubmissionSummary;
  firstAssessor: Assessor;
  secondAssessor: Assessor;
}) {
  return (
    <article className="responder-comparison__period">
      <h3>{period.period}</h3>
      <div className="responder-comparison__scores">
        <ResponderScore summary={firstSummary} assessor={firstAssessor} />
        <ResponderScore summary={secondSummary} assessor={secondAssessor} />
      </div>
    </article>
  );
}

async function fetchSummary(
  periodId: string,
  assessor: Assessor,
): Promise<SdqSubmissionSummary | null> {
  const response = await fetch(
    `/api/sdq/${periodId}/${assessor}/summary`,
    {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    },
  );

  if (response.status === 404) return null;
  if (!response.ok) {
    throw new Error(
      `Could not load ${assessor} scores: ${response.status} ${response.statusText}`,
    );
  }

  return response.json();
}

function ClientComparison({
  clientId,
  firstAssessor,
  secondAssessor,
}: {
  clientId: string;
  firstAssessor: Assessor;
  secondAssessor: Assessor;
}) {
  const { data: reportingPeriods, isLoading, error } =
    useSdqReportingPeriods(clientId);
  const comparisonQuery = useQuery({
    queryKey: [
      "sdqResponderComparison",
      clientId,
      firstAssessor,
      secondAssessor,
      reportingPeriods?.map(({ periodId }) => periodId),
    ],
    enabled: reportingPeriods !== undefined,
    queryFn: async () => {
      if (!reportingPeriods) return [];

      return Promise.all(
        reportingPeriods.map(async (period) => {
          const [firstSummary, secondSummary] = await Promise.all([
            fetchSummary(period.periodId, firstAssessor),
            fetchSummary(period.periodId, secondAssessor),
          ]);
          return { period, firstSummary, secondSummary };
        }),
      );
    },
  });

  if (isLoading) return <p>Loading reporting periods...</p>;
  if (error) return <p role="alert">Could not load reporting periods.</p>;
  if (!reportingPeriods) return null;
  if (comparisonQuery.isLoading) return <p>Loading responder scores...</p>;
  if (comparisonQuery.error) {
    return <p role="alert">{comparisonQuery.error.message}</p>;
  }

  if (reportingPeriods.length === 0) {
    return (
      <p>No SDQ reporting periods are available for this client.</p>
    );
  }

  const comparablePeriods =
    comparisonQuery.data?.filter(
      (period) => period.firstSummary && period.secondSummary,
    ) ?? [];

  if (comparablePeriods.length === 0) {
    return (
      <p>
        No reporting periods have responses from both {firstAssessor} and{" "}
        {secondAssessor}.
      </p>
    );
  }

  return (
    <div className="responder-comparison__periods">
      {comparablePeriods.map(
        ({ period, firstSummary, secondSummary }) =>
          firstSummary &&
          secondSummary && (
        <ComparablePeriod
          key={period.periodId}
          period={period}
          firstSummary={firstSummary}
          secondSummary={secondSummary}
          firstAssessor={firstAssessor}
          secondAssessor={secondAssessor}
        />
          ),
      )}
    </div>
  );
}

function ResponderComparison({ clients }: Props) {
  const [selectedClientId, setSelectedClientId] = useState("");
  const [firstAssessor, setFirstAssessor] = useState<Assessor>("Parent1");
  const [secondAssessor, setSecondAssessor] = useState<Assessor>("School");
  const selectedClient =
    clients.find((client) => client.clientId === selectedClientId) ?? clients[0];

  return (
    <section className="responder-comparison">
      <h2>Compare responders</h2>
      <p>
        Compare SDQ category, posture, and total-difficulty scores for two
        responders side by side. Periods without a response from one responder
        are marked in the comparison.
      </p>
      {clients.length === 0 ? (
        <p>Search for clients to choose one to compare.</p>
      ) : (
        <>
          <div className="responder-comparison__controls">
            <label>
              Client
              <select
                value={selectedClient?.clientId ?? ""}
                onChange={(event) => setSelectedClientId(event.target.value)}
              >
                {clients.map((client) => (
                  <option key={client.clientId} value={client.clientId}>
                    {client.codeName} ({client.clientId})
                  </option>
                ))}
              </select>
            </label>
            <label>
              First responder
              <select
                value={firstAssessor}
                onChange={(event) => {
                  if (isAssessor(event.target.value)) {
                    setFirstAssessor(event.target.value);
                  }
                }}
              >
                {ASSESSORS.map((assessor) => (
                  <option
                    key={assessor}
                    value={assessor}
                    disabled={assessor === secondAssessor}
                  >
                    {assessor}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Second responder
              <select
                value={secondAssessor}
                onChange={(event) => {
                  if (isAssessor(event.target.value)) {
                    setSecondAssessor(event.target.value);
                  }
                }}
              >
                {ASSESSORS.map((assessor) => (
                  <option
                    key={assessor}
                    value={assessor}
                    disabled={assessor === firstAssessor}
                  >
                    {assessor}
                  </option>
                ))}
              </select>
            </label>
          </div>
          {selectedClient && (
            <ClientComparison
              key={selectedClient.clientId}
              clientId={selectedClient.clientId}
              firstAssessor={firstAssessor}
              secondAssessor={secondAssessor}
            />
          )}
        </>
      )}
    </section>
  );
}

export default ResponderComparison;
