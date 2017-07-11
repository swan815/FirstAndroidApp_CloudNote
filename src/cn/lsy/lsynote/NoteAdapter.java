package cn.lsy.lsynote;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.lsy.lsynote.entity.Note;

public class NoteAdapter extends BaseAdapter {
	
	private Context context;
	private List<Note> notes;
	
	
 
	public NoteAdapter(Context context, List<Note> notes) {
		super();
		this.context = context;
		if (notes == null) {
			notes = new ArrayList<Note>();
		}
		this.notes = notes;
	}

	@Override
	public int getCount() {
		return notes.size();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		Note dir = notes.get(position);
		
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_note, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_note_item_name);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvName.setText(dir.getTitle());
		
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
