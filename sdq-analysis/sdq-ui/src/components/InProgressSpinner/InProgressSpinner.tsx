import useInProgressContext from "../../context/InProgressContext";
import JobSpinner from "./JobSpinner";

function InProgressSpinner() {
    const { jobs } = useInProgressContext();

    if (jobs.length === 0) {
        return null;
    }

    return <div>
        {jobs.map(job => <JobSpinner key={job.id} job={job} />)}
    </div>
}

export default InProgressSpinner;