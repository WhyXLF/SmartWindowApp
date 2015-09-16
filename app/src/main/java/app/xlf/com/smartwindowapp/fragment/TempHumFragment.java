package app.xlf.com.smartwindowapp.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import app.xlf.com.smartwindowapp.R;
import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class TempHumFragment extends Fragment {

    private RoundCornerProgressBar progressOne;
    private RoundCornerProgressBar progressTwo;
    private RoundCornerProgressBar progressThree;
    private RoundCornerProgressBar progressFour;
    public float progress1 = 0;
    public float progress2 = 0;
    public float progress3 = 0;
    public float progress4 = 0;
    public TextView outterDegree,outterHum,innerDegree,innerHum;
    private Button advice;
    public static int IN_OUT_TEMP;
    public static int IN_OUT_HUM;
    public TempHumFragment() {
    }


    private View root;
    private AlertDialog alertDialog;

    private String message = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("progress", Context.MODE_PRIVATE);

        IN_OUT_TEMP=sharedPreferences.getInt("tmp_progress",0);
        IN_OUT_HUM=sharedPreferences.getInt("hum_progress",0);

        root = inflater.inflate(R.layout.fragment_temp_hum, container, false);
        initView();

        alertDialog = new SpotsDialog(getActivity());
        alertDialog.show();
        getMessage(321368, 337610, 337611, 337612);
        return root;
    }

    AlertDialog.Builder builder;

    private void initView() {
        progressOne = (RoundCornerProgressBar) root.findViewById(R.id.progressOne);
        progressTwo = (RoundCornerProgressBar) root.findViewById(R.id.progressTwo);
        progressThree = (RoundCornerProgressBar) root.findViewById(R.id.progressThree);
        progressFour = (RoundCornerProgressBar) root.findViewById(R.id.progressFour);
        outterDegree= (TextView) root.findViewById(R.id.outterDegree);
        innerDegree= (TextView) root.findViewById(R.id.innerDegree);
        outterHum= (TextView) root.findViewById(R.id.outterHum);
        innerHum= (TextView) root.findViewById(R.id.innerHum);
        advice = (Button) root.findViewById(R.id.advice);
        advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder != null)
                    builder.show();
            }
        });

    }

    public void refreshProgress(final int one, final int two, final int three, final int four) {


        progress1 = 0;
        progress2 = 0;
        progress3 = 0;
        progress4 = 0;
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(20);
                        if (progress1 >= one && progress2 >= two && progress3 >= three && progress4 >= four) {
                            break;
                        }
                        refreshHandler.sendEmptyMessage(1);
                        if (progress1 < one) {
                            progress1 += 0.8;
                        }
                        if (progress2 < two) {
                            progress2 += 0.8;
                        }
                        if (progress3 < three) {
                            progress3 += 0.8;
                        }
                        if (progress4 < four) {
                            progress4 += 0.8;
                        }
                        refreshHandler.sendEmptyMessage(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressOne.setProgress(progress1);
            outterDegree.setText((int)progress1 + "℃");
            progressTwo.setProgress(progress2);
            outterHum.setText((int)progress2 + "%");
            progressThree.setProgress(progress3);
            innerDegree.setText((int)progress3+"℃");
            progressFour.setProgress(progress4);
            innerHum.setText((int)progress4+"%");
        }
    };

    public int intem = 0;
    public int outtem = 0;
    public int inHum = 0;
    public int outHum = 0;

    public void getMessage(final int sensor1, final int sensor2, final int sensor3, final int sensor4) {

        final Thread thread = new Thread() {
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet get1 = new HttpGet("http://api.yeelink.net/v1.1/device/285189/sensor/" + sensor1 + "/datapoints");
                get1.addHeader("U-ApiKey", "69b13cc9af8cdcc38e659a33f10bb2dd");
                HttpGet get2 = new HttpGet("http://api.yeelink.net/v1.1/device/285189/sensor/" + sensor2 + "/datapoints");
                get2.addHeader("U-ApiKey", "69b13cc9af8cdcc38e659a33f10bb2dd");
                HttpGet get3 = new HttpGet("http://api.yeelink.net/v1.1/device/285189/sensor/" + sensor3 + "/datapoints");
                get3.addHeader("U-ApiKey", "69b13cc9af8cdcc38e659a33f10bb2dd");
                HttpGet get4 = new HttpGet("http://api.yeelink.net/v1.1/device/285189/sensor/" + sensor4 + "/datapoints");
                get4.addHeader("U-ApiKey", "69b13cc9af8cdcc38e659a33f10bb2dd");

                try {
                    HttpResponse httpResponse = httpClient.execute(get1);
                    HttpEntity entity = httpResponse.getEntity();
                    if (entity != null) {
                        String content = EntityUtils.toString(entity);
                        JSONObject object = new JSONObject(content);
                        String intemStr = object.getString("value");
                        intem = Integer.parseInt(intemStr);
                    }
                    httpResponse = httpClient.execute(get2);
                    entity = httpResponse.getEntity();
                    if (entity != null) {
                        String content = EntityUtils.toString(entity);
                        JSONObject object = new JSONObject(content);
                        String outtemStr = object.getString("value");
                        outtem = Integer.parseInt(outtemStr);
                    }
                    httpResponse = httpClient.execute(get3);
                    entity = httpResponse.getEntity();
                    if (entity != null) {
                        String content = EntityUtils.toString(entity);
                        JSONObject object = new JSONObject(content);
                        String inhumStr = object.getString("value");
                        inHum = Integer.parseInt(inhumStr);
                    }
                    httpResponse = httpClient.execute(get4);
                    entity = httpResponse.getEntity();
                    if (entity != null) {
                        String content = EntityUtils.toString(entity);
                        JSONObject object = new JSONObject(content);
                        String outhumStr = object.getString("value");
                        outHum = Integer.parseInt(outhumStr);
                    }
                    alertDialog.dismiss();
                    refreshProgress(outtem, outHum, intem, inHum);

                    Log.e("in_out_temp", IN_OUT_TEMP + "");
                    Log.e("in_out_hum",IN_OUT_HUM+"");
                    if (Math.abs(outtem - intem) > IN_OUT_TEMP) {
                        Log.e("high tmp","high tmp");
                        message = "室内外温差较大，请注意着衣哦！\n";
                    }
                    else if (Math.abs(inHum - outHum) > IN_OUT_HUM) {
                        Log.e("high hum","high hum");
                        message = "室内外湿度相差较大，注意保湿哦！\n";
                    }else {
                        Log.e("in_out_temp",IN_OUT_TEMP+"");
                        Log.e("in_out_hum",IN_OUT_HUM+"");
                        if (Math.abs(outtem - intem) > IN_OUT_TEMP) {
                            message += "室内外温差较大，请注意着衣哦！\n";
                        }
                        else if (Math.abs(inHum - outHum) > IN_OUT_HUM) {
                            message += "室内外湿度相差较大，注意保湿哦！\n";
                        }else{
                            message="暂无推荐信息";
                        }
                    }
                    Message msg=new Message();
                    msg.obj=message;
                    messageHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    getMessage(sensor1, sensor2, sensor3, sensor4);
                    Toast.makeText(getActivity(), "通信失败！", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    getMessage(sensor1, sensor2, sensor3, sensor4);
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            builder = new AlertDialog.Builder(getActivity()).setTitle("推荐信息").setPositiveButton("确定", null).setMessage(msg.obj.toString()).setIcon(R.drawable.ic_adb_black_18dp);
            builder.create();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
