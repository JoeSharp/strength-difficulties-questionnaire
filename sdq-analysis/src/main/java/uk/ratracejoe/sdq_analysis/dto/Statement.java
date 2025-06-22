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
    OBEDIENT(Category.Conduct, true);

    final Category category;
    final boolean isTruePositive;

    public boolean isTruePositive() {
        return isTruePositive;
    }
    public Category category() {
        return this.category;
    }
}
