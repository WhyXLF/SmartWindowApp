package app.xlf.com.smartwindowapp.ServicePackage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//生活推荐信息
    public class Life {
        private String[] chuanyiStr;
        private String[] ganmaoStr;
        private String[] kongtiaoStr;
        private String[] wuranStr;
        private String[] xicheStr;
        private String[] yundongStr;
        private String[] ziwaixianStr;
        private String date;

    public String[] getChuanyiStr() {
        return chuanyiStr;
    }

    public String[] getGanmaoStr() {
        return ganmaoStr;
    }

    public String[] getKongtiaoStr() {
        return kongtiaoStr;
    }

    public String[] getWuranStr() {
        return wuranStr;
    }

    public String[] getXicheStr() {
        return xicheStr;
    }

    public String[] getYundongStr() {
        return yundongStr;
    }

    public String[] getZiwaixianStr() {
        return ziwaixianStr;
    }

    public String getDate() {
        return date;
    }

    public Life(JSONObject data) {
            try {
                JSONObject life=data.getJSONObject("life");
                JSONObject info = life.getJSONObject("info");
                date = life.getString("date");

                JSONArray chuanyi = info.getJSONArray("chuanyi");
                JSONArray ganmao = info.getJSONArray("ganmao");
                JSONArray kongtiao = info.getJSONArray("kongtiao");
                JSONArray wuran = info.getJSONArray("wuran");
                JSONArray xiche = info.getJSONArray("xiche");
                JSONArray yundong = info.getJSONArray("yundong");
                JSONArray ziwaixian = info.getJSONArray("ziwaixian");

                chuanyiStr = new String[]{chuanyi.getString(0), chuanyi.getString(1)};
                ganmaoStr = new String[]{ganmao.getString(0), ganmao.getString(1)};
                kongtiaoStr = new String[]{kongtiao.getString(0), kongtiao.getString(1)};
                wuranStr = new String[]{wuran.getString(0), wuran.getString(1)};
                xicheStr = new String[]{xiche.getString(0), xiche.getString(1)};
                yundongStr = new String[]{yundong.getString(0), yundong.getString(1)};
                ziwaixianStr = new String[]{ziwaixian.getString(0), ziwaixian.getString(1)};
            } catch (JSONException e) {
                e.printStackTrace();
            }
//        Log.d("Life","chuanyi:"+chuanyiStr+" "+"ganmao:"+ganmaoStr+" "+"kongtiao:"+kongtiaoStr+" "+"wuran:"+wuranStr+" "+"xiche:"+xicheStr+" "+"yundong:"+yundongStr+" "+"ziwaixian:"+ziwaixianStr);
        }
    }
