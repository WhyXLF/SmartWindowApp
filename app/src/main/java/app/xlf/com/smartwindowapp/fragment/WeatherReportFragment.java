package app.xlf.com.smartwindowapp.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


import app.xlf.com.smartwindowapp.R;
import app.xlf.com.smartwindowapp.ServicePackage.Life;
import app.xlf.com.smartwindowapp.ServicePackage.MyService;
import app.xlf.com.smartwindowapp.ServicePackage.PM25;
import app.xlf.com.smartwindowapp.ServicePackage.WeatherData;

public class WeatherReportFragment extends Fragment {

    public static int HIGH_PM25=0;
    private static TextView tvWeatherDegree, tvWeatherText, tvWeatherPM25,tvOne, tvTwo, tvThree, tvFour;
    private static ImageView ivWeather;
    private static String tipString="hello";
    private MyService myService;
    private View root;
    PopupWindow popupWindow;

    private Callbacks mCallbacks;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_weather_report, container, false);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("progress",Context.MODE_PRIVATE);
        HIGH_PM25=sharedPreferences.getInt("pm_progress",0);
        Log.e("high_pm",HIGH_PM25+"");
        initView();
        return root;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)){
            throw new IllegalStateException("所在的Activity必须实现Callback接口！");
        }
        mCallbacks= (Callbacks) activity;
        bindMyService();
    }

    //初始化界面
    private void initView() {
        tvWeatherDegree = (TextView) root.findViewById(R.id.tvWeatherDegree);
        tvWeatherText = (TextView) root.findViewById(R.id.tvWeatherText);
        tvWeatherPM25= (TextView) root.findViewById(R.id.tvWeatherPM25);
        tvOne = (TextView) root.findViewById(R.id.one);
        tvTwo = (TextView) root.findViewById(R.id.two);
        tvThree = (TextView) root.findViewById(R.id.three);
        tvFour = (TextView) root.findViewById(R.id.four);
        ivWeather= (ImageView) root.findViewById(R.id.ivWeather);
        final View popupView=getActivity().getLayoutInflater().inflate(R.layout.weather_detail,null);
        popupWindow=new PopupWindow(popupView,560,720);
        ivWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvTip= (TextView) popupView.findViewById(R.id.tvTip);
                tvTip.setText(tipString);
                popupWindow.showAtLocation(root.findViewById(R.id.tvWeatherDegree), Gravity.CENTER, 20, 20);
            }
        });

        Button ok= (Button) popupView.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
