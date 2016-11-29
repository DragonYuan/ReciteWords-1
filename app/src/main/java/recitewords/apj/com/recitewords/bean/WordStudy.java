package recitewords.apj.com.recitewords.bean;

/**
 * Created by CGT on 2016/11/28.
 *
 * 单词学习实体类
 */
public class WordStudy {

    private String word;  //单词
    private String option_A;  //A选项
    private String option_B;  //B选项
    private String option_C;  //C选项
    private String option_D;  //D选项
    private String answer_right;  //正确答案
    private String answer_user;  //用户答案
    private int asterisk;  //记住次数（*号）
    private String date;  //记住日期
    private String book_name;  //词书名字
    private int userID;  //用户ID

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getOption_A() {
        return option_A;
    }

    public void setOption_A(String option_A) {
        this.option_A = option_A;
    }

    public String getOption_B() {
        return option_B;
    }

    public void setOption_B(String option_B) {
        this.option_B = option_B;
    }

    public String getOption_C() {
        return option_C;
    }

    public void setOption_C(String option_C) {
        this.option_C = option_C;
    }

    public String getOption_D() {
        return option_D;
    }

    public void setOption_D(String option_D) {
        this.option_D = option_D;
    }

    public String getAnswer_right() {
        return answer_right;
    }

    public void setAnswer_right(String answer_right) {
        this.answer_right = answer_right;
    }

    public String getAnswer_user() {
        return answer_user;
    }

    public void setAnswer_user(String answer_user) {
        this.answer_user = answer_user;
    }

    public int getAsterisk() {
        return asterisk;
    }

    public void setAsterisk(int asterisk) {
        this.asterisk = asterisk;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
