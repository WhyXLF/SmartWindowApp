package app.xlf.com.smartwindowapp.ServicePackage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//7天天气信息
public class WeatherData {
    private EveryDayWeather everyDayWeather[];
    private JSONArray weather;

    public EveryDayWeather[] getEveryDayWeather() {
        return everyDayWeather;
    }

    public JSONArray getWeather() {
        return weather;
    }

    public WeatherData(JSONObject data) {
        try {
            weather = data.getJSONArray("weather");
            everyDayWeather = new EveryDayWeather[7];

            //循环获取每日天气信息
            for (int i = 0; i < weather.length(); i++) {
                everyDayWeather[i] = new EveryDayWeather(weather.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
