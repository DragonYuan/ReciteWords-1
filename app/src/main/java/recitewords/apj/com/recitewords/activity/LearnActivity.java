package recitewords.apj.com.recitewords.activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.bean.WordStudy;
import recitewords.apj.com.recitewords.db.dao.BookDao;
import recitewords.apj.com.recitewords.fragment.ExampleSentenceFragment;
import recitewords.apj.com.recitewords.util.LearnWordsUtil;
import recitewords.apj.com.recitewords.util.MediaUtils;
import recitewords.apj.com.recitewords.util.UIUtil;
import recitewords.apj.com.recitewords.view.SlidingUpMenu;

/**
 * Created by CGT and CJZ on 2016/11/31.
 * <p/>
 * 学习界面的Activity
 */

public class LearnActivity extends BaseActivity implements View.OnClickListener,
        ViewTreeObserver.OnGlobalLayoutListener, SlidingUpMenu.OnToggleListener, TextWatcher {

    //定义好的8张背景图id数组
    private int[] images = new int[]{R.mipmap.haixin_bg_dim_01, R.mipmap.haixin_bg_dim_02,
            R.mipmap.haixin_bg_dim_03, R.mipmap.haixin_bg_dim_04,
            R.mipmap.haixin_bg_dim_05, R.mipmap.haixin_bg_dim_06};


    public static class ViewHolder {
        public LinearLayout ll_choice;  //选择题模式--中间按钮--不认识
        public LinearLayout ll_incognizance;   //不认识--中间按钮
        public LinearLayout ll_memory;   //回忆模式--中间按钮

        public LinearLayout ll_abcd;   //选择题模式--显示ABCD选项
        public LinearLayout ll_information;   //不认识-，回忆模式-显示词性词义
        public ProgressBar pb_loading;   //回忆模式加载中

        public TextView tv_incognizance_next;  //不认识--下一个
        public TextView tv_incognizance_example;  //不认识--看例句
        public TextView tv_memory_cognize;  //回忆模式--认识
        public TextView tv_memory_incognizance;  //回忆模式--不认识
        public RelativeLayout rl_learn;   //学习的根布局
        public FrameLayout fl_example;  //例句的根布局
        public LinearLayout ll_show_word;  //顶部显示的单词，音标和学习情况根布局
        public TextView tv_back;  //底部返回按钮
        public TextView tv_spell;  //底部拼写按钮
        public TextView tv_delete;  //底部删除按钮
        public SlidingUpMenu learn_sliding;  //上下滑动控件

        public TextView spell_tv_close;  //拼写关闭
        public RelativeLayout spell_rl_root;  //拼写根布局
        public EditText spell_et_input;  //拼写用户输入
        public TextView spell_tv_correct;  //拼写显示正确单词
        public TextView spell_tv_confirm;  //拼写确认单词
        public TextView spell_tv_prompt;  //拼写单词提示
        public RelativeLayout spell_rl_back;  //评写界面黑色背景
        public RelativeLayout spell_rl_bottom;  //评写界面底部背景

        public TextView learn_tv_word;           //要学习的单词
        public TextView learn_tv_soundMark;     // 音标
        public TextView tv_A, tv_B, tv_C, tv_D; //A B C D 选项
        public TextView tv_word_information;    //单词含义答案

        public ImageView asterisk_one, asterisk_two, asterisk_there, asterisk_four; // 4个星号

        public TextView completed_words,no_completed_words;    //已学习的单词数量，剩余单词数量
    }

    private ViewHolder holder;
    private Handler mHandler = new Handler();
    private final String FRAGMENT_SENTENCE = "fragment_sentence";
    private int backgroundNum;  //背景图片序号
    private OnToggleListner mOnToggleListner;  //监听例句显示状态
    private AlertDialog alertDialog;  //评写界面弹窗
    private boolean havaing_comfirm = false;  //拼写是否已经和正确单词比较,默认为false

    private int order = 0; // 定义一个全局变量，用于显示要学习的第几个单词 刚开始是0

    private ArrayList<String> options = new ArrayList<>(); //存放ABCD四个选项的集合

    private ArrayList<WordStudy> studyWords = new ArrayList<>();// 存放20个单词的内容，包括：单词，美式音标，英式音标，单词含义的集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        holder = new ViewHolder();
        holder.rl_learn = findViewByIds(R.id.rl_learn);
        holder.fl_example = findViewByIds(R.id.fl_example);
        holder.ll_choice = findViewByIds(R.id.ll_choice);
        holder.ll_incognizance = findViewByIds(R.id.ll_incognizance);
        holder.ll_memory = findViewByIds(R.id.ll_memory);
        holder.ll_abcd = findViewByIds(R.id.ll_abcd);
        holder.ll_information = findViewByIds(R.id.ll_information);
        holder.pb_loading = findViewByIds(R.id.pb_loading);
        holder.tv_word_information = findViewByIds(R.id.tv_word_information);
        holder.ll_show_word = findViewByIds(R.id.ll_show_word);
        holder.learn_sliding = findViewByIds(R.id.learn_sliding);
        holder.tv_back = findViewByIds(R.id.tv_back);
        holder.tv_spell = findViewByIds(R.id.tv_spell);
        holder.tv_delete = findViewByIds(R.id.tv_delete);

        holder.tv_incognizance_next = findViewByIds(R.id.tv_incognizance_next);

    }

    private void initData() {
        Intent intent = getIntent();
        backgroundNum = intent.getIntExtra("backgroundNum", 0);
        //设置学习界面的背景图片与主页面的背景图片动态一致
        holder.rl_learn.setBackgroundResource(images[backgroundNum]);

        holder.learn_sliding.getBackground().setAlpha(170);  //更改学习界面透明度
        holder.fl_example.getBackground().setAlpha(70);  //更改例句界面透明度

        //用Fragment替换帧布局来显示例句
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_example, new ExampleSentenceFragment("abroad"), FRAGMENT_SENTENCE);
        transaction.commit();

        studyWords = LearnWordsUtil.getWords(this);//初始化20个单词数据

        showWordsInformation(order);        //显示 要单词各个信息
        showWordOption(order);              //显示A B C D 选项 （随机）
    }

    private void initEvent() {
        //监听不认识点击事件
        holder.ll_choice.setOnClickListener(this);
        // 监听“下一个” 的点击事件
        holder.tv_incognizance_next.setOnClickListener(this);
        //监听视图树获取高度
        holder.ll_show_word.getViewTreeObserver().addOnGlobalLayoutListener(this);
        //监听底部返回按钮
        holder.tv_back.setOnClickListener(this);
        //监听底部拼写按钮
        holder.tv_spell.setOnClickListener(this);
        //监听底部删除单词按钮
        holder.tv_delete.setOnClickListener(this);
        //监听例句是否显示
        holder.learn_sliding.setOnToggleListener(this);

        holder.learn_tv_word = (TextView) holder.ll_show_word.findViewById(R.id.learn_tv_word);//获取要学习单词的控件
        holder.learn_tv_soundMark = (TextView) holder.ll_show_word.findViewById(R.id.learn_tv_soundmark);//获取音标控件

        holder.tv_A = (TextView) holder.ll_abcd.findViewById(R.id.tv_A); //获取选项A控件
        holder.tv_B = (TextView) holder.ll_abcd.findViewById(R.id.tv_B);//获取选项B控件
        holder.tv_C = (TextView) holder.ll_abcd.findViewById(R.id.tv_C);//获取选项C控件
        holder.tv_D = (TextView) holder.ll_abcd.findViewById(R.id.tv_D);//获取选项D控件

        holder.tv_A.setOnClickListener(this);//监听A选项
        holder.tv_B.setOnClickListener(this);//监听B选项
        holder.tv_C.setOnClickListener(this);//监听C选项
        holder.tv_D.setOnClickListener(this);//监听D选项

        holder.asterisk_one = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_one);//获取星号1控件
        holder.asterisk_two = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_two);//获取星号2控件
        holder.asterisk_there = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_there);//获取星号3控件
        holder.asterisk_four = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_four);//获取星号4控件

    }

    //点击事件回调方法
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_choice:                    //点击不认识
                incognizance();                      //显示该单词的含义模式
                showWordAnswer(order);              //显示该单词词义
                break;
            case R.id.tv_incognizance_next:       //点击下一个
                order++;                            //单词下标加1
                choice();                            //显示选择题模式
                showWordsInformation(order);        //显示单词信息
                showWordOption(order);              //显示ABCD选项
                break;
            case R.id.tv_memory_cognize:          //点击记忆模式的认识

                break;
            case R.id.tv_memory_incognizance:   //点击记忆模式的不认识

                break;
            case R.id.tv_A:                                 //点击A选项
                String A_option = holder.tv_A.getText().toString().trim();//获取A选项词义
                Log.e("用户选择的是",""+A_option);
                if (check(A_option)){                       //判断是否选择正确
                    order++;                                //判断正确，单词下标加1，显示下一个单词
                    choice();                               // 显示选择题模式
                    showWordsInformation(order);           //显示单词信息
                    showWordOption(order);                 //显示ABCD选项
                    //updateWordAsterisk(order);            //更新数据库 表中字段
                }else {
                    incognizance();                         //用户选择错误，显示单词词义
                    showWordAnswer(order);                  //显示单词答案
                }
                break;
            case R.id.tv_B:                                //点击B选项
                String B_option = holder.tv_B.getText().toString().trim();//获取B选项词义
                Log.e("用户选择的是",""+B_option);
                if (check(B_option)){                        //判断是否选择正确
                    order++;                                //判断正确，单词下标加1，显示下一个单词
                    choice();                               // 显示选择题模式
                    showWordsInformation(order);            // 显示单词信息
                    showWordOption(order);                 //显示ABCD选项
                    //updateWordAsterisk(order);            //更新数据库 表中字段
                }else {
                    incognizance();                         //用户选择错误，显示单词词义
                    showWordAnswer(order);                  //显示单词答案
                }
                break;
            case R.id.tv_C:                                //点击B选项
                String C_option = holder.tv_C.getText().toString().trim();//获取B选项词义
                Log.e("用户选择的是",""+C_option);
                if (check(C_option)){                         //判断是否选择正确
                    order++;                                 //判断正确，单词下标加1，显示下一个单词
                    choice();                                // 显示选择题模式
                    showWordsInformation(order);            // 显示单词信息
                    showWordOption(order);                   //显示ABCD选项
                    //updateWordAsterisk(order);             //更新数据库 表中字段
                }else {
                    incognizance();                         //用户选择错误，显示单词词义
                    showWordAnswer(order);                  //显示单词答案
                }
                break;
            case R.id.tv_D:                                //点击B选项
                String D_option = holder.tv_D.getText().toString().trim();//获取B选项词义
                Log.e("用户选择的是",""+D_option);
                if (check(D_option)){                        //判断是否选择正确
                    order++;                                //判断正确，单词下标加1，显示下一个单词
                    choice();                               // 显示选择题模式
                    showWordsInformation(order);             // 显示单词信息
                    showWordOption(order);                   //显示ABCD选项
                    //updateWordAsterisk(order);             //更新数据库 表中字段
                }else {
                    incognizance();                         //用户选择错误，显示单词词义
                    showWordAnswer(order);                  //显示单词答案
                }
                break;
            case R.id.tv_back:
                //保存数据，显示progressbar,返回主页面
