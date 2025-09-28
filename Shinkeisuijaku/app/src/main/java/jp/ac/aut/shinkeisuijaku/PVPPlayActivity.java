package jp.ac.aut.shinkeisuijaku;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.EdgeToEdge;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.graphics.Insets;
        import androidx.core.view.ViewCompat;
        import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
        import java.util.ArrayList;

public class PVPPlayActivity extends AppCompatActivity {
    public static final String maxTimePlayActivity = "MAXTIMEPLAYACTIVITY";
    public static final String playerOfNumberPlayActivity = "PLAYEROFNUMBERPLAYACTIVITY";
    public static final String easyMode = "EASYMODE";
    public static final int defaultPlayerNum = 2;
    public static final int defaultTimeLimit = 300;
    public static final boolean defaultEasyMode = false;
    public static final int horizontal = 4;
    public static final int vertical = 5;
    public static final int numUpperLimit = 10;
    public static final int CardLimit = 20;
    public static final String colorChangeSuit = "club";
    private Card drawOne;//1枚目にめくられたカードのオブジェクト。
    private Card drawTwo;//2枚目にめくられたカードのオブジェクト。
    private Card[][] arrangedCards = new Card[4][5];//横4縦5。
    private List<Integer> numbers = new ArrayList<Integer>();
    private SoundPool soundPool;//サウンドプール。効果音に使用。
    private boolean tapWait = false;//タップ待ちかどうかのフラグ
    private boolean isEasyMode;
    private boolean pauseGame = false;
    private String cardTable;//どのカードか。
    private String cardImage;//画像の名前。
    private String[] suitArray= {"diamond","club"};//suitの値からスートを取得。ID取得に使う。
    private int soundResId;//カードをめくる音のid
    private int cardTableID;
    private int cardImageID;
    private int leftPairCount = 10;//残りの同じ数字のペアの数。20枚なので残り10ペアから始まる。
    private int suit,num;//カードのスートと数字。
    private int k = 0;//ナンバーズの要素番号。
    private int playerNum ;//プレイヤーの人数
    private int playerTurn = 0;
    private int limitTime;
    private Integer[] playerPoints;//各プレイヤーのカードの数
    private int howManyDraw = 0;//このターン何枚めくったか。
    private long timeTemp;//中断すると中断時の時間が代入される。
    private Intent intent;
    private MyCountDownTimer timer;
    private TextView textLeftTime;//残り時間を表示するテキストボックス
    private TextView textTurn;//誰のターンか表示するテキストボックス
    private TextView textPoints;//何枚持っているか表示するテキストボックス
    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        private long minute;//残り分
        private long second;//残り秒
        private long time;//中断した時点での時間
        @Override
        public void onTick(long milliUntilFinished) {
            this.minute = milliUntilFinished / 1000 / 60;
            this.second = milliUntilFinished / 1000 % 60;
            this.time = milliUntilFinished;
            textLeftTime.setText("残り:"+String.format("%1$02d分%2$02d秒",minute,second));
        }

