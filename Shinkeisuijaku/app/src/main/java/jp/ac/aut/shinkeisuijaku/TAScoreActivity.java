package jp.ac.aut.shinkeisuijaku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TAScoreActivity extends AppCompatActivity {
    public static int noExist = -1;
    public static final int lowLimitRank = 5;
    public static final int worstRank = 4;
    public static String easyMode = "EASYMODE";
    public static boolean defaultEasyMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ta_score);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //タイムを表示
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        Intent intent = getIntent();
        ArrayList<Integer>  bestTime = new ArrayList<Integer>();//ランキングの配列
        boolean isEasyMode = pref.getBoolean(easyMode,defaultEasyMode);
        String timeString = intent.getStringExtra("timeString");
        TextView textView = (TextView)findViewById(R.id.ClearTimeView);
        textView.setText("タイム:"+ timeString);
        String modeKey;
        if(isEasyMode){
            modeKey = "Easy";
        }else{
            modeKey = "Normal";
        }
        int rankLen;//登録されたランキングデータの個数
        int timeInt = Integer.valueOf(timeString.substring(0,2))*60+Integer.valueOf(timeString.substring(3,5));//今回の記録を整数に
        //ランキング配列作成
        for(int i=0;i<lowLimitRank;i++){
            int rankTemp = pref.getInt("Rank" + modeKey + String.valueOf(i),noExist);
            if(rankTemp==noExist){
                break;
            }else{
                bestTime.add(rankTemp);
            }
        }

        rankLen=  bestTime.size();
        //記録が5つ未満なら追加
        if(rankLen<lowLimitRank){
            bestTime.add(timeInt);
            rankLen++;
        }else if(timeInt <  bestTime.get(worstRank)){
            bestTime.set(worstRank,timeInt);//5位より速いなら5位に追加
        }
        Collections.sort(bestTime);

        //テキストビューに表示
        int minute;
        int second;
        int rankI=0;
        int compareTimeFormer = bestTime.get(0);
        int bestTimeI;
        for(int i=0;i<rankLen;i++){
            bestTimeI = bestTime.get(i);
            if(compareTimeFormer!= bestTimeI){
                rankI=i;
                compareTimeFormer = bestTimeI;
            }
            minute = bestTimeI / 60;
            second = bestTimeI % 60;
            //i位のテキストビュー
            int rankID = getResources().getIdentifier("Rank"+ String.valueOf(i+1) + "View","id",getPackageName());
            TextView textViewRank=(TextView)findViewById(rankID);
            textViewRank.setText(String.format("%1$1d位:%2$02d分%3$02d秒",rankI+1,minute,second) );
        }

        for (int i=0;i<rankLen;i++){
            editor.putInt("Rank"+ modeKey + String.valueOf(i), bestTime.get(i));
        }
        editor.apply();
        //戻るボタン
        findViewById(R.id.backTitle3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}