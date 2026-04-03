import React from "react";

import "./card.scss";

interface Props {
  title: string;
  children: React.ReactNode;
}

function Card({ title, children }: Props) {
  return (
    <div className={`card card--hover`}>
      {title && <div className="card__header">{title}</div>}
      <div className="card__body">{children}</div>
    </div>
  );
}

export default Card;
