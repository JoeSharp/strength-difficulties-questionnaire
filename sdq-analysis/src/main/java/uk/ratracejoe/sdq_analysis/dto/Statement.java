package uk.ratracejoe.sdq_analysis.dto;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Statement {
    CONSIDERATE(Category.ProSocial, false),
    RESTLESS(Category.HyperActivity, false),
    COMPLAINS_ACHES(Category.Emotional, false),
    SHARES_READILY(Category.ProSocial, false),
    TEMPER(Category.Conduct, false),
    SOLITARY(Category.Peer, false),
    OBEDIENT(Category.Conduct, true),
    WORRIES(Category.Emotional, false),
    HELPFUL(Category.ProSocial, false),
    FIDGETING(Category.HyperActivity, false),
    ONE_GOOD_FRIEND(Category.Peer, true),
    FIGHTS(Category.Conduct, false),
    UNHAPPY(Category.Emotional, false),
    LIKED_BY_OTHERS(Category.Peer, true),
    DISTRACTED(Category.HyperActivity, false),
    NERVOUS(Category.Emotional, false),
    KIND_TO_YOUNGER(Category.ProSocial, false),
    LIES(Category.Conduct, false),
    PICKED_ON(Category.Peer, false),
    VOLUNTEERS(Category.ProSocial, false),
    THINKS_THROUGH(Category.HyperActivity, true),
    STEALS(Category.Conduct, false),
    GETS_ON_ADULTS_BETTER(Category.Peer, false),
    FEARS(Category.Emotional, false),
    ATTENTION(Category.HyperActivity, true);

    final Category category;
    final boolean isTruePositive;

    public boolean isTruePositive() {
        return isTruePositive;
    }
    public Category category() {
        return this.category;
    }
}
