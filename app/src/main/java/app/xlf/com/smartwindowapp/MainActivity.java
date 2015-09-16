package app.xlf.com.smartwindowapp;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import app.xlf.com.smartwindowapp.fragment.BlankFragment;
import app.xlf.com.smartwindowapp.fragment.TempHumFragment;
import app.xlf.com.smartwindowapp.fragment.WeatherReportFragment;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MaterialTabListener,WeatherReportFragment.Callbacks, Toolbar.OnMenuItemClickListener {

    public static String MY_DATA="myData";
    public static String CHECK="check";
    public static String ET_HOUR="et_hour";
    public static String ET_MINUTE="et_minute";
    public static String WINDOW="window";
    public static String CURTAIN="curtain";
    public static
    Toolbar toolbar;
    Button window,curtain;
    Button clock;
    Switch clockSwitch;
    ViewPager pager;
    ViewPagerAdapter pagerAdapter;
    MaterialTabHost tabHost;
    Resources res;
    Calendar c;
    TimePickerDialog timePickerDialog;
    PendingIntent pi;
    AlarmManager alarmManager;
    TextView etHour,etMinute;
    public static boolean windowStatus=true;
    public static boolean curtainStatus=true;
    public static int HIGH_PM25=150;
    public static final int REQUEST_CODE=1000;

    private TempHumFragment tempHumFragment;
    private WeatherReportFragment weatherReportFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res=getResources();
        HIGH_PM25=getSharedPreferences("progress",Context.MODE_PRIVATE).getInt("pm_progress",150);
        initView();
        getMessage();
    }

    private void initView() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("智能窗户系统");
        BitmapDrawable bitmapDrawable= (BitmapDrawable) res.getDrawable(R.mipmap.ic_launcher);
        toolbar.setLogo(bitmapDrawable);
        this.setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(this);
        window= (Button) findViewById(R.id.window);
        curtain= (Button) findViewById(R.id.curtain);
        clock= (Button) findViewById(R.id.clock);
        clockSwitch= (Switch) findViewById(R.id.clockSwitch);
        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);
        etHour= (TextView) findViewById(R.id.etHour);
        etMinute= (TextView) findViewById(R.id.etMinute);
        window.setOnClickListener(this);
        curtain.setOnClickListener(this);
        clock.setOnClickListener(this);
        clockSwitch.setOnClickListener(this);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d("onPageSelected", position + "");
                if (position==0&&tempHumFragment!=null){
                    tempHumFragment.refreshProgress(tempHumFragment.outtem, tempHumFragment.outHum, tempHumFragment.intem, tempHumFragment.inHum);
                }
                tabHost.setSelectedNavigationItem(position);

            }
        });
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setIcon(getIcon(i))
                            .setTabListener(this)
            );
        }
        alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);

        //读取保存的数据
        clockSwitch.setChecked((Boolean) read(CHECK,1));
        etHour.setText((String) read(ET_HOUR, 5));
        etMinute.setText((String)read(ET_MINUTE,5));
        windowStatus= (boolean) read(WINDOW,1);
        curtainStatus= (boolean) read(CURTAIN,1);
        window.setTextColor((windowStatus)?Color.WHITE:Color.CYAN);
        curtain.setTextColor((curtainStatus)?Color.WHITE:Color.CYAN);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.window:
                if (windowStatus==true){
                    windowStatus=false;
                    window.setTextColor(Color.CYAN);
                    postMessage(1,false);
                }else{
                    windowStatus=true;
                    postMessage(1,true);
                    window.setTextColor(Color.WHITE);
                }
                break;
            case R.id.curtain:
                if (curtainStatus==true){
                    curtainStatus=false;
                    curtain.setTextColor(Color.CYAN);
                    postMessage(2,false);
                }else {
                    curtainStatus=true;
                    curtain.setTextColor(Color.WHITE);
                    postMessage(2,true);
                }
                break;
            case R.id.clock:
                showAlertDialog();
                break;
            case R.id.clockSwitch:
                if (clockSwitch.isChecked()){
                    if (alarmManager!=null){
                        c=Calendar.getInstance();
                        c.set(Calendar.SECOND,0);
                        c.set(Calendar.MINUTE,Integer.parseInt(etMinute.getText().toString()));
                        c.set(Calendar.HOUR_OF_DAY,Integer.parseInt(etHour.getText().toString()));

                        //时间调整
                        Calendar currentTime=Calendar.getInstance();
                        int cur_hourOfDay=currentTime.get(Calendar.HOUR_OF_DAY);
                        int cur_minute=currentTime.get(Calendar.MINUTE);
                        int c_date_orgin=c.get(Calendar.DATE);
                        int c_hourOfDay=c.get(Calendar.HOUR_OF_DAY);
                        int c_minute=c.get(Calendar.MINUTE);
                        if (c_hourOfDay<cur_hourOfDay||(c_hourOfDay==cur_hourOfDay&&c_minute<cur_minute)){
                            c.set(Calendar.DATE,c_date_orgin+1);
                            Log.d("time adjustment","date from "+c_date_orgin+" to "+c.get(Calendar.DATE));
                        }

                        alarmManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pi);
                        Toast.makeText(MainActivity.this,"闹钟设置完成！",Toast.LENGTH_SHORT).show();
                        save(CHECK,true,1);
                    }
                }else {
                    if (alarmManager!=null){
                        pi=PendingIntent.getActivity(MainActivity.this,REQUEST_CODE,new Intent(MainActivity.this,AlarmActivity.class),0);
                        alarmManager.cancel(pi);
                        save(CHECK,false,1);
                        Toast.makeText(MainActivity.this,"闹钟已经关闭！",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.setting:
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                break;
        }
    }

    private void showAlertDialog() {
        Calendar defaultTime=Calendar.getInstance();
       timePickerDialog=new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
           @Override
           public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
               Intent intent=new Intent(MainActivity.this,AlarmActivity.class);
               pi=PendingIntent.getActivity(MainActivity.this, REQUEST_CODE, intent, 0);
               c=Calendar.getInstance();
               c.setTimeInMillis(System.currentTimeMillis());
               c.set(Calendar.SECOND, 0);
               c.set(Calendar.MINUTE, minute);
               c.set(Calendar.HOUR_OF_DAY, hourOfDay);

               //时间调整
               Calendar currentTime=Calendar.getInstance();
               int cur_hourOfDay=currentTime.get(Calendar.HOUR_OF_DAY);
               int cur_minute=currentTime.get(Calendar.MINUTE);
               int c_date_orgin=c.get(Calendar.DATE);
               int c_hourOfDay=c.get(Calendar.HOUR_OF_DAY);
               int c_minute=c.get(Calendar.MINUTE);
               if (c_hourOfDay<cur_hourOfDay||(c_hourOfDay==cur_hourOfDay&&c_minute<cur_minute)){
                   c.set(Calendar.DATE,c_date_orgin+1);
                   Log.d("time adjustment","date from "+c_date_orgin+" to "+c.get(Calendar.DATE));
               }
               //设置显示调整
               if (c.get(Calendar.HOUR_OF_DAY)<10){
                   etHour.setText("0"+c.get(Calendar.HOUR_OF_DAY));
               }else{
                   etHour.setText(c.get(Calendar.HOUR_OF_DAY) + "");
               }
               if (c.get(Calendar.MINUTE)<10){
                   etMinute.setText("0"+c.get(Calendar.MINUTE));
               }else{
                   etMinute.setText(c.get(Calendar.MINUTE) + "");
               }

               //取消之前的闹钟
               if (clockSwitch.isChecked()){
                   alarmManager.cancel(pi);
               }
               //设置闹钟
               alarmManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pi);
               clockSwitch.setChecked(true);
               //保存开关状态
               save(CHECK,true,1);
               save(ET_HOUR,etHour.getText().toString(),5);
               save(ET_MINUTE,etMinute.getText().toString(),5);

               Toast.makeText(MainActivity.this,"闹钟设置完成！",Toast.LENGTH_SHORT).show();
           }
       },defaultTime.get(Calendar.HOUR_OF_DAY),defaultTime.get(Calendar.MINUTE),false);
        timePickerDialog.show();
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        pager.setCurrentItem(materialTab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {
    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {
    }

    @Override
    public void onDataTooHigh(final int pm25,boolean isRain) {
        if (windowStatus==true) {
            if (pm25!=0){
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("PM2.5值过高")
                        .setIcon(R.drawable.ic_error_black_18dp)
                        .setMessage("今日PM2.5值为" + pm25 + "，建议您关闭窗户，减少室内污染")
                        .setPositiveButton("关闭窗户", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                window.setTextColor(Color.CYAN);
                                windowStatus = false;
                                postMessage(1, windowStatus);
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.create().show();
            }
            if (isRain==true){
                Log.d("onHigh isRain",isRain+"");
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("下雨")
                        .setIcon(R.drawable.ic_error_black_18dp)
                        .setMessage("今天有雨，建议您关闭窗户，减少室内污染")
                        .setPositiveButton("关闭窗户", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                window.setTextColor(Color.CYAN);
                                windowStatus = false;
                                postMessage(1, windowStatus);

                            }
                        })
                        .setNegativeButton("取消", null);
                builder.create().show();
            }
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    //内部的pagerAdapter
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        public Fragment getItem(int num) {
            switch (num){
                case 0:
                    tempHumFragment=new TempHumFragment();
                    return tempHumFragment;
                case 1:
                    weatherReportFragment=new WeatherReportFragment();
                    return weatherReportFragment;
                default:
                    return new BlankFragment();
            }
        }
        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return "tabHost 1";
                case 1: return "tabHost 2";
                default: return null;
            }
        }
    }
    private Drawable getIcon(int position) {
        switch(position) {
            case 0:
                return res.getDrawable(R.drawable.ic_person_black_24dp);
            case 1:
                return res.getDrawable(R.drawable.ic_group_black_24dp);
        }
        return null;
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
                return sharedPreferences.getString(key,"");
            default:
                return null;
        }
    }


    public void getMessage(){
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true){
                    try {
                        HttpClient httpClient=new DefaultHttpClient();
                        //请求窗户开关状态数据
                        HttpGet get=new HttpGet("http://api.yeelink.net/v1.1/device/285189/sensor/337582/datapoints");
                        get.addHeader("U-ApiKey","69b13cc9af8cdcc38e659a33f10bb2dd");
                        try {
                            HttpResponse httpResponse=httpClient.execute(get);
                            HttpEntity entity=httpResponse.getEntity();
                            if (entity!=null){
                                String content=EntityUtils.toString(entity);
                                JSONObject jsonObject=new JSONObject(content);
                               windowStatus= (((Integer)(jsonObject.get("value")))==1)?true:false;
                                Message msg=new Message();
                                msg.what=1;
                                cloudHandler.sendMessage(msg);
                                Log.d("internet window data", content);
                                Log.d("windowStatus",windowStatus+"");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //请求窗帘状态信息
                        get=new HttpGet("http://api.yeelink.net/v1.1/device/285189/sensor/337613/datapoints");
                        get.addHeader("U-ApiKey","69b13cc9af8cdcc38e659a33f10bb2dd");
                        try {
                            HttpResponse httpResponse=httpClient.execute(get);
                            HttpEntity entity=httpResponse.getEntity();
                            if (entity!=null){
                                String content=EntityUtils.toString(entity);
                                JSONObject jsonObject=new JSONObject(content);
                                curtainStatus= (((Integer)(jsonObject.get("value")))==1)?true:false;
                                Message msg=new Message();
                                msg.what=2;
                                cloudHandler.sendMessage(msg);
                                Log.d("internet curtain data",content);
                                Log.d("curtainStatus",curtainStatus+"");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    public void postMessage(final int i, final boolean state){
        final int status=(state==true)?1:0;
        switch (i){
            //控制窗户
            case 1:
                new Thread(){
                    @Override
                    public void run() {
                        HttpClient httpClient=new DefaultHttpClient();

                        HttpPost post=new HttpPost("http://api.yeelink.net/v1.1/device/285189/sensor/337582/datapoints");
                        post.addHeader("U-ApiKey", "69b13cc9af8cdcc38e659a33f10bb2dd");

                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("value", status);
                            StringEntity entity=new StringEntity(jsonObject.toString());
                            entity.setContentType("application/json");
                            post.setEntity(entity);
                            HttpResponse httpResponse= httpClient.execute(post);
                            HttpEntity httpEntity=httpResponse.getEntity();
                            if (httpEntity!=null){
                                String content= EntityUtils.toString(httpEntity);
                                Log.d("internet data", content);
                                if (content.contains("error\":\"TOO FREQUENTLY REQUESTS (INTERVAL MORE THAN 10S)")){
                                    windowStatus=!windowStatus;
                                    Log.d("window status=",windowStatus+"");
                                    Message msg=new Message();
                                    cloudHandler.sendMessage(msg);
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
                break;
            //控制窗帘
            case 2:
                new Thread(){
                    @Override
                    public void run() {
                        HttpClient httpClient=new DefaultHttpClient();

                        HttpPost post=new HttpPost("http://api.yeelink.net/v1.1/device/285189/sensor/337613/datapoints");
                        post.addHeader("U-ApiKey", "69b13cc9af8cdcc38e659a33f10bb2dd");

                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("value", status);
                            StringEntity entity=new StringEntity(jsonObject.toString());
                            entity.setContentType("application/json");
                            post.setEntity(entity);
                            HttpResponse httpResponse= httpClient.execute(post);
                            HttpEntity httpEntity=httpResponse.getEntity();
                            if (httpEntity!=null){
                                String content= EntityUtils.toString(httpEntity);
                                Log.d("internet data", content);
                                if (content.contains("error\":\"TOO FREQUENTLY REQUESTS (INTERVAL MORE THAN 10S)")){
                                    curtainStatus=!curtainStatus;
                                    Log.d("window status=", curtainStatus + "");
                                    Message msg=new Message();
                                    cloudHandler.sendMessage(msg);
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
                break;
            default:
                break;
        }

    }

    Handler cloudHandler=new Handler(){
        int cyan=Color.CYAN;
        int white=Color.WHITE;
        int color = 0;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    color=(windowStatus==true)?white:cyan;
                    window.setTextColor(color);
                    color=(curtainStatus==true)?white:cyan;
                    curtain.setTextColor(color);
                    break;
                case 2:
                    color=(windowStatus==true)?white:cyan;
                    window.setTextColor(color);
                    color=(curtainStatus==true)?white:cyan;
                    curtain.setTextColor(color);
                    break;
                default:
                    color=(windowStatus==true)?white:cyan;
                    window.setTextColor(color);
                    color=(curtainStatus==true)?white:cyan;
                    curtain.setTextColor(color);
                    Toast.makeText(MainActivity.this,"您的刷新频率过快请稍后再试！",Toast.LENGTH_SHORT).show();
                    break;

            }


        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        save(WINDOW, windowStatus, 1);
        save(CURTAIN,curtainStatus,1);

    }

    @Override
    protected void onRestart() {
        //读取保存的数据
        clockSwitch.setChecked((Boolean) read(CHECK,1));
        etHour.setText((String) read(ET_HOUR, 5));
        etMinute.setText((String)read(ET_MINUTE,5));
        super.onRestart();
    }

    @Override
    protected void onResume() {
        //读取保存的数据
        clockSwitch.setChecked((Boolean) read(CHECK, 1));
        etHour.setText((String) read(ET_HOUR, 5));
        etMinute.setText((String) read(ET_MINUTE, 5));
        super.onResume();
    }


}
