package cn.eoe.app.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.eoe.app.entity.DataBean;

/**
 * Created by Administrator on 2017/5/13.
 */

public class JSONUtils {   //对JSON内容解析
    public static List<DataBean.DailyBean> weatherData = new ArrayList<DataBean.DailyBean>();

    public static List<DataBean.DailyBean> parseJson(String jsonData) {
        try {
            JSONObject object = new JSONObject(jsonData);                  // 根据JSON格式获取想要的数据
            JSONObject object1 = object.getJSONArray("results").getJSONObject(0);
            JSONArray array = object1.getJSONArray("daily");
            for (int i = 0; i < array.length(); i++) {
                DataBean.DailyBean dailyBean = new DataBean.DailyBean();
                dailyBean.setDate(array.getJSONObject(i).getString("date").toString());
                dailyBean.setText_day(array.getJSONObject(i).getString("text_day").toString());
                Log.d("SSSSSSS",array.getJSONObject(i).getString("text_day").toString());
                dailyBean.setCode_day(array.getJSONObject(i).getString("code_day").toString());
                dailyBean.setText_night(array.getJSONObject(i).getString("text_night").toString());
                dailyBean.setCode_night(array.getJSONObject(i).getString("code_night").toString());
                dailyBean.setHigh(array.getJSONObject(i).getString("high").toString());
                dailyBean.setLow(array.getJSONObject(i).getString("low").toString());
                dailyBean.setPrecip(array.getJSONObject(i).getString("precip").toString());
                dailyBean.setWind_direction(array.getJSONObject(i).getString("wind_direction").toString());
                dailyBean.setWind_direction_degree(array.getJSONObject(i).getString("wind_direction_degree").toString());
                dailyBean.setWind_speed(array.getJSONObject(i).getString("wind_speed").toString());
                dailyBean.setWind_scale(array.getJSONObject(i).getString("wind_scale").toString());
                weatherData.add(dailyBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
            return weatherData;
        }
}

