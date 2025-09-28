package jp.ac.aut.shinkeisuijaku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PVPScoreActivity extends AppCompatActivity {
    public static final int noUseDefaultValue=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pvp_score);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        int compareCardNumFormer = intent.getIntExtra("playerCardNum0",noUseDefaultValue);
        int playerNum = intent.getIntExtra("playerNum",noUseDefaultValue);
        int rank = 1;
        for(int i=0;i<playerNum;i++){
            int textViewID = getResources().getIdentifier("Rank"+String.valueOf(i+1)+"View","id",getPackageName());
            int compareCardNumLatter = intent.getIntExtra("playerCardNum"+String.valueOf(i),noUseDefaultValue);;
            TextView textView = (TextView) findViewById(textViewID);
            if(compareCardNumFormer!=compareCardNumLatter){
                rank = i + 1;
            }

            textView.setText(String.valueOf(rank)+"位:"+intent.getStringExtra("RankInfo"+String.valueOf(i)));
            compareCardNumFormer = compareCardNumLatter;
        }


        //戻るボタン
        findViewById(R.id.backTitle3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}