//                Intent intent = new Intent(LearnActivity.this, MainActivity.class);
//                intent.putExtra("backgroundNum",backgroundNum);
//                startActivity(intent);
                AlertDialog.Builder mLoadingBuilder=new AlertDialog.Builder(this);
                AlertDialog mLoadingAlertDialog = mLoadingBuilder.create();
                View mLoadingView = getLayoutInflater().inflate(R.layout.dialog_save_loading, null);
                mLoadingAlertDialog.setView(mLoadingView);
                mLoadingAlertDialog.show();
                // finish();
                break;
            case R.id.tv_spell:
                //打开拼写界面
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog_Fullscreen);

                this.alertDialog = builder.create();
                View view = getLayoutInflater().inflate(R.layout.dialog_spell, null);
                this.alertDialog.setView(view, 0, 0, 0, 0);
                holder.spell_tv_close = UIUtil.findViewByIds(view, R.id.spell_tv_close);
                holder.spell_rl_root = UIUtil.findViewByIds(view, R.id.spell_rl_root);
                holder.spell_et_input = UIUtil.findViewByIds(view, R.id.spell_et_input);
                holder.spell_tv_correct = UIUtil.findViewByIds(view, R.id.spell_tv_correct);
                holder.spell_tv_confirm = UIUtil.findViewByIds(view, R.id.spell_tv_confirm);
                holder.spell_tv_prompt = UIUtil.findViewByIds(view, R.id.spell_tv_prompt);
                holder.spell_rl_back = UIUtil.findViewByIds(view, R.id.spell_rl_back);
                holder.spell_rl_bottom = UIUtil.findViewByIds(view, R.id.spell_rl_bottom);

                holder.spell_rl_root.setBackgroundResource(images[backgroundNum]);
                //设置黑色背景透明度，模糊化背景图片
