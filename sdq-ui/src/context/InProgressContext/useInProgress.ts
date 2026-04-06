import React from "react";

export interface IInProgress {
    id: number,
    message: string,
}

export interface IInProgressState {
    jobs: IInProgress[];
    beginJob: (message: string) => number;
    endJob: (id: number) => void;
}

let nextId = 0;

function useInProgress(): IInProgressState {
    const [jobs, setJobs] = React.useState<IInProgress[]>([]);
    const beginJob = React.useCallback((message: string) => {
        const newJob = {id: nextId++, message};
        setJobs(p => ([...p, newJob]));
        return newJob.id;
    }, []);
    const endJob = React.useCallback((id: number) => {
        setJobs(p => p.filter(e => e.id !== id));
    }, []);

    return {
        jobs,
        beginJob,
        endJob
    }
}

export default useInProgress;

