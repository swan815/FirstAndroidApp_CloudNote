package cn.lsy.lsynote.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.lsy.lsynote.DirectoryAdapter;
import cn.lsy.lsynote.NoteAdapter;
import cn.lsy.lsynote.R;
import cn.lsy.lsynote.entity.Directory;
import cn.lsy.lsynote.entity.Note;
import cn.lsy.lsynote.util.NoteApi;

public class NotesFragment extends BaseFragment implements NoteApi {
	
	View view;
	
	ListView lvNotes;
	TextView tvEmptyView;
	List<Note> notes;
	
	NoteAdapter adapter;
	
	Handler handler;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_note_list, null);
		
		
		
		lvNotes = (ListView) view.findViewById(R.id.lv_notes);
		tvEmptyView = (TextView) view.findViewById(R.id.tv_empty_notesview);
		
		//String notebookId = getArguments().getString("notebookId");
		
		//System.out.println(notebookId);
		
		
		
		notes = new ArrayList<Note>();
		adapter = new NoteAdapter(getActivity(), notes); 
		lvNotes.setAdapter(adapter);
		lvNotes.setEmptyView(tvEmptyView);
		
		handler = new InnerHandler();
		
		//开启线程联网获取数据
		WorkThread thread = new WorkThread();
		thread.start();
		
		
		
		return view;
	}
	
	@SuppressLint("HandlerLeak")
	private class InnerHandler extends Handler{
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_RESPONSE_FAIL:
				Toast.makeText(getActivity(), "请求失败！响应码=" + msg.arg1,
						Toast.LENGTH_SHORT).show();
				break;

			case MESSAGE_RESPONSE_OK:
				// 获取响应的正文
				String responseResult = (String) msg.obj;
				// 将正文转换为Json对象
				JSONObject jsonObject = null;
				
				try {
					jsonObject = new JSONObject(responseResult);
					int state = jsonObject.getInt("state");
					if (state == 0) {
						JSONArray dataArray = jsonObject.getJSONArray("data");
						for (int i = 0; i < dataArray.length(); i++) {
							JSONObject dirObject = dataArray.getJSONObject(i);
							String title = dirObject.getString("title");
							String id = dirObject.getString("id");
							
							Note dir = new Note();
							dir.setId(id);
							dir.setTitle(title);
							
							notes.add(dir);

						}
						
						adapter.notifyDataSetChanged();
					}
	
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
				
		}
		
	}
	
	static final int MESSAGE_RESPONSE_FAIL = -1;
	static final int MESSAGE_RESPONSE_OK = 1;
	
	private class WorkThread extends Thread{
		
		@Override
		public void run() {
			
			// 获取当前登录用户的数据
			SharedPreferences sp = getActivity().getSharedPreferences("user-info",
					Context.MODE_PRIVATE);
			String userId = sp.getString("id", null);
			String session = sp.getString("session", null);
			
			String notebookId = getArguments().getString("notebookId");
			// 处理提交的参数
			String params = PARAM_NOTEBOOK_ID + "=" + notebookId;
			// 准备联网
			HttpURLConnection conn = null; // 网络连接
			URL url = null; // 网址
			
			try {
				url = new URL(URL_NOTE_LIST + "?" +params);
				conn = (HttpURLConnection) url.openConnection(); // 创建连接的对
				conn.setRequestMethod(METHOD_GET); // 设置请求类型
				conn.addRequestProperty("Cookie", session);
				
				int responseCode = conn.getResponseCode(); // 获取响应码
				// 判断响应码
				if (responseCode != 200) {
					// 出错
					Message msg = Message.obtain(); // 虽然可以，但是不要 new Message()
					msg.what = MESSAGE_RESPONSE_FAIL; // 标记消息的类型
					msg.arg1 = responseCode; // 将响应码封装在消息中
					handler.sendMessage(msg); // 发出消息
					// 测试结果
					System.out.println("response code = " + responseCode);
				} else {
					// 获得正确的响应，则读取响应数据
					InputStream in = conn.getInputStream();
					InputStreamReader isr = new InputStreamReader(in);
					BufferedReader br = new BufferedReader(isr);
					String responseResult = br.readLine();
					// 测试结果
					System.out.println("response result = " + responseResult);
					// 把结果发给主线程
					Message msg = Message.obtain();
					msg.what = MESSAGE_RESPONSE_OK;
					msg.obj = responseResult;
					handler.sendMessage(msg);
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			super.run();
		}
		
		}
}
