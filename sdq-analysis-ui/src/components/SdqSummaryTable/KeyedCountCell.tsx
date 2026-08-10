interface Props {
  data: Record<string, number>;
}

const KeyedCountCell: React.FC<Props> = ({ data }) => {
  return (
    <>
      {Object.entries(data).map(([key, value]) => (
        <div key={key}>
          {key}: {value}
        </div>
      ))}
    </>
  );
};

export default KeyedCountCell;
