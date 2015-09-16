package app.xlf.com.smartwindowapp.ServicePackage;

import org.json.JSONException;
import org.json.JSONObject;

//PM2.5信息
public class PM25{
    private String curPm;
    private String pm25InIn;
    private String pm10;
    private String level;
    private String quality;
    private String des;

    public String getCurPm() {
        return curPm;
    }

    public String getPm25InIn() {
        return pm25InIn;
    }

    public String getPm10() {
        return pm10;
    }

    public String getLevel() {
        return level;
    }

    public String getQuality() {
        return quality;
    }

    public String getDes() {
        return des;
    }

    public PM25(JSONObject data){
        //获取当日空气质量的数据
        try {
            JSONObject pm25  = data.getJSONObject("pm25");
            JSONObject pm25In = pm25.getJSONObject("pm25");
            curPm = pm25In.getString("curPm");
            pm25InIn = pm25In.getString("pm25");
            pm10 = pm25In.getString("pm10");
            level = pm25In.getString("level");
            quality = pm25In.getString("quality");
            des = pm25In.getString("des");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d("PM2.5","curPm:"+curPm+" "+"pm25InIn:"+pm25InIn+" "+"pm10:"+pm10+" "+"level:"+level+" "+"quality:"+quality+" "+"des:"+des);
    }
}
