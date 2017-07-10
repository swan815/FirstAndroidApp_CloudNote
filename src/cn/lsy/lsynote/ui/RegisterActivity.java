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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity implements NoteApi,OnClickListener {
	
	Button btnRegist;
	
	EditText etNewUsername;
	
	EditText etNickName;
	
	EditText etNewPassword;
	
	EditText etConfirmPassword;
	
	TextView tvBackLogin;
	
	Handler handler;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnRegist = (Button) findViewById(R.id.btn_register);
		etNewUsername = (EditText) findViewById(R.id.et_new_username);
		etNewPassword = (EditText) findViewById(R.id.et_new_password);
		etNickName = (EditText) findViewById(R.id.et_nickname);
		etConfirmPassword = (EditText) findViewById(R.id.et_password_confirm);
		tvBackLogin = (TextView) findViewById(R.id.tv_back_login);
		
		btnRegist.setOnClickListener(this);
		tvBackLogin.setOnClickListener(this);
		
		handler = new InnerHandler();
		
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_register:
			//����ע�����߳�
			WorkThread thread = new WorkThread();
			thread.start();
			
			btnRegist.setEnabled(false);
			break;
		case R.id.tv_back_login:
			//���ص�¼ҳ��
			Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
			startActivity(intent);
			break;
		}
		
	}
	
	class InnerHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_RESPONSE_FAIL:
				Toast.makeText(RegisterActivity.this, "����ʧ�ܣ���Ӧ��=" + msg.arg1, Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_RESPONSE_OK:
				String responseResult = (String) msg.obj;
				JSONObject jsonObject = null;
				
				try {
					jsonObject = new JSONObject(responseResult);
					int state = jsonObject.getInt("state");
					
					switch (state) {
					case 0:
						Toast.makeText(RegisterActivity.this, "ע��ɹ���", Toast.LENGTH_SHORT).show();
						//ת����¼ҳ��
						Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
						startActivity(intent);
						finish();
						break;
					case 2:
					case 3:
						// ��ȡ��Ϣ
						String message = jsonObject.getString("message");
						// ��ʾ
						Toast.makeText(RegisterActivity.this, "ע��ʧ�ܣ�" + message + "��", Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			}
			btnRegist.setEnabled(true);
		}
	}
	
	
	static final int MESSAGE_RESPONSE_FAIL = -1;
	static final int MESSAGE_RESPONSE_OK = 1;
	
	class WorkThread extends Thread{
		
		@Override
		public void run() {
			String newusername = etNewUsername.getText().toString();
			String nickname = etNickName.getText().toString();
			String newpassword = etNewPassword.getText().toString();
			String confirmpwd = etConfirmPassword.getText().toString();
			
			ParamBuilder builder = new ParamBuilder();
			builder.add(PARAM_USERNAME, newusername);
			builder.add(PARAM_NICKNAME, nickname);
			builder.add(PARAM_PASSWORD, newpassword);
			builder.add(PARAM_PASSWORD_COMFIRM, confirmpwd);
			
			String params = builder.build();
			System.out.println("Ҫ��������ݣ�"+ params);
			
			//׼������
			HttpURLConnection conn = null;
			URL url = null;
			
			try {
				url = new URL(URL_REGISTER);
				conn = (HttpURLConnection) url.openConnection();
				
				conn.setRequestMethod(METHOD_POST);
				conn.addRequestProperty("Content-Length", params.length()+"");
				conn.addRequestProperty("contentType", "application/x-www-form-urlencoded");
				conn.addRequestProperty("Charset", "UTF-8"); // ���ò������ַ�����
				conn.setDoOutput(true); // ����������������
				
				conn.getOutputStream().write(params.getBytes());
				
				int responseCode = conn.getResponseCode();
				
				if (responseCode != 200) {
					Message msg = Message.obtain(); // ��Ȼ���ԣ����ǲ�Ҫ new Message()
					msg.what = MESSAGE_RESPONSE_FAIL; // �����Ϣ������
					msg.arg1 = responseCode; // ����Ӧ���װ����Ϣ��
					handler.sendMessage(msg); // ������Ϣ
					// ���Խ��
					System.out.println("response code = " + responseCode);
				}else{
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
			}finally{
				if (conn != null) {
					conn.disconnect();
				}
			}
			
			
			
		}
	}
	

	@Override
	protected boolean isHideTitleBar() {
		return false;
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_register;
	}

	

}
