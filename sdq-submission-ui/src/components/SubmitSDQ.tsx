import { useState } from "react";
import "./sdq.scss";

type Answer = "Not True" | "Sometimes" | "True" | "";

const questions = [
  {
    id: 1,
    text: "Considerate of other peoples' feelings.",
    category: "pro-social",
  },
  {
    id: 2,
    text: "Restless, overreactive, can't stay still for long.",
    category: "hyperactivity",
  },
  {
    id: 3,
    text: "Often complains of headaches, stomachaches or sickness.",
    category: "emotional",
  },
  {
    id: 4,
    text: "Shares readily with other children. Treats, toys, pens etc.",
    category: "pro-social",
  },
  {
    id: 5,
    text: "Often has temper tantrums or hot tempers.",
    category: "conduct",
  },
  { id: 6, text: "Rather solitary, usually plays alone.", category: "peer" },
  {
    id: 7,
    text: "Generally obedient, usually does what adults request.",
    category: "conduct",
  },
  { id: 8, text: "Many worries, often seems worried.", category: "emotional" },
  {
    id: 9,
    text: "Helpful if someone is hurt, upset or feeling ill.",
    category: "pro-social",
  },
  {
    id: 10,
    text: "Constantly fidgeting or squirming.",
    category: "hyperactivity",
  },
  { id: 11, text: "Has at least one good friend.", category: "peer" },
  {
    id: 12,
    text: "Often fights with other children or bullies them.",
    category: "conduct",
  },
  {
    id: 13,
    text: "Often unhappy, downhearted or tearful.",
    category: "emotional",
  },
  { id: 14, text: "Generally liked by other children.", category: "peer" },
  {
    id: 15,
    text: "Easily distracted, concentration wanders.",
    category: "hyperactivity",
  },
  {
    id: 16,
    text: "Nervous or clingy in certain situations, easily loses confidence.",
    category: "emotional",
  },
  { id: 17, text: "Kind to younger children.", category: "pro-social" },
  { id: 18, text: "Often lies or cheats.", category: "conduct" },
  { id: 19, text: "Picked on or bullied by other children.", category: "peer" },
  {
    id: 20,
    text: "Often volunteers to help others (parents, teachers, other children).",
    category: "pro-social",
  },
  {
    id: 21,
    text: "Thinks things out before acting.",
    category: "hyperactivity",
  },
  {
    id: 22,
    text: "Steals from home, school or elsewhere.",
    category: "conduct",
  },
  {
    id: 23,
    text: "Gets on better with adults than with other children.",
    category: "peer",
  },
  { id: 24, text: "Many fears, easily scared.", category: "emotional" },
  {
    id: 25,
    text: "Sees tasks through to the end, good attention span.",
    category: "hyperactivity",
  },
];

const reverseQuestions = [7, 11, 14, 21, 25];

function normalScore(answer: Answer): number {
  switch (answer) {
    case "Not True":
      return 0;
    case "Sometimes":
      return 1;
    case "True":
      return 2;
    default:
      return 0;
  }
}

function reverseScore(answer: Answer): number {
  switch (answer) {
    case "Not True":
      return 2;
    case "Sometimes":
      return 1;
    case "True":
      return 0;
    default:
      return 0;
  }
}

function QuestionnaireKirill() {
  const [answers, setAnswers] = useState<Record<number, Answer>>({});

  const [submitted, setSubmitted] = useState(false);

  const handleAnswerChange = (questionId: number, answer: Answer) => {
    setAnswers((previousAnswers) => ({
      ...previousAnswers,
      [questionId]: answer,
    }));

    setSubmitted(false);
  };

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const allAnswered = questions.every((question) => answers[question.id]);

    if (!allAnswered) {
      alert("Please answer all 25 questions.");
      return;
    }

    setSubmitted(true);
  };

  const getQuestionScore = (questionId: number): number => {
    const answer = answers[questionId] || "";

    return reverseQuestions.includes(questionId)
      ? reverseScore(answer)
      : normalScore(answer);
  };

  const calculateScore = (category: string): number => {
    return questions
      .filter((question) => question.category === category)
      .reduce((total, question) => total + getQuestionScore(question.id), 0);
  };

  const proSocialScore = calculateScore("pro-social");
  const hyperactivityScore = calculateScore("hyperactivity");
  const emotionalScore = calculateScore("emotional");
  const conductScore = calculateScore("conduct");
  const peerScore = calculateScore("peer");

  const totalDifficultiesScore =
    emotionalScore + conductScore + hyperactivityScore + peerScore;

  return (
    <div className="questionnaire-container">
      <h2 className="questionnaire-title">
        Strengths and Difficulties Questionnaire
      </h2>

      <p>Please complete all questions.</p>

      <form className="questionnaire-form" onSubmit={handleSubmit}>
        {questions.map((question) => (
          <div className={`question ${question.category}`} key={question.id}>
            <p>
              Q{question.id}. {question.text}
            </p>

            {(["Not True", "Sometimes", "True"] as const).map((option) => (
              <label key={option}>
                <input
                  type="radio"
                  name={`q${question.id}`}
                  value={option}
                  checked={answers[question.id] === option}
                  onChange={() => handleAnswerChange(question.id, option)}
                  required
                />

                {option}
              </label>
            ))}
          </div>
        ))}

        <div className="SubmitButton">
          <button type="submit">Submit</button>
        </div>
      </form>

      {submitted && (
        <div className="questionnaire-results">
          <h2>Questionnaire Results</h2>

          <div className="result-item">
            <p>Emotional Symptoms:</p>
            <strong>{emotionalScore} / 10</strong>
          </div>

          <div className="result-item">
            <p>Conduct Problems:</p>
            <strong>{conductScore} / 10</strong>
          </div>

          <div className="result-item">
            <p>Hyperactivity:</p>
            <strong>{hyperactivityScore} / 10</strong>
          </div>

          <div className="result-item">
            <p>Peer Problems:</p>
            <strong>{peerScore} / 10</strong>
          </div>

          <div className="result-item">
            <p>Prosocial Behaviour:</p>
            <strong>{proSocialScore} / 10</strong>
          </div>

          <hr />

          <div className="result-total">
            <h3>Total Difficulties Score:</h3>
            <strong>{totalDifficultiesScore} / 40</strong>
          </div>
        </div>
      )}
    </div>
  );
}

export default QuestionnaireKirill;
