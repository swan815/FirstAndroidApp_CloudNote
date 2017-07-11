package cn.lsy.lsynote;

import java.util.ArrayList;
import java.util.List;

import cn.lsy.lsynote.entity.Directory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DirectoryAdapter extends BaseAdapter {
	
	private Context context;
	private List<Directory> directories;
	
	
 
	public DirectoryAdapter(Context context, List<Directory> directories) {
		super();
		this.context = context;
		if (directories == null) {
			directories = new ArrayList<Directory>();
		}
		
		this.directories = directories;
	}

	@Override
	public int getCount() {
		return directories.size();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		Directory dir = directories.get(position);
		
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_directory, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_directory_item_name);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvName.setText(dir.getName());
		
		return convertView;
	}
	
	class ViewHolder{
		TextView tvName;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	

}
