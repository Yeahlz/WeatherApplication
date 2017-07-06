package cn.eoe.app.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.weatherapplication.R;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.eoe.app.adapter.SearchAdapter;
import cn.eoe.app.adapter.WeatherAdapter;
import cn.eoe.app.db.RecordsOperation;
import cn.eoe.app.entity.DataBean;
import cn.eoe.app.https.HttpUtils;
import cn.eoe.app.utils.JSONUtils;
import cn.eoe.app.utils.LocationChangeCity;

public class WeatherActivity extends AppCompatActivity {
    private View historyView;
    private RelativeLayout queryInterfaceLayout;
    private ListView recordslist;
    private TextView tv_clear_records;
    private List<String> searchContentList;
    private List<String> searchContentList2;
    RecordsOperation recordsOperation;
    private SearchAdapter searchAdapter;           //搜索查询模块
    private EditText editText;
    public ImageView iv_search;
    private InputMethodManager inputMethodManager;   // 软键盘管理器

    public static String Notification = "Notification";
    public static final String ChangeCity = "ChangeCity";
    public static String adress3 = "https://api.seniverse.com/v3/weather/daily.json?key=gqirmwtnh4w3ykfs&location=guangzhou&language=zh-Hans&unit=c&start=0&days=5";
    private static final String preUrl = "https://api.seniverse.com/v3/weather/daily.json?key=gqirmwtnh4w3ykfs&location=";
    private static final String endUrl = "&language=zh-Hans&unit=c&start=0&days=5";
    OnClickReceiver onClickReceiver = new OnClickReceiver();
    public static String Search = "Search";
    private static TextView tv_weather;
    private static TextView tv_temporature_high;
    public static List<DataBean.DailyBean> weatherData = new ArrayList<DataBean.DailyBean>();
    private static ImageView iv_weather;
    private static TextView tv_city;
    private static TextView tv_temporature_low;
    private static TextView tv_data1;
    private static TextView tv_data2;
    private static TextView tv_data3;
    private static TextView tv_date;
    private static WeatherAdapter adapter2;
    private static ListView listView;
    private static ProgressBar progressBar;            //返回数据更新ui模块
    private ImageView iv_share;
    private static Context context;
    private ImageView iv_update;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private LocationManager manager;
    String provider;              //  定位模块

