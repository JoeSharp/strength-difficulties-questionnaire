import type { Progress } from "@/api/SdqApi/sdqApi";

interface Props {
  data: Record<string, Progress>;
}

interface ProgressProps {
  progressKey?: string;
  progress: Progress;
}

export const ProgressCell: React.FC<ProgressProps> = ({
  progressKey,
  progress,
}) => {
  if (!progress) return null;
  return (
    <div>
      {progressKey && `${progressKey}: `}
      {progress.first} to {progress.last} ({progress.delta >= 0 ? "+" : ""}
      {progress.delta})
    </div>
  );
};

const KeyedProgressCell: React.FC<Props> = ({ data }) => {
  return (
    <>
      {Object.entries(data).map(([key, value]) => (
        <ProgressCell key={key} progressKey={key} progress={value} />
      ))}
    </>
  );
};

export default KeyedProgressCell;