//                holder.spell_rl_back.getBackground().setAlpha(210);
                //底部暗灰色导航条，使背景模糊化
                holder.spell_rl_bottom.getBackground().setAlpha(170);

                holder.spell_tv_close.setOnClickListener(this);  //监听拼写关闭按钮
                holder.spell_tv_prompt.setOnClickListener(this);  //监听评写提示按钮
                holder.spell_tv_confirm.setOnClickListener(this);  //监听评写确认按钮
                holder.spell_et_input.addTextChangedListener(this);  //监听文本框内容变化
                this.alertDialog.show();
                break;
            case R.id.tv_delete:
                //从学习单词表中删除单词，表示已经掌握
                Toast.makeText(LearnActivity.this, "点击删除按钮", Toast.LENGTH_LONG).show();
                break;
            case R.id.spell_tv_close:
                //关闭拼写界面
                this.alertDialog.dismiss();
                break;
            case R.id.spell_tv_prompt:
                //提示用户的单词信息，显示音标和读音
                MediaUtils.playWord(this, "abandon");  //播放单词
                //获取底部导航栏高度，让土司显示在中间
                int height = holder.spell_rl_bottom.getHeight();
                int time=3000;  //提示时间3秒
                //自定义土司，设置土司显示位置
                UIUtil.toast(this,studyWords.get(order).getSoundmark_american(),time, Gravity.BOTTOM,0,height/2-20);
                //改变背景图片
                holder.spell_tv_prompt.setBackgroundResource(R.mipmap.ic_spell_prompt_highlight);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.spell_tv_prompt.setBackgroundResource(R.mipmap.ic_spell_prompt);
                    }
                },time);

                break;
            case R.id.spell_tv_confirm:
                //比较用户输入单词和正确单词
                String word = holder.spell_et_input.getText().toString();  //获取用户输入的单词
                if (word.equals(LearnWordsUtil.getWords(this).get(order).getWord())) {
                    //改变文字颜色--淡黄
                    holder.spell_et_input.setTextColor(Color.parseColor("#D1F57F"));
                    MediaUtils.playWord(this, "abandon.mp3");  //播放单词
                } else {
                    //改变文字颜色--红色
                    holder.spell_et_input.setTextColor(Color.parseColor("#FF4444"));
                    MediaUtils.playWord(this,"abandon.mp3");  //播放单词
                    holder.spell_tv_correct.setText(LearnWordsUtil.getWords(this).get(order).getWord());  //显示正确的单词
                    holder.spell_tv_correct.setVisibility(View.VISIBLE);
                }
                havaing_comfirm = true;  //改变是否比较状态
                break;
            default:
                break;
        }
    }

    /**
     * 显示 单词 各个信息
     * 包括：单词，音标，星号，已学习单词，剩余学习单词
     * */
    private void showWordsInformation(int order){
        holder.learn_tv_word = (TextView) holder.ll_show_word.findViewById(R.id.learn_tv_word);//获取要学习单词的控件
        holder.learn_tv_soundMark = (TextView) holder.ll_show_word.findViewById(R.id.learn_tv_soundmark);//获取音标控件

        holder.learn_tv_word.setText(studyWords.get(order).getWord());                      //显示要学习的单词
        holder.learn_tv_soundMark.setText(studyWords.get(order).getSoundmark_american());  //显示单词音标
        showAsterisk(getAsterisk(order));                                                      //显示单词的星号
        showFinishWords();                                                                     //显示已学习单词，剩余学习单词
    }

    /**
     * 获取单词星号数量
     * */
    private int getAsterisk(int order){
        return studyWords.get(order).getAsterisk();
    }

    /**
     * 显示单词星号
     * */
    private void showAsterisk(int order){
        holder.asterisk_one = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_one);//获取星号1控件
        holder.asterisk_two = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_two);//获取星号2控件
        holder.asterisk_there = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_there);//获取星号3控件
        holder.asterisk_four = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_four);//获取星号4控件
        switch (order){
            case 1:
                holder.asterisk_one.setImageResource(R.mipmap.star_on);
                break;
            case 2:
                holder.asterisk_one.setImageResource(R.mipmap.star_on);
                holder.asterisk_two.setImageResource(R.mipmap.star_on);
                break;
            case 3:
                holder.asterisk_one.setImageResource(R.mipmap.star_on);
                holder.asterisk_two.setImageResource(R.mipmap.star_on);
                holder.asterisk_there.setImageResource(R.mipmap.star_on);
                break;
            case 4:
                holder.asterisk_one.setImageResource(R.mipmap.star_on);
                holder.asterisk_two.setImageResource(R.mipmap.star_on);
                holder.asterisk_there.setImageResource(R.mipmap.star_on);
                holder.asterisk_four.setImageResource(R.mipmap.star_on);
                break;
            default:
                break;
        }
    }

    /**
     * 显示已学习单词，剩余学习单词
     * 用for循环判断单词的星号是否为4个
     * 如果为4，左上角就加上1
     * 右上角就减1
     * （同时，单词的星号为4，就修改Book的 word_is_study 字段变成1）
     * */
    private void showFinishWords(){
        holder.completed_words = (TextView)holder.ll_show_word.findViewById(R.id.tv_complete);
        holder.no_completed_words = (TextView)holder.ll_show_word.findViewById(R.id.tv_need_complete);
        int completedNum = 0;
        for (int i=0;i<studyWords.size();i++){
            if (studyWords.get(i).getAsterisk() == 4){
                setWord_is_study(studyWords.get(i).getWord());//修改Book里面的 word_is_study 字段
                completedNum++;
            }
        }
        holder.completed_words.setText(completedNum+"");
        holder.no_completed_words.setText((20-completedNum)+"");
    }

    /**
     * 如果单词的星号字段等于4
     * 就修改Book里该单词的 word_is_study 字段变成 1
     * 表示已学习
     * */
    private void setWord_is_study(String word){
        BookDao bookDao = new BookDao(this);
        bookDao.updateWord_is_study(word);
    }

    /**
     * 显示 ABCD 四个选项（随机的）
     * */
    private void showWordOption(int order){
        options = LearnWordsUtil.getWordsOption(this,studyWords.get(order).getWord());//获取20个单词的随机答案
        holder.tv_A = (TextView) holder.ll_abcd.findViewById(R.id.tv_A); //获取选项A控件
        holder.tv_B = (TextView) holder.ll_abcd.findViewById(R.id.tv_B);//获取选项B控件
        holder.tv_C = (TextView) holder.ll_abcd.findViewById(R.id.tv_C);//获取选项C控件
        holder.tv_D = (TextView) holder.ll_abcd.findViewById(R.id.tv_D);//获取选项D控件

        holder.tv_A.setText(options.get(0));    //显示A选项
        holder.tv_B.setText(options.get(1));    //显示B选项
        holder.tv_C.setText(options.get(2));    //显示C选项
        holder.tv_D.setText(options.get(3));    //显示D选项

        Log.e("当前单词是：",""+studyWords.get(order).getWord());
        for(int i=0;i<options.size();i++){
            Log.e("获取到的4个ABCD选项：",""+options.get(i));
        }
    }

    /**
     * 判断用户所选的答案是否正确
     * */
    private boolean check(String word){
        String answer = studyWords.get(order).getAnswer_right();
        Log.e("当前单词答案：",""+answer);
        if(word.equals(answer)){
            Log.e("判断","是正确的");
            return true;
        }
        Log.e("判断","是错误的");
        return false;
    }

    /**
     * 显示单词的答案
     * */
    private void showWordAnswer(int order) {
        holder.tv_word_information = (TextView) holder.ll_information.findViewById(R.id.tv_word_information);
        holder.tv_word_information.setText(studyWords.get(order).getAnswer_right());
    }

    //视图树的回调方法
    @Override
    public void onGlobalLayout() {
        int height = holder.ll_show_word.getHeight();  //和例句一样的高度
        holder.fl_example.setMinimumHeight(height);  //设置例句高度
        holder.ll_show_word.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    //监听例句是否显示
    @Override
    public void onToggleChange(SlidingUpMenu view, boolean isOpen) {
        if (mOnToggleListner != null) {
            mOnToggleListner.onToggleChange(view, isOpen);
        }
    }
    String input="";
    //文本框变化前
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        input=s.toString();  //内容默认为
    }

    //文本框变化中
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (havaing_comfirm) {
            input = s.toString().substring(start, (start + count));  //获取用户重新输入的值
        } else {
            input=s.toString();
        }
    }

    //文本框变化后
    @Override
    public void afterTextChanged(Editable s) {
        if (havaing_comfirm){
            havaing_comfirm=false;
            //隐藏正确单词
            holder.spell_tv_correct.setVisibility(View.INVISIBLE);
            //用户在比较单词后会改变输入文字颜色，在此处确保用户再次输入的文字为白色
            int color = Color.parseColor("#FFFFFF");
            holder.spell_et_input.setTextColor(color);
            holder.spell_et_input.setText(input);
            holder.spell_et_input.setSelection(input.length());  //设置光标位置
        }

    }

    /**
     * 监听例句是否显示
     * @param listener  监听者
     */
    public void setOnToggleListener(OnToggleListner listener) {
        this.mOnToggleListner = listener;
    }

    public interface OnToggleListner {
        void onToggleChange(SlidingUpMenu view, boolean isOpen);
    }

    /**
     * 显示 不认识，和 ABCD选项
     * */
    private void choice(){
        holder.ll_choice.setVisibility(View.VISIBLE);
        holder.ll_abcd.setVisibility(View.VISIBLE);

        holder.ll_incognizance.setVisibility(View.INVISIBLE);
        holder.ll_information.setVisibility(View.INVISIBLE);

        holder.ll_memory.setVisibility(View.INVISIBLE);
    }
    /**
     * 显示下一个，看例句
     * 单词含义
     * */
    private void incognizance(){
        holder.ll_choice.setVisibility(View.INVISIBLE);
        holder.ll_abcd.setVisibility(View.INVISIBLE);

        holder.ll_incognizance.setVisibility(View.VISIBLE);//显示下一个，看例句
        holder.ll_information.setVisibility(View.VISIBLE);//显示单词答案

        holder.ll_memory.setVisibility(View.INVISIBLE);
    }
    /**
     * 显示认识，不认识
     * 转圈
     * 单词词义
     * */
    private void memory(){
        holder.ll_choice.setVisibility(View.INVISIBLE);
        holder.ll_abcd.setVisibility(View.INVISIBLE);

        holder.ll_incognizance.setVisibility(View.INVISIBLE);

        holder.ll_memory.setVisibility(View.VISIBLE);
        holder.ll_information.setVisibility(View.VISIBLE);
        holder.pb_loading = (ProgressBar)holder.ll_information.findViewById(R.id.pb_loading);
        holder.pb_loading.setVisibility(View.VISIBLE);
    }



}
