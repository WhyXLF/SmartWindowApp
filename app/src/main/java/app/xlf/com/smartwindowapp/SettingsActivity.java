package app.xlf.com.smartwindowapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    SeekBar pmBar,tmpBar,humBar;
    TextView pmNum,tmpNum,humNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        toolbar.setTitleTextColor(Color.WHITE);
        initView();

    }

    public void initView(){
        pmNum = (TextView) findViewById(R.id.pmNum);
        pmBar = (SeekBar) findViewById(R.id.pmBar);
        tmpBar= (SeekBar) findViewById(R.id.tmpBar);
        tmpNum= (TextView) findViewById(R.id.tmpNum);
        humBar= (SeekBar) findViewById(R.id.humBar);
        humNum= (TextView) findViewById(R.id.humNum);
        SharedPreferences sharedPreferences=getSharedPreferences("progress",Context.MODE_PRIVATE);
        pmBar.setProgress(sharedPreferences.getInt("pm_progress",0));
        pmNum.setText(sharedPreferences.getInt("pm_progress",0)+"");
        tmpBar.setProgress(sharedPreferences.getInt("tmp_progress",0));
        tmpNum.setText(sharedPreferences.getInt("tmp_progress",0)+"");
        humBar.setProgress(sharedPreferences.getInt("hum_progress",0));
        humNum.setText(sharedPreferences.getInt("hum_progress",0)+"");
        pmBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("pm progress", progress + "");
                Message msg = new Message();
                msg.what=1;
                Bundle b = new Bundle();
                b.putInt("progress", seekBar.getProgress());
                msg.setData(b);
                myHandler.handleMessage(msg);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("pm progress", seekBar.getProgress() + "");
                Message msg = new Message();
                msg.what=1;
                Bundle b = new Bundle();
                b.putInt("progress", seekBar.getProgress());
                msg.setData(b);
                myHandler.handleMessage(msg);
            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("pm progress", seekBar.getProgress() + "");
                Message msg = new Message();
                msg.what=1;
                Bundle b = new Bundle();
                b.putInt("progress", seekBar.getProgress());
                msg.setData(b);
                myHandler.handleMessage(msg);
            }
        });

        tmpBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("tmp progress", seekBar.getProgress() + "");
                Message msg = new Message();
                msg.what=2;
                Bundle b = new Bundle();
                b.putInt("progress", seekBar.getProgress());
                msg.setData(b);
                myHandler.handleMessage(msg);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("tmp progress", seekBar.getProgress() + "");
                Message msg = new Message();
                msg.what=2;
                Bundle b = new Bundle();
                b.putInt("progress", seekBar.getProgress());
                msg.setData(b);
                myHandler.handleMessage(msg);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("tmp progress", seekBar.getProgress() + "");
                Message msg = new Message();
                msg.what=2;
                Bundle b = new Bundle();
                b.putInt("progress", seekBar.getProgress());
                msg.setData(b);
                myHandler.handleMessage(msg);
            }
        });

        humBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("hum progress", seekBar.getProgress() + "");
                Message msg = new Message();
                msg.what=3;
                Bundle b = new Bundle();
                b.putInt("progress", seekBar.getProgress());
                msg.setData(b);
                myHandler.handleMessage(msg);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("tmp progress", seekBar.getProgress() + "");
                Message msg = new Message();
                msg.what=3;
                Bundle b = new Bundle();
                b.putInt("progress", seekBar.getProgress());
                msg.setData(b);
                myHandler.handleMessage(msg);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("tmp progress", seekBar.getProgress() + "");
                Message msg = new Message();
                msg.what=3;
                Bundle b = new Bundle();
                b.putInt("progress", seekBar.getProgress());
                msg.setData(b);
                myHandler.handleMessage(msg);
            }
        });
    }
    private Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    pmNum.setText(msg.getData().getInt("progress") + "");

                    SharedPreferences sharedPreferences=getSharedPreferences("progress", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putInt("pm_progress",msg.getData().getInt("progress"));
                    editor.commit();
                    break;
                case 2:
                    Log.d("test",msg.getData().getInt("progress")+"");
                    tmpNum.setText(msg.getData().getInt("progress") + "");

                    sharedPreferences=getSharedPreferences("progress", Context.MODE_PRIVATE);
                    editor= sharedPreferences.edit();
                    editor.putInt("tmp_progress", msg.getData().getInt("progress"));
                    editor.commit();
                    break;

                case 3:
                    humNum.setText(msg.getData().getInt("progress") + "");
                    sharedPreferences=getSharedPreferences("progress", Context.MODE_PRIVATE);
                    editor= sharedPreferences.edit();
                    editor.putInt("hum_progress", msg.getData().getInt("progress"));
                    editor.commit();
                    break;
            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
