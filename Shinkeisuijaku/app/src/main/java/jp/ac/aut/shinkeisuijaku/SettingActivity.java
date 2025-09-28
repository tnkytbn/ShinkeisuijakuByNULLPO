package jp.ac.aut.shinkeisuijaku;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingActivity extends AppCompatActivity {
    public static final String maxTime = "MAXTIME";
    public static final String playerOfNumber = "PLAYEROFNUMBER";
    public static final String easyMode = "EASYMODE";
    public static final String maxTimePlayActivity = "MAXTIMEPLAYACTIVITY";
    public static final String playerOfNumberPlayActivity = "PLAYEROFNUMBERPLAYACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBarTime);
        Spinner spinner = (Spinner)findViewById(R.id.spinnerPlayNum);
        CheckBox easyModeCheckBox = (CheckBox)findViewById(R.id.checkBoxEasy);
        int maxTimeValue = pref.getInt(maxTime,270);
        int playerOfNumberValue = pref.getInt(playerOfNumber,1);
        boolean easyModeValue = pref.getBoolean(easyMode,false);

        //スピナーの設定
        spinner.setSelection(playerOfNumberValue);

        // シークバーの設定
        seekBar.setProgress(maxTimeValue);
        setMaxTimeText(seekBar.getProgress());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean frowUser) {
                setMaxTimeText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //スイッチの設定
        easyModeCheckBox.setChecked(easyModeValue);

        //リセットボタン
        findViewById(R.id.Resetbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setProgress(270);
                setMaxTimeText(seekBar.getProgress());
                easyModeCheckBox.setChecked(false);
                spinner.setSelection(1);
            }
        });

        //戻るボタン
        findViewById(R.id.backTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //保存ボタン
        findViewById(R.id.SaveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(maxTime,seekBar.getProgress());
                editor.putInt(maxTimePlayActivity,seekBar.getProgress()+30);
                editor.putInt(playerOfNumber,spinner.getSelectedItemPosition());
                editor.putInt(playerOfNumberPlayActivity,Integer.valueOf(spinner.getSelectedItem().toString()));
                editor.putBoolean(easyMode,easyModeCheckBox.isChecked());
                editor.apply();
                finish();
            }
        });
    }

    public void setMaxTimeText(int progress){
        TextView text_Max_time =(TextView)findViewById(R.id.textMaxTime);
        int maxTime;
        int minute;
        int second;
        //シークバーから時間を受け取り最小値を30に調整
        maxTime = progress + 30;
        minute = maxTime / 60;//最大時間の分
        second = maxTime % 60;//最大時間の秒
        text_Max_time.setText(String.format("%02d分%2$02d秒",minute,second));
    }
}