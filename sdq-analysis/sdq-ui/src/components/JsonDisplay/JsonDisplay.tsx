import Card from "../Card";

interface Props {
    id: string;
    title: string;
    value: object;
}

function JsonDisplay({ id, title, value }: Props) {
    const lastTaskJson = JSON.stringify(value, null, 2);

    return <Card id={id} title={title}>
        <pre className="bg-light p-3 border rounded">
            <code id="json-output">
                {lastTaskJson}
            </code>
        </pre>
    </Card>
}

export default JsonDisplay;