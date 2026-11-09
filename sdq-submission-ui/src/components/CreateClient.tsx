import { useState } from "react";
import type React from "react";
import useClientApi from "../api/clientApi";
import "./CreateClient.scss";

const OPTIONS = {
  gender: [
    ["MALE", "Male"],
    ["FEMALE", "Female"],
    ["NON_BINARY", "Non-binary"],
    ["OTHER", "Other"],
    ["PREFER_NOT_TO_SAY", "Prefer not to say"],
  ],
  council: [
    ["CHELTENHAM", "Cheltenham"],
    ["GLOUCESTER_CITY", "Gloucester City"],
    ["STROUD", "Stroud"],
    ["TEWKESBURY", "Tewkesbury"],
    ["FOREST_OF_DEAN", "Forest of Dean"],
    ["NORTH_COTSWOLDS", "North Cotswolds"],
    ["OUT_OF_COUNTY", "Out of County"],
    ["UNKNOWN", "Unknown"],
  ],
  ethnicity: [
    ["WHITE_BRITISH", "White British"],
    ["WHITE_EUROPEAN", "White European"],
    ["MIXED", "Mixed"],
    ["ASIAN", "Asian/Asian British"],
    ["BLACK", "Black/African/Caribbean/Black British"],
    ["TRAVELLER", "Traveller"],
    ["OTHER", "Other"],
  ],
  eal: [
    ["YES", "Yes"],
    ["NO", "No"],
    ["PREFER_NOT_TO_SAY", "Prefer not to say"],
  ],
  disabilityStatus: [
    ["DISABILITY", "Disability"],
    ["NO_DISABILITY", "No disability"],
    ["PREFER_NOT_TO_SAY", "Prefer not to say"],
  ],
  disabilityTypes: [
    ["PHYSICAL", "Physical"],
    ["SENSORY", "Sensory impairment"],
    ["LEARNING", "Learning"],
    ["NEURODIVERSE", "Neurodivergence"],
    ["MENTAL_HEALTH_CONDITION", "Mental health condition"],
    ["CHRONIC", "Long-term or chronic illness"],
    ["SPEECH_OR_COMMUNICATION", "Speech or communication"],
    ["COGNITIVE_OR_MEMORY", "Cognitive or memory impairment"],
    ["OTHER", "Other"],
    ["NOT_APPLICABLE", "Not applicable"],
    ["PREFER_NOT_TO_SAY", "Prefer not to say"],
  ],
  careExperience: [
    ["NO", "No"],
    ["YES_ADOPTED", "Yes - adopted"],
    ["YES_CHILD_IN_CARE", "Yes - child in care"],
    ["SGO", "Special Guardianship Order"],
    ["KINSHIP", "Kinship"],
    ["UNKNOWN", "Unknown"],
  ],
  fundingSource: [
    ["EHCP", "EHCP"],
    ["PEP", "PEP"],
    ["ASGSF", "ASGSF"],
    ["PRIVATE", "Private"],
    ["OTHER_CHARITABLE", "Other charitable"],
    ["SUBSIDISED_SESSION_FUND", "Subsidised Session Fund"],
    ["PROJECT", "Project"],
    ["UNKNOWN", "Unknown"],
  ],
} as const;

const INTERVENTION_TYPES = ["CCPT", "CPRT", "PTP", "IA", "UNKNOWN"];
const ACE_TYPES = [
  "COMMUNITY",
  "SOCIO_ECONOMIC",
  "DISCRIMINATION",
  "HEALTH",
  "EDUCATION",
  "BEREAVEMENT",
  "DIGITAL_ONLINE",
  "ENVIRONMENTAL",
  "CHILD_WELFARE",
  "GENERIC",
];

function parseInterventions(value: string) {
  if (!value.trim()) return [];
  return value.split(",").map((entry) => {
    const match = /^\s*([A-Z]+)\s*(?:\(\s*(\d+)\s*\)|:\s*(\d+))?\s*$/i.exec(
      entry,
    );
    const type = match?.[1].toUpperCase();
    const sessions = match?.[2] ?? match?.[3] ?? "0";
    if (!match || !type || !INTERVENTION_TYPES.includes(type)) {
      throw new Error(`Invalid intervention "${entry}".`);
    }
    return { type, sessions: Number(sessions) };
  });
}

function parseAces(value: string): Record<string, number> {
  if (!value.trim()) return {};
  return Object.fromEntries(
    value.split(",").map((entry) => {
      const match = /^\s*([A-Z_]+)\s*:\s*(\d+)\s*$/i.exec(entry);
      const type = match?.[1].toUpperCase();
      if (!match || !type || !ACE_TYPES.includes(type)) {
        throw new Error(`Invalid ACES entry "${entry}".`);
      }
      return [type, Number(match[2])];
    }),
  );
}

