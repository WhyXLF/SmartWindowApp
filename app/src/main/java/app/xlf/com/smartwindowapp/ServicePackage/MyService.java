package app.xlf.com.smartwindowapp.ServicePackage;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MyService extends Service {
    public static String MY_PREFS="my_sharedPreferences";
    public static String ALL_DATA="my_all_data";
    private WeatherData weatherData;
    private PM25 pm25;
    private Life life;

    public MyService() {
    }

    @Override
    public void onCreate() {
        System.out.println("hello onCreate!");
        readURL("http://op.juhe.cn/onebox/weather/query?key=ad0def305c1281b93fa943c0cec4f765&dtype=json&cityname=%E7%A7%A6%E7%9A%87%E5%B2%9B");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand!");
        readURL("http://op.juhe.cn/onebox/weather/query?key=ad0def305c1281b93fa943c0cec4f765&dtype=json&cityname=%E7%A7%A6%E7%9A%87%E5%B2%9B");
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("onBind","OK!");
        return binder;
    }


    public class MyBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    private final IBinder binder = new MyBinder();

    //读取网络数据
    private void readURL(String url) {
        new MyAsyncTask().execute(url);
    }

    //后台线程读取网络数据
    private class MyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {
            SharedPreferences mySharedPreferences=getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE);
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(arg0[0]);
                URLConnection conn = url.openConnection();
                InputStream inputStream = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(isr);
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (MalformedURLException e) {
                String data=mySharedPreferences.getString(ALL_DATA,"");
                return data;
            } catch (IOException e) {
                String data=mySharedPreferences.getString(ALL_DATA,"");
                return data;
            }

            SharedPreferences.Editor editor=mySharedPreferences.edit();
            editor.putString(ALL_DATA,sb.toString());
            editor.apply();
            return sb.toString();
        }


        @Override
        protected void onPostExecute(String s) {
                praseJson(s);
        }

    }

    //解析Json文件
    private void praseJson(String s) {
        try {
            JSONObject root = new JSONObject(s);
            JSONObject result = root.getJSONObject("result");
            JSONObject data = result.getJSONObject("data");

            //创建WeatherData实例
            weatherData = new WeatherData(data);
//                EveryDayWeather dayWeather[]=new EveryDayWeather[7];
//            System.out.println(weatherData.getWeather());
            //创建PM25实例
            pm25 = new PM25(data);
//            //创建Life实例
            life = new Life(data);
            if (callbackToMain!=null){
                callbackToMain.onDataChanged(weatherData,pm25,life);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //设置回调
    private Callback callbackToMain = null;

    public Callback getCallbackToMain() {
        return callbackToMain;
    }

    public void setCallbackToMain(Callback callbackToMain) {
        this.callbackToMain = callbackToMain;
    }

    public static interface Callback {
        public void onDataChanged(WeatherData weatherData, PM25 pm25, Life life);
    }

}
