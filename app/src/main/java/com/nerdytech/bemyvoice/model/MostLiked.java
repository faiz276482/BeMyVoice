package com.nerdytech.bemyvoice.model;

public class MostLiked {
    String most_liked;
    int votes;
    String meaning;

    public MostLiked(String most_liked, int votes,String meaning) {
        this.most_liked = most_liked;
        this.votes = votes;
        this.meaning=meaning;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getMost_liked() {
        return most_liked;
    }

    public void setMost_liked(String most_liked) {
        this.most_liked = most_liked;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
