package com.example.appdating.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appdating.R;

import java.util.List;
                                //nếu không có cái này thì ở super nó báo lỗi
public class arrayAdapter extends ArrayAdapter<cards> {
    Context context;

    public  arrayAdapter(Context context, int resourceId, List<cards> items) {
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        cards card_item = getItem(position);
        if(convertView ==  null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        TextView name = convertView.findViewById(R.id.name);
        ImageView image = convertView.findViewById(R.id.image);

        name.setText(card_item.getName());
        //cho hình mặc định
        image.setImageResource(R.mipmap.ic_launcher);
        return convertView;
    }
}
