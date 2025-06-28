import type { IInProgress } from "../../context/InProgressContext/useInProgress";

interface Props {
    job: IInProgress
}

function JobSpinner({ job }: Props) {
    return <div>
        {job.message}
        <div className='spinner-border' role='status'>
            <span className='sr-only' />
        </div>
    </div>
}

export default JobSpinner;