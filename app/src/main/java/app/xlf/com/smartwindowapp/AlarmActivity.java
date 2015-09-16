package app.xlf.com.smartwindowapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    public static String MY_DATA="myData";
    public static String CHECK="check";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        setCurtianOpen();
        init();

    }
    public void init(){
        save(CHECK, false, 1);

        AudioManager mAudioManager = null;
        mAudioManager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d("MAX",max+"");
        Log.d("CURRENT",current+"");

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);
        Calendar currentTime=Calendar.getInstance();
        final MediaPlayer mediaPlayer=MediaPlayer.create(AlarmActivity.this, R.raw.alarm_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        AlertDialog.Builder builder=new AlertDialog.Builder(AlarmActivity.this)
                .setTitle("闹铃响起")
                .setIcon(R.drawable.ic_add_alarm_black_18dp)
                .setMessage("现在时间"+currentTime.get(Calendar.HOUR_OF_DAY)+":"+currentTime.get(Calendar.MINUTE))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaPlayer.stop();
                        finish();

                    }
                });
        builder.create().show();



    }


    /*
    *@param type 1代表Boolean 2代表Float 3代表Int 4代表Long 5代表String
     */
    public static final String OK="OK";
    public static final String ERROR="ERROR";
    private String save(String key,Object content,int type){
        SharedPreferences sharedPreferences=getSharedPreferences(MY_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        switch (type){
            case 1:
                editor.putBoolean(key, (boolean) content);
                editor.commit();
                return OK;
            case 2:
                editor.putFloat(key, (float) content);
                editor.commit();
                return OK;
            case 3:
                editor.putInt(key, (int) content);
                editor.commit();
                return OK;
            case 4:
                editor.putLong(key, (long) content);
                editor.commit();
                return OK;
            case 5:
                editor.putString(key, (String) content);
                editor.commit();
                return OK;
            default:
                return ERROR;
        }
    }
    /*
    *@param type 1代表Boolean 2代表Float 3代表Int 4代表Long 5代表String
     */
    private Object read(String key,int type){
        SharedPreferences sharedPreferences=getSharedPreferences(MY_DATA, Context.MODE_PRIVATE);
        switch (type){
            case 1:
                return sharedPreferences.getBoolean(key,false);
            case 2:
                return sharedPreferences.getFloat(key, (float) -1.0);
            case 3:
                return sharedPreferences.getInt(key,-1);
            case 4:
                return sharedPreferences.getLong(key,-1);
            case 5:
                return sharedPreferences.getString(key,null);
            default:
                return null;
        }
    }

    public void setCurtianOpen(){
        new Thread(){
            @Override
            public void run() {
                HttpClient httpClient=new DefaultHttpClient();

                HttpPost post=new HttpPost("http://api.yeelink.net/v1.1/device/285189/sensor/337613/datapoints");
                post.addHeader("U-ApiKey", "69b13cc9af8cdcc38e659a33f10bb2dd");

                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("value", 1);
                    StringEntity entity=new StringEntity(jsonObject.toString());
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    HttpResponse httpResponse= httpClient.execute(post);
                    HttpEntity httpEntity=httpResponse.getEntity();
                    if (httpEntity!=null){
                        String content= EntityUtils.toString(httpEntity);
                        Log.d("internet data", content);
                        if (content.contains("error\":\"TOO FREQUENTLY REQUESTS (INTERVAL MORE THAN 10S)")){
                            Toast.makeText(AlarmActivity.this,"操作过于频繁，请稍后再试",Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
}
