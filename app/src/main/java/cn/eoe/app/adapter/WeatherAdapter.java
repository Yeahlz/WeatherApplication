package cn.eoe.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.weatherapplication.R;

import java.util.List;

import cn.eoe.app.entity.DataBean;

/**
 * Created by Administrator on 2017/5/13.
 */

public class WeatherAdapter extends BaseAdapter {    // 多天预报适配器
    private Context context;
    private List<DataBean.DailyBean> list;
    public WeatherAdapter(Context context, List<DataBean.DailyBean> list){
        this.context=context;
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size()-1;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView = View.inflate(context, R.layout.manydays_weather,null);
            viewHolder.iv=(ImageView)convertView.findViewById(R.id.imageView);
            viewHolder.tv1=(TextView)convertView.findViewById(R.id.textView);
            viewHolder.tv2=(TextView)convertView.findViewById(R.id.textView2);
            viewHolder.tv5=(TextView)convertView.findViewById(R.id.textView5);
            viewHolder.tv4=(TextView)convertView.findViewById(R.id.textView4);
            viewHolder.tv3=(TextView)convertView.findViewById(R.id.textView3);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder) convertView.getTag();
        }
        peatureMatch(list.get(position+1).getCode_day(),viewHolder);
        viewHolder.tv1.setText(list.get(position+1).getDate());
        viewHolder.tv2.setText(list.get(position+1).getText_day());
        viewHolder.tv5.setText(list.get(position+1).getLow()+"℃");
        viewHolder.tv4.setText(list.get(position+1).getHigh()+"℃");
        viewHolder.tv3.setText("/");
        return convertView;
    }
    public class ViewHolder{
        ImageView iv;
        TextView tv1;
        TextView tv2;
        TextView tv5;
        TextView tv4;
        TextView tv3;
    }
    public static void peatureMatch(String string, ViewHolder viewHolder){
        if (string.equals("0") )
        { viewHolder.iv.setImageResource(R.drawable.a0);}
        if (string.equals("1") )
        { viewHolder.iv.setImageResource(R.drawable.a1);}
        if (string.equals("2") )
        { viewHolder.iv.setImageResource(R.drawable.a2);}
        if (string.equals("3") )
        { viewHolder.iv.setImageResource(R.drawable.a3);}
        if (string.equals("4") )
        { viewHolder.iv.setImageResource(R.drawable.a4);}
        if (string.equals("5") )
        { viewHolder.iv.setImageResource(R.drawable.a5);}
        if (string.equals("6") )
        { viewHolder.iv.setImageResource(R.drawable.a6);}
        if (string.equals("7") )
        { viewHolder.iv.setImageResource(R.drawable.a7);}
        if (string.equals("8") )
        { viewHolder.iv.setImageResource(R.drawable.a8);}
        if (string.equals("9") )
        { viewHolder.iv.setImageResource(R.drawable.a9);}
        if (string.equals("10") )
        { viewHolder.iv.setImageResource(R.drawable.a10);}
        if (string.equals("11") )
        { viewHolder.iv.setImageResource(R.drawable.a11);}
        if (string.equals("12") )
        { viewHolder.iv.setImageResource(R.drawable.a12);}
        if (string.equals("13") )
        { viewHolder.iv.setImageResource(R.drawable.a13);}
        if (string.equals("14") )
        { viewHolder.iv.setImageResource(R.drawable.a14);}
        if (string.equals("15") )
        { viewHolder.iv.setImageResource(R.drawable.a15);}
        if (string.equals("16") )
        { viewHolder.iv.setImageResource(R.drawable.a16);}
        if (string.equals("17") )
        { viewHolder.iv.setImageResource(R.drawable.a17);}
        if (string.equals("18") )
        { viewHolder.iv.setImageResource(R.drawable.a18);}
        if (string.equals("19") )
        { viewHolder.iv.setImageResource(R.drawable.a19);}
        if (string.equals("20") )
        { viewHolder.iv.setImageResource(R.drawable.a20);}
        if (string.equals("21") )
        { viewHolder.iv.setImageResource(R.drawable.a21);}
        if (string.equals("22") )
        { viewHolder.iv.setImageResource(R.drawable.a22);}
        if (string.equals("23") )
        { viewHolder.iv.setImageResource(R.drawable.a23);}
        if (string.equals("24") )
        { viewHolder.iv.setImageResource(R.drawable.a24);}
        if (string.equals("25") )
        { viewHolder.iv.setImageResource(R.drawable.a25);}
        if (string.equals("26") )
        { viewHolder.iv.setImageResource(R.drawable.a26);}
        if (string.equals("27") )
        { viewHolder.iv.setImageResource(R.drawable.a27);}
        if (string.equals("28") )
        { viewHolder.iv.setImageResource(R.drawable.a28);}
        if (string.equals("29") )
        { viewHolder.iv.setImageResource(R.drawable.a29);}
        if (string.equals("30") )
        { viewHolder.iv.setImageResource(R.drawable.a30);}
        if (string.equals("31") )
        { viewHolder.iv.setImageResource(R.drawable.a31);}
        if (string.equals("32") )
        { viewHolder.iv.setImageResource(R.drawable.a32);}
        if (string.equals("33") )
        { viewHolder.iv.setImageResource(R.drawable.a33);}
        if (string.equals("34") )
        { viewHolder.iv.setImageResource(R.drawable.a34);}
        if (string.equals("35") )
        { viewHolder.iv.setImageResource(R.drawable.a35);}
        if (string.equals("36") )
        { viewHolder.iv.setImageResource(R.drawable.a36);}
        if (string.equals("37") )
        { viewHolder.iv.setImageResource(R.drawable.a37);}
        if (string.equals("38") )
        { viewHolder.iv.setImageResource(R.drawable.a38);}
        if (string.equals("99") )
        { viewHolder.iv.setImageResource(R.drawable.a99);}
    }
}
