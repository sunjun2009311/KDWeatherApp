package org.jan.kdweather;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SpinnerListAdapter extends BaseAdapter {
	private Context context;
	private List<String> values;

	public SpinnerListAdapter(Context context,List<String> list){
		this.context=context;
		this.values=list;
	}
	@Override
	public int getCount() {
		return values.size();
	}

	@Override
	public Object getItem(int position) {
		return values.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = new TextView(context);
		textView.setText(values.get(position));
		textView.setTextSize(25);
		textView.setTextColor(Color.BLACK);
		return textView;
	}

}
