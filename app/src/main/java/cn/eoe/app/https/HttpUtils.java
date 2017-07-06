package cn.eoe.app.https;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.eoe.app.ui.WeatherActivity;

import static cn.eoe.app.ui.WeatherActivity.ChangeCity;
import static cn.eoe.app.ui.WeatherActivity.Notification;
import static cn.eoe.app.utils.JSONUtils.weatherData;

/**
 * Created by Administrator on 2017/5/13.
 */

public class HttpUtils {   // 网络请求
    private int time =0;
    private int returnCode=200;
    private static final int what = 1;
    private StringBuilder response;
    private static HttpUtils httpUtils;
    public static  HttpUtils getInstance(){   // 单例模式 希望只创建一个实例，并提供一个全局的访问点
        if(httpUtils==null){              // 双重锁定检查
            synchronized (HttpUtils.class){     // 用synchornized修饰一段时间内只能执行一个线程
                if(httpUtils==null){
                    httpUtils = new HttpUtils();
                }
            }
        }
        return httpUtils;
    }
    public  void sendHttpRequest(final String address, final Activity activity) {   //请求网络返回数据
        time=time+1;     //判断第几次访问网络
        new Thread(new Runnable() {
            public void run() {              //由于访问网络是耗时操作，开启子线程进行网络请求
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);//获取网页地址
                    connection = (HttpURLConnection) url.openConnection();   //获取HttpURLConnection实例

                    connection.setRequestMethod("GET");   //请求获取数据

                    connection.setConnectTimeout(8000);     // 连接超时

                    connection.setReadTimeout(8000);        // 读取超时

                    connection.connect();

                    returnCode=connection.getResponseCode();   //获取状态码

                    InputStream inputStream = connection.getInputStream();    // 获取输入流

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream , "UTF-8"));

                    response = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Message message = new Message();          //主线程进行ui操作
                    message.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("weather_data",response.toString());
                    message.setData(bundle);
                    WeatherActivity.handler.sendMessage(message);
                } catch (Exception e) {
                } finally {
                    if (connection != null) {
                        connection.disconnect();     //断开连接
                    }
                }
                if (returnCode==200&&time!=1){      //返回数据正确 通知切换城市
                    Intent intent = new Intent(ChangeCity);
                    intent.putExtra("address",address);
                    activity.sendBroadcast(intent);
                    weatherData.clear();
                }
                else if(returnCode!=200){
                    Intent intent2 = new Intent(Notification);  //通知输入错误
                    activity.sendBroadcast(intent2);
                }
            }
        }).start();
    }
}

