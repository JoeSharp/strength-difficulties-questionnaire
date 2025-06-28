import React from "react";

interface Props {
    id: string;
    title: string;
    children: React.ReactNode
}

function Card({ id, title, children }: Props) {

    return <div className="card mb-4">
        <div className="card-header" role='button' data-bs-toggle='collapse' data-bs-target={`#${id}`}>{title}</div>
        <div className='collapse' id={id}>
            <div className="card-body">
                {children}
            </div>
        </div>
    </div>
}

export default Card;