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
import java.util.Collections;

public class TimeRankingActivity extends AppCompatActivity {
    public static final int lowLimitRank = 5;
    public static final int noExist = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_time_ranking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //プリファレンス
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String modeKey = "Normal";
        //ランキングを表示
        showRanking(modeKey);
        // 切り替えボタン
        findViewById(R.id.buttonModeChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) findViewById(R.id.modeText);
                String modeKey2;
                if(textView.getText().equals("NormalMode")){
                    modeKey2 = "Easy";
                }else{
                    modeKey2 = "Normal";
                }
                Log.d("ModeKey",modeKey2);

                showRanking(modeKey2);
            }
        });
        //戻るボタン
        findViewById(R.id.backTitle3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void showRanking(String modeKey){
        //プリファレンス
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        TextView textView = (TextView) findViewById(R.id.modeText);
        int rankLen;//登録されたランキングデータの個数
        textView.setText(modeKey+"Mode");
        ArrayList<Integer> bestTime = new ArrayList<Integer>();//ランキングの配列

        for(int i=0;i<lowLimitRank;i++){
            int timeTemp = pref.getInt("Rank" + modeKey + String.valueOf(i),noExist);
            if(timeTemp==noExist){
                break;
            }else{
                bestTime.add(timeTemp);
            }
        }
        rankLen=bestTime.size();
        Collections.sort(bestTime);
        //表示をリセット
        if(rankLen<5) {
            for (int i = 0; i < lowLimitRank; i++) {
                int rankID = getResources().getIdentifier("Rank" + String.valueOf(i + 1) + "View", "id", getPackageName());
                TextView textViewRank = (TextView) findViewById(rankID);
                textViewRank.setText(String.format("%1$1d位:--分--秒", i + 1));
            }
        }

        //テキストビューに表示
        int minute;
        int second;
        int rankI=0;
        int compareTimeFormer = -1;
        int bestTimeI = -1;
        for(int i=0;i < rankLen;i++) {
            bestTimeI = bestTime.get(i);
            if (compareTimeFormer != bestTimeI) {
                rankI = i;
                compareTimeFormer = bestTimeI;
            }
            minute = bestTimeI / 60;
            second = bestTimeI % 60;
            //i位のテキストビュー
            int rankID = getResources().getIdentifier("Rank" + String.valueOf(i + 1) + "View", "id", getPackageName());
            TextView textViewRank = (TextView) findViewById(rankID);
            textViewRank.setText(String.format("%1$1d位:%2$02d分%3$02d秒", rankI + 1, minute, second));
        }
    }
}