        @Override
        public void onFinish() {
            textLeftTime.setText("残り:00分00秒");
            nextActivity();
        }
        public long getTime(){
            return this.time;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pvp_play);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        playerNum = pref.getInt(playerOfNumberPlayActivity,defaultPlayerNum);//プレイヤーの数。
        limitTime = pref.getInt(maxTimePlayActivity,defaultTimeLimit) ;//制限時間。
        isEasyMode = pref.getBoolean(easyMode,defaultEasyMode);//イージーモードかどうか
        playerPoints = new Integer[playerNum];//各プレイヤーのカードの数
        intent = new Intent(PVPPlayActivity.this, PVPScoreActivity.class);
        intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP);
        textLeftTime = (TextView) findViewById(R.id.progressTime);
        textTurn = (TextView) findViewById(R.id.Turn);
        textPoints = (TextView) findViewById(R.id.Points);
        timer = new MyCountDownTimer(limitTime*1000,100);
        timer.start();
        for(int i = 0;i < playerPoints.length;i++){
            playerPoints[i] = 0;
        }
        for(int i = 0;i < CardLimit;i++){
            numbers.add(i);
        }

        Collections.shuffle(numbers);
        for(int i=0;i<horizontal;i++){
            for(int j=0;j<vertical;j++){
                //numbersのk番目の商と余りからsuitとnumを割り当てる。
                suit = numbers.get(k) / numUpperLimit;
                num = (numbers.get(k) % numUpperLimit) + 1;
                //カードのIDはCard0_0からCard3_4にする。
                cardTable = "Card"+String.valueOf(j)+"_"+String.valueOf(i);
                if (num<10) {
                    cardImage = "card_" + suitArray[suit] + "_0" + String.valueOf(num);
                }else{
                    cardImage = "card_" + suitArray[suit] + "_" + String.valueOf(num);
                }
                //文字列からIDへ変換。
                cardTableID = getResources().getIdentifier(cardTable,"id",getPackageName());
                cardImageID = getResources().getIdentifier(cardImage,"drawable",getPackageName());
                //オブジェクトの初期化
                arrangedCards[i][j] = new Card(suitArray[suit],num,cardTableID,cardImageID);
                //クラブなら青色のカラーフィルタをかける。
                if(isEasyMode && arrangedCards[i][j].getSuit().equals(colorChangeSuit)){
                    ImageView imageView = (ImageView) findViewById(arrangedCards[i][j].getCardTableID());
                    imageView.setColorFilter(Color.argb(50,0,0,255));
                }
                k++;
            }
        }
        //終了ボタン
        findViewById(R.id.backTitle2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //以下カードをタップした時の処理
        for (int i = 0; i < horizontal; i++) {
            for (int j = 0; j < vertical; j++) {
                final int x = i;
                final int y = j;
                findViewById(arrangedCards[i][j].getCardTableID()).setOnClickListener(v -> DrawCard(x, y));
            }
        }
    }


    //スクリーンをタップした時の処理。
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {//ネットで調べた。ブール値が返ってくるが、使わない。
        if (pauseGame){
            leftTimerReStart();
        } else if(tapWait) {
            ScreenTap();
        }
        return false;
    }
    public void ScreenTap(){
        //IDから1枚目と2枚目のImageViewを取得。
        ImageView DrawOneImageView = (ImageView) findViewById(drawOne.getCardTableID());
        ImageView DrawTwoImageView = (ImageView) findViewById(drawTwo.getCardTableID());
        if (drawTwo.getNum() == drawOne.getNum()) {//カードの数字が一致
            leftPairCount--;//残りのペアの数を減らす。
            playerPoints[playerTurn] += 2;//取得したカードを増やす。
            //カードを消す。INVISIBLEは配置を変更しないため採用。
            DrawOneImageView.setVisibility(View.INVISIBLE);
            DrawTwoImageView.setVisibility(View.INVISIBLE);
            if(leftPairCount==0) {//残りペアが0ならスコア画面へ移行
                if(timer!=null) {
                    timer.cancel();
                    timer=null;
                }
                nextActivity();
            }
        } else {
            playerTurn = (playerTurn + 1) % playerNum;//次のプレイヤーのターンへ
            textTurn.setText("プレイヤー" + String.valueOf(playerTurn + 1) + "のターン");
            //カードを裏に戻す。
            DrawOneImageView.setImageResource(R.drawable.card_back);
            if(isEasyMode && drawOne.getSuit().equals(colorChangeSuit)){
                ImageView imageView = (ImageView) findViewById(drawOne.getCardTableID());
                imageView.setColorFilter(Color.argb(50,0,0,255));
            }
            DrawTwoImageView.setImageResource(R.drawable.card_back);
            if(isEasyMode && drawTwo.getSuit().equals(colorChangeSuit)){
                ImageView imageView = (ImageView) findViewById(drawTwo.getCardTableID());
                imageView.setColorFilter(Color.argb(50,0,0,255));
            }
            //1枚目と2枚目のカードが表向きかどうかのフラグをfalseに。
            drawOne.setAlreadyOpen(false);
            drawTwo.setAlreadyOpen(false);
        }
        textPoints.setText("カード:" + String.valueOf(playerPoints[playerTurn]) + "枚");//枚数を更新
        howManyDraw = 0;//裏に戻したのでめくられた枚数は0枚。
        tapWait = false;//タップ待ちのフラグをfalseへ
    }

    //トランプをめくった時の処理。
    public void DrawCard(int i,int j){
        if(pauseGame) {
            leftTimerReStart();
        }else{
            switch (howManyDraw) {
                case 0:
                    revealCard(i, j);
                    drawOne = arrangedCards[i][j];//そのターンの1番目にめくられたことを示す。
                    break;

                case 1:
                    if (!(arrangedCards[i][j].getAlreadyOpen())) {//同じ位置のカードをタップしても反応させない。
                        revealCard(i, j);
                        drawTwo = arrangedCards[i][j];//そのターンの2番目にめくられたことを指す。
                        tapWait = true;//タップするまで待つフラグを立てる。
                    }
                    break;

                default:
                    ScreenTap();
            }
        }
    }

    //カードをめくる処理
    public void revealCard(int i,int j){
        Card DrawnCard = arrangedCards[i][j];
        ImageView cardImageView = (ImageView) findViewById(DrawnCard.getCardTableID());
        soundPool.play(soundResId, 1, 1, 1, 0, 1.0f);
        cardImageView.setColorFilter(null);//表にするときフィルター解除
        cardImageView.setImageResource(DrawnCard.getCardImageID());//カードを表にする。
        DrawnCard.setAlreadyOpen(true);//このカードをめくられた事をtrueに。
        howManyDraw++;//めくられた枚数を1枚増やす。
    }

    //プレイヤーが再開した
    public void leftTimerReStart(){
        textTurn.setText("プレイヤー" + String.valueOf(playerTurn + 1) + "のターン");
        //中断された時の残り時間で再開
        timer = new MyCountDownTimer(timeTemp,100);
        timer.start();
        pauseGame = false;
    }

    //結果画面へ移行
    public void nextActivity(){
        int rankI=-1;
        //各プレイヤーの取得したカード枚数を降順で保存
        intent.putExtra("playerNum",playerNum);
        Integer[] playerPointsSort =  Arrays.copyOf(playerPoints,playerNum);
        Arrays.sort(playerPointsSort,Collections.reverseOrder());

        //プレイヤーiが登録されていないかどうかの配列生成
        boolean[] notRegistered = new boolean[playerNum];
        Arrays.fill(notRegistered,true);//まだプレイヤー全員の順位が登録されてないのでtrueで初期化。

        for(int i=0;i<playerNum;i++){
            for(int j=0;j<playerNum;j++){
                //登録されてないかつi+1位のプレイヤーがj番目のプレイヤーか確認。trueなら登録。
                if((playerPointsSort[i] == playerPoints[j])&&notRegistered[j]){
                    rankI=j;
                    notRegistered[j]=false;//i番目のプレイヤーが登録されたのでfalse。
                    break;
                }
            }
            intent.putExtra("playerCardNum"+String.valueOf(i),playerPointsSort[i]);
            intent.putExtra("RankInfo"+String.valueOf(i),String.format("プレイヤー%1$1d[%2$02d枚]",rankI+1,playerPointsSort[i]));
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        } else {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attr)
                    .setMaxStreams(2)
                    .build();
        }
        soundResId = soundPool.load(this,R.raw.card_draw1,1);//サウンドプールにめくる音を設定
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(timer!=null) {
            timeTemp = timer.getTime();
            timer.cancel();
            timer=null;
        }
        soundPool.release();//アプリを閉じているときは効果音中断
        textTurn.setText("プレイヤー" + String.valueOf(playerTurn + 1) + ":中断中");
        pauseGame = true;
    }
}