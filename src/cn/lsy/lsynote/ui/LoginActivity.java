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
import cn.lsy.lsynote.util.NoteApi;
import cn.lsy.lsynote.util.ParamBuilder;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements NoteApi, OnClickListener {
	// ��¼��ť
	Button btnLogin;
	// EditText���û����������
	EditText etUsername;
	// EditText������������
	EditText etPassword;
	// TextView��ע���˺�
	TextView tvRegister;
	// TextView���һ�����
	TextView tvFindPassword;
	// ��Ϣ�ķ������봦����
	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ��ʼ���ؼ�
		btnLogin = (Button) findViewById(R.id.btn_login);
		etUsername = (EditText) findViewById(R.id.et_username);
		etPassword = (EditText) findViewById(R.id.et_password);
		tvRegister = (TextView) findViewById(R.id.tv_register);
		tvFindPassword = (TextView) findViewById(R.id.tv_find_password);

		// Ϊ�ؼ����ü�����
		btnLogin.setOnClickListener(this);
		tvRegister.setOnClickListener(this);
		tvFindPassword.setOnClickListener(this);
		
		// ����Handler����
		handler = new InnerHandler();
	}

	@Override
	protected boolean isHideTitleBar() {
		return false;
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_login;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_login:
			// �������������̣߳������߳�����������󣬲���ȡ���
			WorkThread thread = new WorkThread();
			thread.start();
			// ���õ�¼��ť
			btnLogin.setEnabled(false);
			break;

		case R.id.tv_register:
			// ��ת��ע�����
			Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
			break;
			
		case R.id.tv_find_password:
			// �һ����룬���ܻ�û����
			Toast.makeText(LoginActivity.this, "�ù������ڿ����У������ڴ�����", Toast.LENGTH_SHORT).show();
			break;
		}
		
	}
	
	@SuppressLint("HandlerLeak") 
	class InnerHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_RESPONSE_FAIL:
				Toast.makeText(LoginActivity.this, "����ʧ�ܣ���Ӧ��=" + msg.arg1, Toast.LENGTH_SHORT).show();
				break;

			case MESSAGE_RESPONSE_OK:
				// ��ȡ��Ӧ������
				String responseResult = (String) msg.obj;
				// ������ת��ΪJson����
				JSONObject jsonObject = null; 
				
				try {
					// ������ת��ΪJson����
					jsonObject = new JSONObject(responseResult);
					// ��ȡ״̬��
					int state = jsonObject.getInt("state");
					// �ж�״̬��
					switch (state) {
					case 0:
						// �����¼�û�����Ϣ���Ա��ں����Ľ������ʱʹ��
						JSONObject data = jsonObject.getJSONObject("data");
						SharedPreferences sp = getSharedPreferences("user-info", MODE_PRIVATE);
						SharedPreferences.Editor editor = sp.edit();
						editor.putString("id", data.getString("id"));
						editor.putString("name", data.getString("name"));
						editor.putString("password", data.getString("password"));
						editor.putString("token", data.getString("token"));
						editor.putString("nick", data.getString("nick"));
						editor.putString("session", session);
						editor.commit();
						
						// ��ʾ
						Toast.makeText(LoginActivity.this, "��¼�ɹ���",
								Toast.LENGTH_SHORT).show();
						// ��ת��MainActivity
						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(intent);
						// �ѵ�ǰActivity����
						finish();
						break;

					case 2:
					case 3:
						// ��ȡ��Ϣ
						String message = jsonObject.getString("message");
						// ��ʾ
						Toast.makeText(LoginActivity.this, "��¼ʧ�ܣ�" + message + "��", Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
			// ���õ�¼��ť
			btnLogin.setEnabled(true);
		}
	}
	
	static final int MESSAGE_RESPONSE_FAIL = -1;
	static final int MESSAGE_RESPONSE_OK = 1;
	String session;

	class WorkThread extends Thread {
		@Override
		public void run() {
			// ��ȡ�û����������������û���������
			String username = etUsername.getText().toString();
			String password = etPassword.getText().toString();
			
			// ���û����������ת�������������ĸ�ʽ��username=chengheng&passwod=123456&nickname=chengheng
			ParamBuilder builder = new ParamBuilder();
			builder.add(PARAM_USERNAME, username);
			builder.add(PARAM_PASSWORD, password);
			String params = builder.build();
			System.out.println("�ύ�����ݣ�" + params);
			
			// ׼������
			HttpURLConnection conn = null; // ��������
			URL url = null; // ��ַ
			
			try {
				url = new URL(URL_LOGIN); // ������ַ����
				conn = (HttpURLConnection) url.openConnection(); // �������ӵĶ���
				
				conn.setRequestMethod(METHOD_POST); // ������������
				conn.addRequestProperty("Content-Length", params.length() + ""); // ���ò��������ݳ���
				conn.addRequestProperty("contentType", "application/x-www-form-urlencoded"); // ���ò���������
				conn.addRequestProperty("Charset", "UTF-8"); // ���ò������ַ�����
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
					
					// ��ȡSesstion��Ϣ
					String headerCookie = conn.getHeaderField("Set-Cookie");
					session = headerCookie.split(";")[0];
					System.out.println("response ok, session data = " + session);
					
					
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
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
		}
	}

}