//        tvTip= (TextView) root.findViewById(R.id.tvTip);

    }

    private void bindMyService() {
        getActivity().bindService(new Intent(getActivity(), MyService.class), wrConnection, Context.BIND_AUTO_CREATE);
    }

    //连接Service
    private ServiceConnection wrConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("Weather connect", "ServiceConnected!");
            myService = ((MyService.MyBinder) iBinder).getService();
            myService.setCallbackToMain(new MyService.Callback() {
                @Override
                public void onDataChanged(WeatherData weatherData, PM25 pm25, Life life) {
                    Log.d("data changed!","OK");
                    Message msg = new Message();
                    String weatherDegreeStr = weatherData.getEveryDayWeather()[0].getNightStr()[2] + "~" + weatherData.getEveryDayWeather()[0].getDayStr()[2] + "℃";
                    Log.d("wehater degree:",weatherDegreeStr);
                    msg.what = 0;
                    msg.obj = weatherDegreeStr;
                    myHander.handleMessage(msg);
                    String weatherTextStr;
                    String dayStr=weatherData.getEveryDayWeather()[0].getDayStr()[1];
                    String nightStr=weatherData.getEveryDayWeather()[0].getNightStr()[1];
                    if (dayStr.equals(nightStr)){
                        weatherTextStr=dayStr;
                    }else {
                        weatherTextStr = dayStr + "转" + nightStr;
                    }
                    if (weatherTextStr.contains("雨")){
                        handleWeather("0",true);
                    }

                    msg.what = 1;
                    msg.obj = weatherTextStr;
                    myHander.handleMessage(msg);
                    String one="";
                    if (weatherData.getEveryDayWeather()[1].getDayStr()[1].equals(weatherData.getEveryDayWeather()[1].getNightStr()[1])){
                        one=weatherData.getEveryDayWeather()[1].getNightStr()[2] + "~" + weatherData.getEveryDayWeather()[1].getDayStr()[2] + "℃\n\n" + weatherData.getEveryDayWeather()[1].getDayStr()[1];
                    }else{

                        one = weatherData.getEveryDayWeather()[1].getNightStr()[2] + "~" + weatherData.getEveryDayWeather()[1].getDayStr()[2] + "℃\n\n" + weatherData.getEveryDayWeather()[1].getDayStr()[1] + "转" + weatherData.getEveryDayWeather()[1].getNightStr()[1];
                    }
                    msg.what=2;
                    msg.obj=one;
                    myHander.handleMessage(msg);
                    String two="";
                    if (weatherData.getEveryDayWeather()[2].getDayStr()[1].equals( weatherData.getEveryDayWeather()[2].getNightStr()[1])){
                        two= weatherData.getEveryDayWeather()[2].getNightStr()[2] + "~" + weatherData.getEveryDayWeather()[2].getDayStr()[2] + "℃\n\n" + weatherData.getEveryDayWeather()[2].getDayStr()[1];
                    }else {
                        two = weatherData.getEveryDayWeather()[2].getNightStr()[2] + "~" + weatherData.getEveryDayWeather()[2].getDayStr()[2] + "℃\n\n" + weatherData.getEveryDayWeather()[2].getDayStr()[1] + "转" + weatherData.getEveryDayWeather()[2].getNightStr()[1];
                    }
                    msg.what=3;
                    msg.obj=two;
                    myHander.handleMessage(msg);
                    String three="";
                    if (weatherData.getEveryDayWeather()[3].getDayStr()[1].equals(weatherData.getEveryDayWeather()[3].getNightStr()[1])){
                        three=weatherData.getEveryDayWeather()[3].getNightStr()[2] + "~" + weatherData.getEveryDayWeather()[3].getDayStr()[2] + "℃\n\n"
                                + weatherData.getEveryDayWeather()[3].getDayStr()[1];
                    }else {
                        three = weatherData.getEveryDayWeather()[3].getNightStr()[2] + "~" + weatherData.getEveryDayWeather()[3].getDayStr()[2] + "℃\n\n"
                                + weatherData.getEveryDayWeather()[3].getDayStr()[1] + "转" + weatherData.getEveryDayWeather()[3].getNightStr()[1];
                    }
                    msg.what=4;
                    msg.obj=three;
                    Log.d("Three", three);
                    myHander.handleMessage(msg);
                    String four="";
                    if (weatherData.getEveryDayWeather()[4].getDayStr()[1].equals(weatherData.getEveryDayWeather()[4].getNightStr()[1])){
                        four=weatherData.getEveryDayWeather()[4].getNightStr()[2] + "~" + weatherData.getEveryDayWeather()[4].getDayStr()[2] + "℃\n\n"
                                + weatherData.getEveryDayWeather()[4].getDayStr()[1];
                    }else {
                        four = weatherData.getEveryDayWeather()[4].getNightStr()[2] + "~" + weatherData.getEveryDayWeather()[4].getDayStr()[2] + "℃\n\n"
                                + weatherData.getEveryDayWeather()[4].getDayStr()[1] + "转" + weatherData.getEveryDayWeather()[4].getNightStr()[1];
                    }
                    msg.what=5;
                    msg.obj=four;
                    myHander.handleMessage(msg);
                    String pm25Str=pm25.getQuality()+"("+pm25.getPm25InIn()+")";
                    handleWeather(pm25.getPm25InIn(),false);
                    msg.what=6;
                    msg.obj=pm25Str;
                    myHander.handleMessage(msg);
                    String chuanyiStr="穿衣："+life.getChuanyiStr()[0]+","+life.getChuanyiStr()[1]+"\n\n";
                    String ganmaoStr="感冒："+life.getGanmaoStr()[0]+","+life.getGanmaoStr()[1]+"\n\n";
                    String kongtiaoStr="空调："+life.getKongtiaoStr()[0]+","+life.getKongtiaoStr()[1]+"\n\n";
                    String wuranStr="污染："+life.getWuranStr()[0]+","+life.getWuranStr()[1]+"\n\n";
                    String xicheStr="洗车："+life.getXicheStr()[0]+","+life.getXicheStr()[1]+"\n\n";
                    String yundongStr="运动："+life.getYundongStr()[0]+","+life.getYundongStr()[1]+"\n\n";
                    String ziwaixianStr="紫外线："+life.getZiwaixianStr()[0]+","+life.getZiwaixianStr()[1]+"\n\n";
                    StringBuilder lifeSb=new StringBuilder();
                    lifeSb.append(chuanyiStr);
                    lifeSb.append(ganmaoStr);
                    lifeSb.append(kongtiaoStr);
                    lifeSb.append(wuranStr);
                    lifeSb.append(xicheStr);
                    lifeSb.append(yundongStr);
                    lifeSb.append(ziwaixianStr);
                    msg.what=7;
                    msg.obj=lifeSb.toString();
                    myHander.handleMessage(msg);

                }


            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private Handler myHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("handle message","OK");
            switch (msg.what) {
                case 0:
                    tvWeatherDegree.setText(msg.obj.toString());
                    break;
                case 1:
                    tvWeatherText.setText(msg.obj.toString());
                    break;
                case 2:
                    tvOne.setText(msg.obj.toString());
                    break;
                case 3:
                    tvTwo.setText(msg.obj.toString());
                    break;
                case 4:
                    tvThree.setText(msg.obj.toString());
                    break;
                case 5:
                    tvFour.setText(msg.obj.toString());
                    break;
                case 6:
                    tvWeatherPM25.setText(msg.obj.toString());
                    break;
                case 7:
//                    tvTip.setText(msg.obj.toString());
                    tipString=msg.obj.toString();
                    Log.d("tipString",tipString);

                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void handleWeather(String pm25InIn,boolean isRain) {
        int ipm25InIn=Integer.parseInt(pm25InIn);
        if (ipm25InIn>HIGH_PM25){
            Log.d("ipm25InIn",pm25InIn);
            mCallbacks.onDataTooHigh(ipm25InIn,isRain);
        }

        if (isRain){
            Log.d("isRain",isRain+"");
            mCallbacks.onDataTooHigh(0,isRain);
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unbindService(wrConnection);
    }

    public interface Callbacks{
        public void onDataTooHigh(int pm25,boolean isRain);
    }

}
