import type { Assessor } from "@/api/types";
import useGetSdqProgress from "@/api/SdqApi/useGetSdqProgress";
import KeyedProgressCell, {
  ProgressCell,
} from "@/components/SdqProgressTable/KeyedProgressCell";

type Props = {
  clientId: string;
  assessor: Assessor;
};

const SdqProgressCard: React.FC<Props> = ({ clientId, assessor }) => {
  const { data: progress } = useGetSdqProgress(clientId, assessor);
  if (!progress) return null;

  return (
    <div>
      <h3>Progress</h3>
      <div>
        <KeyedProgressCell data={progress.categoryProgress} />
        <KeyedProgressCell data={progress.postureProgress} />
        <p></p>
        <ProgressCell
          progressKey="Total"
          progress={progress.totalDifficulties}
        />
      </div>
    </div>
  );
};

export default SdqProgressCard;