function CreateClient() {
  const { createClient } = useClientApi();
  const [formError, setFormError] = useState("");
  const [disabilityTypes, setDisabilityTypes] = useState<string[]>([]);
  const [disabilityStatus, setDisabilityStatus] = useState("");
  const [hasAttemptedSubmit, setHasAttemptedSubmit] = useState(false);
  const [fieldHasData, setFieldHasData] = useState<Record<string, boolean>>({});

  const updateValidationState = (form: HTMLFormElement) => {
    const formData = new FormData(form);
    const fieldNames = new Set([...Object.keys(formData), "disabilityTypes"]);
    setFieldHasData(
      Object.fromEntries(
        [...fieldNames].map((name) => [
          name,
          name === "disabilityTypes"
            ? formData.getAll(name).length > 0 ||
              formData.get("disabilityStatus") === "PREFER_NOT_TO_SAY" ||
              formData.get("disabilityStatus") === "NO_DISABILITY"
            : formData
                .getAll(name)
                .some((value) => String(value).trim().length > 0),
        ]),
      ),
    );
    setHasAttemptedSubmit(true);
  };

  const validationClass = (name: string) =>
    hasAttemptedSubmit
      ? fieldHasData[name]
        ? "client-form-field--filled"
        : "client-form-field--empty"
      : "";

  const updateDisabilityType = (type: string, checked: boolean) => {
    setDisabilityTypes((current) => {
      if (!checked) return current.filter((value) => value !== type);
      if (type === "PREFER_NOT_TO_SAY" || type === "NOT_APPLICABLE") {
        return [type];
      }
      return [
        ...current.filter(
          (value) =>
            value !== "PREFER_NOT_TO_SAY" && value !== "NOT_APPLICABLE",
        ),
        type,
      ];
    });
  };

  const onSubmit: React.FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const values = new FormData(form);

    try {
      setFormError("");
      createClient.mutate(
        {
          codeName: String(values.get("codeName") ?? "").trim(),
          dateOfBirth: String(values.get("dateOfBirth") ?? ""),
          gender: String(values.get("gender") ?? ""),
          council: String(values.get("council") ?? ""),
          ethnicity: String(values.get("ethnicity") ?? ""),
          englishAdditionalLanguage: String(values.get("eal") ?? ""),
          disabilityStatus,
          disabilityTypes,
          careExperience: String(values.get("careExperience") ?? ""),
          interventions: parseInterventions(
            String(values.get("interventionType") ?? ""),
          ),
          aces: parseAces(String(values.get("aces") ?? "")),
          fundingSource: String(values.get("fundingSource") ?? ""),
        },
        {
          onSuccess: () => {
            form.reset();
            setDisabilityTypes([]);
            setDisabilityStatus("");
            setHasAttemptedSubmit(false);
            setFieldHasData({});
          },
        },
      );
    } catch (error) {
      setFormError(
        error instanceof Error ? error.message : "Invalid client data",
      );
    }
  };

  return (
    <section className="client-capture">
      <header className="client-capture__heading">
        <h2>Add a new client</h2>
        <p>
          Add the client details below. Their ID will be created automatically.
        </p>
      </header>

      {createClient.isSuccess && (
        <div
          className="client-capture__notice client-capture__notice--success"
          role="status"
        >
          <span className="client-capture__notice-icon" aria-hidden="true">
            &#10003;
          </span>
          <div>
            <strong>Client added to the shared list</strong>
            <p>
              {createClient.data?.codeName
                ? `${createClient.data.codeName} is ready to find in SDQ Analysis.`
                : "The new record is ready to find in SDQ Analysis."}
            </p>
            {createClient.data?.submittedAt && (
              <p>
                Submitted at{" "}
                {new Date(createClient.data.submittedAt).toLocaleString()}
              </p>
            )}
          </div>
        </div>
      )}
      {createClient.isError && (
        <p
          className="client-capture__notice client-capture__notice--error"
          role="alert"
        >
          {createClient.error.message}
        </p>
      )}
      {formError && (
        <p
          className="client-capture__notice client-capture__notice--error"
          role="alert"
        >
          {formError}
        </p>
      )}

      <form
        className="client-capture__form"
        onChange={(event) => {
          if (hasAttemptedSubmit) {
            updateValidationState(event.currentTarget);
          }
        }}
        onInvalidCapture={(event) => updateValidationState(event.currentTarget)}
        onSubmit={(event) => {
          updateValidationState(event.currentTarget);
          onSubmit(event);
        }}
      >
        <div className="client-form-grid">
          <div className={`client-form-field ${validationClass("codeName")}`}>
            <label htmlFor="client-code-name">Code name *</label>
            <input
              id="client-code-name"
              name="codeName"
              className="client-form-input"
              required
            />
          </div>
          <div
            className={`client-form-field ${validationClass("dateOfBirth")}`}
          >
            <label htmlFor="client-date-of-birth">Date of birth *</label>
            <input
              id="client-date-of-birth"
              name="dateOfBirth"
              type="date"
              className="client-form-input"
              required
            />
          </div>
          {(
            [
              ["gender", "Gender"],
              ["council", "Council"],
              ["ethnicity", "Ethnicity"],
              ["eal", "English as an additional language"],
              ["disabilityStatus", "Disability status"],
              ["careExperience", "Care experience"],
              ["fundingSource", "Funding source"],
            ] as const
          ).map(([name, label]) => (
            <div
              className={`client-form-field ${validationClass(name)}`}
              key={name}
            >
              <label htmlFor={`client-${name}`}>{label} *</label>
              <select
                id={`client-${name}`}
                name={name}
                className="client-form-select"
                required
                value={
                  name === "disabilityStatus" ? disabilityStatus : undefined
                }
                defaultValue={name === "disabilityStatus" ? undefined : ""}
                onChange={
                  name === "disabilityStatus"
                    ? (event) => {
                        const status = event.currentTarget.value;
                        setDisabilityStatus(status);
                        if (status === "PREFER_NOT_TO_SAY") {
                          setDisabilityTypes(["PREFER_NOT_TO_SAY"]);
                        } else if (status === "NO_DISABILITY") {
                          setDisabilityTypes(["NOT_APPLICABLE"]);
                        } else {
                          setDisabilityTypes((current) =>
                            current.filter(
                              (type) =>
                                type !== "PREFER_NOT_TO_SAY" &&
                                type !== "NOT_APPLICABLE",
                            ),
                          );
                        }
                      }
                    : undefined
                }
              >
                <option value="">Select {label.toLowerCase()}...</option>
                {OPTIONS[name].map(([value, optionLabel]) => (
                  <option key={value} value={value}>
                    {optionLabel}
                  </option>
                ))}
              </select>
            </div>
          ))}
          <details
            className={`client-form-disability-dropdown ${
              hasAttemptedSubmit
                ? fieldHasData.disabilityTypes
                  ? "client-form-disability-dropdown--filled"
                  : "client-form-disability-dropdown--empty"
                : ""
            }`}
          >
            <summary>
              <span>
                <strong>Disability types</strong>
                <small>Select all that apply</small>
              </span>
              <span className="client-form-disability-summary">
                {disabilityTypes.length
                  ? disabilityTypes
                      .map(
                        (type) =>
                          OPTIONS.disabilityTypes.find(
                            ([value]) => value === type,
                          )?.[1] ?? type,
                      )
                      .join(", ")
                  : "None selected"}
              </span>
            </summary>
            <div className="client-form-disability-options">
              {OPTIONS.disabilityTypes.map(([value, label]) => (
                <label className="client-form-disability-option" key={value}>
                  <input
                    checked={disabilityTypes.includes(value)}
                    disabled={
                      disabilityStatus === "PREFER_NOT_TO_SAY" ||
                      disabilityStatus === "NO_DISABILITY" ||
                      (value === "PREFER_NOT_TO_SAY" &&
                        disabilityStatus !== "PREFER_NOT_TO_SAY") ||
                      (value === "NOT_APPLICABLE" &&
                        disabilityStatus !== "NO_DISABILITY")
                    }
                    name="disabilityTypes"
                    onChange={(event) =>
                      updateDisabilityType(value, event.currentTarget.checked)
                    }
                    type="checkbox"
                    value={value}
                  />
                  {label}
                </label>
              ))}
            </div>
          </details>
          <div
            className={`client-form-field ${validationClass("interventionType")}`}
          >
            <label htmlFor="client-interventions">Interventions</label>
            <input
              id="client-interventions"
              name="interventionType"
              className="client-form-input"
            />
          </div>
          <div className={`client-form-field ${validationClass("aces")}`}>
            <label htmlFor="client-aces">ACES</label>
            <input id="client-aces" name="aces" className="client-form-input" />
          </div>
        </div>

        <div className="client-capture__actions">
          <button
            type="submit"
            className="client-form-submit"
            disabled={createClient.isPending}
            onClick={(event) => {
              const form = event.currentTarget.form;
              if (form) updateValidationState(form);
            }}
          >
            {createClient.isPending ? "Saving client..." : "Add client"}
          </button>
        </div>
      </form>
    </section>
  );
}

export default CreateClient;
