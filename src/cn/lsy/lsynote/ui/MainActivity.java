package cn.lsy.lsynote.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import cn.lsy.lsynote.R;
import cn.lsy.lsynote.entity.Directory;
import cn.lsy.lsynote.fragment.DirectoriesFragment;
import cn.lsy.lsynote.util.NoteApi;
import cn.lsy.lsynote.util.ParamBuilder;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements NoteApi,OnClickListener{

	ImageButton ibNewFolder;
	
	ImageButton ibNewNote;
	
	ImageButton ibSort;
	
	FrameLayout flContainer;
	
	FragmentPagerAdapter fragmentPagerAdapter;
	
	TextView tvTitleText;
	
	Handler handler;
	
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ibNewFolder = (ImageButton) findViewById(R.id.ib_new_folder);
        ibNewNote = (ImageButton) findViewById(R.id.ib_new_note);
        ibSort = (ImageButton) findViewById(R.id.ib_sort);
        flContainer = (FrameLayout) findViewById(R.id.fl_container);
        tvTitleText = (TextView) findViewById(R.id.tv_title_text);
        
        ibNewFolder.setOnClickListener(this);
        ibNewNote.setOnClickListener(this);
        ibSort.setOnClickListener(this);
        
        SharedPreferences sp = getSharedPreferences("user-info", MODE_PRIVATE);
        String nickname = sp.getString("nick", null);
        tvTitleText.setText(nickname+"���Ʊʼ�");
        
        handler = new InnerHandler();
        
        fragmentPagerAdapter = new InnerFragmentPagerAdapter(getSupportFragmentManager());
        
        currentFragmentPosition = 0;
        updateFragment();
        
        
        
    }
    
    Object lastFragment;
    int lastFragmentPosition;
    int currentFragmentPosition;
    private void updateFragment(){
    	if (lastFragment != null) {
			fragmentPagerAdapter.destroyItem(flContainer, lastFragmentPosition, lastFragment);
		}
    	
    	Object fragment = fragmentPagerAdapter.instantiateItem(flContainer, currentFragmentPosition);
    	fragmentPagerAdapter.setPrimaryItem(flContainer, 0, fragment);
    	fragmentPagerAdapter.finishUpdate(flContainer);
    	
    	lastFragment = fragment;
    	lastFragmentPosition = currentFragmentPosition;
    }
    
    private class InnerFragmentPagerAdapter extends FragmentPagerAdapter{

		public InnerFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = null;
			switch (i) {
			case 0:
				fragment = new DirectoriesFragment();
				break;

			default:
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 1;
		}
    	
    }

	@Override
	protected boolean isHideTitleBar() {
		return false;
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_main;
	}
	
	// EditText��PopupWindow�е�������������봴�����ļ��е�����
	EditText etNewFolderName;
	// Button��PopupWindow�еİ�ť
	Button btnNewFolder;
	
	static final int MESSAGE_RESPONSE_FAIL = -1;
	static final int MESSAGE_RESPONSE_OK = 1;
	
	private PopupWindow newFolderPopupWindow;
	
	@SuppressLint("HandlerLeak")
	private class InnerHandler extends Handler{
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_RESPONSE_FAIL:
				Toast.makeText(MainActivity.this, "����ʧ�ܣ���Ӧ��=" + msg.arg1,
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
					switch (state) {
					case 1:
						// ��¼���ڣ���session��֤ʧ�ܣ�
						Toast.makeText(MainActivity.this, "�����ļ���ʧ�ܣ�" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(MainActivity.this, LoginActivity.class);
						startActivity(intent);
						finish();
						break;

					case 0:
						// ��ӳɹ�������PopupWindow
						etNewFolderName.setText(null);
						newFolderPopupWindow.dismiss();
						// ʹ��Toast��ʾ������Ӧ�ø�Ϊˢ���б�
						Toast.makeText(MainActivity.this, "�����ļ��гɹ���", Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	private class WorkThread extends Thread {
		@Override
		public void run() {
			// ��ȡ�û�������µ��ļ��е�����
			String name = etNewFolderName.getText().toString();
			// ��ȡ��ǰ��¼�û�������
			SharedPreferences sp = getSharedPreferences("user-info",
					MODE_PRIVATE);
			String userId = sp.getString("id", null);
			String session = sp.getString("session", null);

			// �����ύ�Ĳ���
			ParamBuilder builder = new ParamBuilder();
			builder.add(PARAM_USER_ID, userId);
			builder.add(PARAM_DIRECTORY_NAME, name);
			String params = builder.build();
			System.out.println("�ύ�����ݣ�" + params);

			// ׼������
			HttpURLConnection conn = null; // ��������
			URL url = null; // ��ַ




			try {
				url = new URL(URL_DIRECTORY_ADD); // ������ַ����
				conn = (HttpURLConnection) url.openConnection(); // �������ӵĶ���
				
				conn.setRequestMethod(METHOD_POST); // ������������
				conn.addRequestProperty("Content-Length", params.length() + ""); // ���ò��������ݳ���
				conn.addRequestProperty("contentType",
						"application/x-www-form-urlencoded"); // ���ò���������
				conn.addRequestProperty("Charset", "UTF-8"); // ���ò������ַ�����
				conn.addRequestProperty("Cookie", session);
				conn.setDoOutput(true); // ����������������

				conn.getOutputStream().write(params.getBytes()); // ����������������������ύ�û���������

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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_new_folder:
			WorkThread thread = new WorkThread();
			thread.start();
			break;

		case R.id.ib_new_folder:
			// �жϣ�����popupWindow������ʱ������
			if (newFolderPopupWindow == null) {
				// ����popup����ͼ
				View contentView = getLayoutInflater().inflate(
						R.layout.popup_new_folder, null);
				// ��ȡpopup����ͼ�еı�Ҫ�ؼ�
				etNewFolderName = (EditText) contentView
						.findViewById(R.id.et_new_folder_name);
				btnNewFolder = (Button) contentView
						.findViewById(R.id.btn_new_folder);
				// ����popup����ͼ�еĿؼ�
				btnNewFolder.setOnClickListener(this);
				// ����popup����
				newFolderPopupWindow = new PopupWindow();
				// ����popup��[��Ҫ] ���
				newFolderPopupWindow.setWidth((int) (getResources()
						.getDisplayMetrics().widthPixels * 0.8));
				// ����popup��[��Ҫ] �߶�
				newFolderPopupWindow
						.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
				// ����popup��[��Ҫ] ����
				newFolderPopupWindow.setContentView(contentView);
				// ����popup�������ý���
				newFolderPopupWindow.setFocusable(true);
				// ����popup���������ⲿ
				newFolderPopupWindow.setOutsideTouchable(true);
				// ����popup��������ɫ
				int color = Color.parseColor("#ffffff");
				ColorDrawable drawable = new ColorDrawable(color);
				newFolderPopupWindow.setBackgroundDrawable(drawable);
				// ����popup������ʧʱȡ������
				newFolderPopupWindow.setOnDismissListener(new OnDismissListener(){

					@Override
					public void onDismiss() {
						unmaskWindow();
						
					}
					
				});
			}
			// ��ʾ
			newFolderPopupWindow.showAtLocation(flContainer, Gravity.CENTER, 0,
					0);
			// �����汳����������
			maskWindow();
			break;

		case R.id.ib_new_note:
		case R.id.ib_sort:
			Toast.makeText(MainActivity.this, "�ù������ڿ����У������ڴ�����",
					Toast.LENGTH_SHORT).show();
			break;
		}

	}
	
	private void unmaskWindow() {
		setWindowMask(1);
	}

	private void maskWindow() {
		setWindowMask(0.4f);
	}

	private void setWindowMask(float alpha) {
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.alpha = alpha;
		getWindow().setAttributes(layoutParams);
	}


    
}
