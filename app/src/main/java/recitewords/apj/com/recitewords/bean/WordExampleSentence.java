package recitewords.apj.com.recitewords.bean;

/**
 * Created by Seven on 2016/11/28.
 */

public class WordExampleSentence {
    private String word;   //单词
    private String example_sentence;  //例句
    private String example_sentence_mean;  //例句意思
    private String example_sentence_pronounce;  //例句发音
    private String example_sentence_resource;  //例句来源

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getExample_sentence() {
        return example_sentence;
    }

    public void setExample_sentence(String example_sentence) {
        this.example_sentence = example_sentence;
    }

    public String getExample_sentence_mean() {
        return example_sentence_mean;
    }

    public void setExample_sentence_mean(String example_sentence_mean) {
        this.example_sentence_mean = example_sentence_mean;
    }

    public String getExample_sentence_pronounce() {
        return example_sentence_pronounce;
    }

    public void setExample_sentence_pronounce(String example_sentence_pronounce) {
        this.example_sentence_pronounce = example_sentence_pronounce;
    }

    public String getExample_sentence_resource() {
        return example_sentence_resource;
    }

    public void setExample_sentence_resource(String example_sentence_resource) {
        this.example_sentence_resource = example_sentence_resource;
    }
}
