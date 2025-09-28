package jp.ac.aut.shinkeisuijaku;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
    private int soundResId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //対人戦モードボタン
        findViewById(R.id.pvpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundResId, 1.0f, 1.0f, 0, 0, 1.0f);
                Intent intent = new Intent(MainActivity.this, PVPPlayActivity.class);
                startActivity(intent);
            }
        });

        //タイムアタックモードボタン
        findViewById(R.id.TAButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundResId,1.0f,1.0f,0,0,1.0f);

                Intent intent = new Intent(MainActivity.this, TAPlayActivity.class);
                startActivity(intent);
            }
        });

        //最短記録確認ボタン
        findViewById(R.id.ShowTimeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundResId,1.0f,1.0f,0,0,1.0f);
                Intent intent = new Intent(MainActivity.this, TimeRankingActivity.class);
                startActivity(intent);
            }
        });

        //設定画面ボタン
        findViewById(R.id.Setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        //終了ボタン
        findViewById(R.id.Stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {moveTaskToBack(true);}
        });

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
        soundResId = soundPool.load(this,R.raw.card_shuffle2,1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundPool.release();
    }
}