    String sdCardPath;
    String filePath;
    int picture = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_today_weather);
        context();
        iv_share = (ImageView) findViewById(R.id.iv_share);
        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenshot();
                share();
            }
        });
        iv_search = (ImageView) findViewById(R.id.iv_search);           //  搜索模块的控件
        historyView = LayoutInflater.from(this).inflate(R.layout.add_clear_record, null);   //将布局压缩成控件
        recordslist = (ListView) historyView.findViewById(R.id.lv_search_records);
        tv_clear_records = (TextView) historyView.findViewById(R.id.tv_clear_records);
        editText = (EditText) findViewById(R.id.et_search_content);
        queryInterfaceLayout = (RelativeLayout) findViewById(R.id.search_content_show);
        queryInterfaceLayout.addView(historyView);    // 再次添加控件

        recordsOperation = new RecordsOperation(this);
        searchContentList = new ArrayList<>();
        searchContentList2 = new ArrayList<>();
        searchContentList.addAll(recordsOperation.getRecordsList());
        changeList();
        historyView.setVisibility(historyView.GONE);  // 搜索记录隐藏控件
        searchAdapter = new SearchAdapter(this, searchContentList2);
        recordslist.setAdapter(searchAdapter);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  //软键盘管理类
        recordslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {   //点击查询过的记录可搜索
                try {
                    String city = searchContentList2.get(position);
                    String url = preUrl + URLEncoder.encode(city, "UTF-8") + endUrl; //将中文转化为字符编码
                    HttpUtils httpUtils = HttpUtils.getInstance();       // 获取网络请求
                    httpUtils.sendHttpRequest(url, WeatherActivity.this);
                    editText.setText("");
                    historyView.setVisibility(View.GONE);
                    queryInterfaceLayout.setFocusable(true);    // 将焦点重新放在父类控件
                    queryInterfaceLayout.setFocusableInTouchMode(true);
                    if (inputMethodManager.isActive()) {   //如果软键盘收起
                        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {    //监听edittext点击事件
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {      // 如果是点击搜索键
                    if (editText.getText().toString().length() > 0) {     //假如输入不为空
                        String record = editText.getText().toString();
                        if (!recordsOperation.isRecord(record)) {    //判断是否已有过搜寻记录
                            searchContentList.add(record);
                        }
                        recordsOperation.addRecords(record);   // 数据库添加
                        changeList();
                        checkRecordSize();            // 根据是否有搜索记录来判断控件显示
                        searchAdapter.notifyDataSetChanged();  //  更新适配器数据
                        Intent intent = new Intent(Search);      // 发送通知请求网络
                        intent.putExtra("city", editText.getText().toString());
                        sendBroadcast(intent);
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(WeatherActivity.this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                    }
                    editText.setText("");
                    historyView.setVisibility(View.GONE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);  //如果软键盘收起
                    }
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {   //当输入数据变化时
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String simlarname = editText.getText().toString();
                searchContentList.clear();
                searchContentList.addAll(recordsOperation.getsimlarRecords(simlarname));  //根据数据库的模糊搜索来重新更新数据
                changeList();
                checkRecordSize();
                searchAdapter.notifyDataSetChanged();//重新显示数据
            }
        });
        tv_clear_records.setOnClickListener(new View.OnClickListener() {   //清除历史记录
            @Override
            public void onClick(View v) {
                searchContentList.clear();
                changeList();
                recordsOperation.deleteRecordsList();     // 数据库删除数据
                searchAdapter.notifyDataSetChanged();
                historyView.setVisibility(View.GONE);
            }
        });

        iv_weather = (ImageView) findViewById(R.id.iv_weather);        // 天气数据
        tv_temporature_low = (TextView) findViewById(R.id.tv_temporature_low);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_data2 = (TextView) findViewById(R.id.tv_data2);
        tv_data3 = (TextView) findViewById(R.id.tv_data3);
        tv_data1 = (TextView) findViewById(R.id.tv_data1);
        tv_weather = (TextView) findViewById(R.id.tv_weather);
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_temporature_high = (TextView) findViewById(R.id.tv_temporature_high);
        listView = (ListView) findViewById(R.id.listview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        final HttpUtils httpUtils = HttpUtils.getInstance();
        httpUtils.sendHttpRequest("https://api.seniverse.com/v3/weather/daily.json?key=gqirmwtnh4w3ykfs&location=guangzhou&language=zh-Hans&unit=c&start=0&days=5", WeatherActivity.this);

        iv_update = (ImageView) findViewById(R.id.iv_update);       //手动更新天气
        iv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpUtils.sendHttpRequest(adress3, WeatherActivity.this);
                weatherData.clear();
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Search);
        registerReceiver(onClickReceiver, intentFilter);

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction(ChangeCity);
        registerReceiver(onClickReceiver, intentFilter1);

        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(Notification);
        registerReceiver(onClickReceiver, intentFilter2);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);   // 实现定位功能
        List<String> provideList = manager.getProviders(true);    //判断位置提供器
        if (provideList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(WeatherActivity.this, "无法定位，请打开定位服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        Location location = manager.getLastKnownLocation(provider);  //获取location位置数据
        if (location != null) {
            ShowLocation(location);

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.requestLocationUpdates(provider, 1000, 1000, locationListener);    //设置位置移动监听器
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            ShowLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    private void ShowLocation(Location location) {
        tv_city.setText(LocationChangeCity.getAddress(this, location.getLatitude(), location.getLongitude()).getLocality());  //将获取位置转化为城市
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.removeUpdates(locationListener);  //移除监听器
        unregisterReceiver(onClickReceiver);
    };


    public void changeList() {      //将搜索过的倒过来显示，最近搜索的在上面
        searchContentList2.clear();
        for (int i = searchContentList.size() - 1; i >= 0; i--) {
            searchContentList2.add(searchContentList.get(i));
        }
    }
    public class OnClickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String notification_msg = intent.getAction();
            try {
                if (notification_msg.equals(Search)) {          //edittext 点击搜索进行网络请求
                    String city = intent.getStringExtra("city");
                    String url = preUrl+ URLEncoder.encode(city,"UTF-8")+endUrl;   //将中文转化为字符编码
                    HttpUtils httpUtils = HttpUtils.getInstance();
                    httpUtils.sendHttpRequest(url,WeatherActivity.this);
                }
                if(notification_msg.equals(ChangeCity)){        // 网络请求成功  进行城市切换
                    String address = intent.getStringExtra("address");
                    String address1 =address.replace(preUrl,"");
                    String address2 =address1.replace(endUrl,"");
                    String city = URLDecoder.decode(address2,"UTF-8");
                    tv_city.setText(city+"市");
                    adress3=address;        //储存地址以便手动更新天气可用
                }
                if(notification_msg.equals(Notification)){     //若网络请求不成功，弹出窗口
                    Toast.makeText(context,"搜索内容有误，请重新输入",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void checkRecordSize() {         // 根据是否有搜索记录来判断控件显示
        if (searchContentList.size() == 0) {
            historyView.setVisibility(View.GONE);
        } else {
            historyView.setVisibility(View.VISIBLE);
        }
    }

    public void context(){
        context = WeatherActivity.this;
    }  //获取上下文环境

    public static android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {   //网络请求成功返回数据进行控件更新
            switch (msg.what) {
                case 1:;
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    weatherData = JSONUtils.parseJson(bundle.getString("weather_data"));
                    peatureMatch(weatherData.get(0).getCode_day(),iv_weather);
                    tv_temporature_low.setText(weatherData.get(0).getLow()+"℃");
                    tv_date.setText("日期:"+weatherData.get(0).getDate());
                    tv_temporature_high.setText(weatherData.get(0).getHigh()+"℃");
                    tv_weather.setText("天气:"+weatherData.get(0).getText_day());
                    tv_data1.setText("风力等级:"+weatherData.get(0).getWind_scale());
                    tv_data2.setText("风速:"+weatherData.get(0).getWind_speed());
                    tv_data3.setText("风向:"+weatherData.get(0).getWind_direction());
                    adapter2=new WeatherAdapter(context,weatherData);
                    listView.setAdapter(adapter2);
                    progressBar.setVisibility(View.GONE);
                    break;
            }
        }
    };
        public void screenshot(){
            picture++;
            View view = getWindow().getDecorView().getRootView();   //找到当前页面跟布局
            view.setDrawingCacheEnabled(true);    //设置缓存
            view.buildDrawingCache();
            Bitmap bmp = view.getDrawingCache(); //从缓存中获取当前图片
            if(bmp!=null){
                try{
                    sdCardPath= Environment.getExternalStorageDirectory().getPath();   //获取SD卡内存路径
                    filePath=sdCardPath+ File.separator+"screenshot"+picture+".png";   //获取图片文件路径
                    File file =new File(filePath);         //新建文件
                    FileOutputStream fileOutputStream =new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);  // 压缩图片  这里100表示不压缩
                    fileOutputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
           view.destroyDrawingCache(); //销毁旧的cache 更新cache
        }

      public void share(){
          Intent intent = new Intent(Intent.ACTION_SEND);
          intent.setType("image/png");          //确定数据格式
          File image = new File(filePath);
          Uri uri =Uri.fromFile(image);       //将文件路径转为uri格式
          intent.putExtra(Intent.EXTRA_STREAM,uri);
          startActivity(intent.createChooser(intent,"分享图片"));  // 应用选择器
      }

    public static void peatureMatch(String string,ImageView iv2){   //根据数据返回特定图片
        if (string.equals("0") )
        { iv2.setImageResource(R.drawable.a0);}
        if (string.equals("1") )
        { iv2.setImageResource(R.drawable.a1);}
        if (string.equals("2") )
        { iv2.setImageResource(R.drawable.a2);}
        if (string.equals("3") )
        { iv2.setImageResource(R.drawable.a3);}
        if (string.equals("4") )
        { iv2.setImageResource(R.drawable.a4);}
        if (string.equals("5") )
        { iv2.setImageResource(R.drawable.a5);}
        if (string.equals("6") )
        { iv2.setImageResource(R.drawable.a6);}
        if (string.equals("7") )
        { iv2.setImageResource(R.drawable.a7);}
        if (string.equals("8") )
        { iv2.setImageResource(R.drawable.a8);}
        if (string.equals("9") )
        { iv2.setImageResource(R.drawable.a9);}
        if (string.equals("10") )
        { iv2.setImageResource(R.drawable.a10);}
        if (string.equals("11") )
        { iv2.setImageResource(R.drawable.a11);}
        if (string.equals("12") )
        { iv2.setImageResource(R.drawable.a12);}
        if (string.equals("13") )
        {iv2.setImageResource(R.drawable.a13);}
        if (string.equals("14") )
        { iv2.setImageResource(R.drawable.a14);}
        if (string.equals("15") )
        { iv2.setImageResource(R.drawable.a15);}
        if (string.equals("16") )
        { iv2.setImageResource(R.drawable.a16);}
        if (string.equals("17") )
        { iv2.setImageResource(R.drawable.a17);}
        if (string.equals("18") )
        { iv2.setImageResource(R.drawable.a18);}
        if (string.equals("19") )
        {iv2.setImageResource(R.drawable.a19);}
        if (string.equals("20") )
        { iv2.setImageResource(R.drawable.a20);}
        if (string.equals("21") )
        { iv2.setImageResource(R.drawable.a21);}
        if (string.equals("22") )
        { iv2.setImageResource(R.drawable.a22);}
        if (string.equals("23") )
        { iv2.setImageResource(R.drawable.a23);}
        if (string.equals("24") )
        { iv2.setImageResource(R.drawable.a24);}
        if (string.equals("25") )
        { iv2.setImageResource(R.drawable.a25);}
        if (string.equals("26") )
        { iv2.setImageResource(R.drawable.a26);}
        if (string.equals("27") )
        { iv2.setImageResource(R.drawable.a27);}
        if (string.equals("28") )
        { iv2.setImageResource(R.drawable.a28);}
        if (string.equals("29") )
        { iv2.setImageResource(R.drawable.a29);}
        if (string.equals("30") )
        { iv2.setImageResource(R.drawable.a30);}
        if (string.equals("31") )
        { iv2.setImageResource(R.drawable.a31);}
        if (string.equals("32") )
        { iv2.setImageResource(R.drawable.a32);}
        if (string.equals("33") )
        { iv2.setImageResource(R.drawable.a33);}
        if (string.equals("34") )
        { iv2.setImageResource(R.drawable.a34);}
        if (string.equals("35") )
        { iv2.setImageResource(R.drawable.a35);}
        if (string.equals("36") )
        { iv2.setImageResource(R.drawable.a36);}
        if (string.equals("37") )
        { iv2.setImageResource(R.drawable.a37);}
        if (string.equals("38") )
        { iv2.setImageResource(R.drawable.a38);}
        if (string.equals("99") )
        { iv2.setImageResource(R.drawable.a99);}
    }
}

