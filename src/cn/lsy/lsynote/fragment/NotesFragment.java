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
		
		//�����߳�������ȡ����
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
				Toast.makeText(getActivity(), "����ʧ�ܣ���Ӧ��=" + msg.arg1,
						Toast.LENGTH_SHORT).show();
				break;

			case MESSAGE_RESPONSE_OK:
				// ��ȡ��Ӧ������
				String responseResult = (String) msg.obj;
				// ������ת��ΪJson����
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
			
			// ��ȡ��ǰ��¼�û�������
			SharedPreferences sp = getActivity().getSharedPreferences("user-info",
					Context.MODE_PRIVATE);
			String userId = sp.getString("id", null);
			String session = sp.getString("session", null);
			
			String notebookId = getArguments().getString("notebookId");
			// �����ύ�Ĳ���
			String params = PARAM_NOTEBOOK_ID + "=" + notebookId;
			// ׼������
			HttpURLConnection conn = null; // ��������
			URL url = null; // ��ַ
			
			try {
				url = new URL(URL_NOTE_LIST + "?" +params);
				conn = (HttpURLConnection) url.openConnection(); // �������ӵĶ�
				conn.setRequestMethod(METHOD_GET); // ������������
				conn.addRequestProperty("Cookie", session);
				
				int responseCode = conn.getResponseCode(); // ��ȡ��Ӧ��
				// �ж���Ӧ��
				if (responseCode != 200) {
					// ����
					Message msg = Message.obtain(); // ��Ȼ���ԣ����ǲ�Ҫ new Message()
					msg.what = MESSAGE_RESPONSE_FAIL; // �����Ϣ������
					msg.arg1 = responseCode; // ����Ӧ���װ����Ϣ��
					handler.sendMessage(msg); // ������Ϣ
					// ���Խ��
					System.out.println("response code = " + responseCode);
				} else {
					// �����ȷ����Ӧ�����ȡ��Ӧ����
					InputStream in = conn.getInputStream();
					InputStreamReader isr = new InputStreamReader(in);
					BufferedReader br = new BufferedReader(isr);
					String responseResult = br.readLine();
					// ���Խ��
					System.out.println("response result = " + responseResult);
					// �ѽ���������߳�
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
