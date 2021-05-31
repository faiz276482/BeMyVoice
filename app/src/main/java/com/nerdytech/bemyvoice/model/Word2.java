package com.nerdytech.bemyvoice.model;

import java.util.List;

public class Word2 {
    String meaning;
    String most_liked;
    int votes;
    String word;

    public Word2(String meaning, String most_liked, int votes, String word) {
        this.meaning = meaning;
        this.most_liked = most_liked;
        this.votes = votes;
        this.word=word;
    }

    public Word2() {
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
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
