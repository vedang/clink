package clink.examples.wordcount;

public class WordWithCount {
    public String word;
    public Long wcount;

    public WordWithCount(String word, long wcount) {
        this.word = word;
        this.wcount = wcount;
    }

    @Override
    public String toString() {
        return word + " : " + wcount;
    }
}
