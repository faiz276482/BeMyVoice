package com.nerdytech.bemyvoice.model.android;

public class Word {
    String meaning;
    String most_liked;
    int votes;

    public Word(String meaning, String most_liked, int votes) {
        this.meaning = meaning;
        this.most_liked = most_liked;
        this.votes = votes;
    }

    public Word() {
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
