package com.way.chat.activity;

import java.util.LinkedList;

import com.way.chat.common.bean.SeekInfoEntity;
import com.way.chat.common.util.Constants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.way.chat.activity.AsyncViewTask;

public class SeekInfoAdapter extends BaseAdapter{
	private Context context;
	private LinkedList<SeekInfoEntity> mData;
	
	public SeekInfoAdapter(Context context, LinkedList<SeekInfoEntity> mData){
		this.mData = mData;
		this.context = context;
	}
	
	@Override
	public int getCount(){
		return mData.size();
	}
	
	@Override
	public Object getItem(int position){
		return mData.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.seek_info_item, parent, false);
			holder = new ViewHolder();
			holder.img_icon = (ImageView) convertView.findViewById(R.id.imgicon);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.address = (TextView) convertView.findViewById(R.id.address);
			holder.says = (TextView) convertView.findViewById(R.id.says);
			convertView.setTag(holder); 
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		//holder.img_icon.setBackgroundResource(mData.get(position).getImg());
		
		holder.img_icon.setTag("http://" + Constants.SERVER_IP + "/pic/" + (mData.get(position).getImg() + ".png"));
		new AsyncViewTask().execute(holder.img_icon);//异步加载图片
		
		holder.name.setText(mData.get(position).getName());
		holder.address.setText(mData.get(position).getAddress());
		holder.says.setText(mData.get(position).getSays());
		return convertView;
	}
	
	static class ViewHolder{
		ImageView img_icon;
		TextView name;
		TextView address;
		TextView says;
	}
}
