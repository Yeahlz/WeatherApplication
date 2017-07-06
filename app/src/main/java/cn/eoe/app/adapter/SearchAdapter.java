package cn.eoe.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.administrator.weatherapplication.R;
import java.util.List;

/**
 * Created by Administrator on 2017/5/13.
 */

public  class SearchAdapter extends BaseAdapter {     // 搜索记录模块适配器
    private Context context;
    private List<String> recordsList;

    public SearchAdapter(Context context, List<String>recordsList){
        this.context = context;
        this.recordsList = recordsList;
    }

    public int getCount() {
        return recordsList.size();
    }

    public Object getItem(int position) {
        return recordsList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(null == convertView){
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.history_search_record,null);
            viewHolder.city =(TextView)convertView.findViewById(R.id.tv_search_content);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.city.setText(recordsList.get(position));
        return convertView;
    }

    public class ViewHolder{
        TextView city;
    }
}

