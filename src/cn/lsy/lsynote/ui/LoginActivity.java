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
	// 登录按钮
	Button btnLogin;
	// EditText：用户名的输入框
	EditText etUsername;
	// EditText：密码的输入框
	EditText etPassword;
	// TextView：注册账号
	TextView tvRegister;
	// TextView：找回密码
	TextView tvFindPassword;
	// 消息的发送者与处理者
	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 初始化控件
		btnLogin = (Button) findViewById(R.id.btn_login);
		etUsername = (EditText) findViewById(R.id.et_username);
		etPassword = (EditText) findViewById(R.id.et_password);
		tvRegister = (TextView) findViewById(R.id.tv_register);
		tvFindPassword = (TextView) findViewById(R.id.tv_find_password);

		// 为控件配置监听器
		btnLogin.setOnClickListener(this);
		tvRegister.setOnClickListener(this);
		tvFindPassword.setOnClickListener(this);
		
		// 创建Handler对象
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
			// 创建并开启子线程，让子线程完成网络请求，并获取结果
			WorkThread thread = new WorkThread();
			thread.start();
			// 禁用登录按钮
			btnLogin.setEnabled(false);
			break;

		case R.id.tv_register:
			// 跳转到注册界面
			Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
			break;
			
		case R.id.tv_find_password:
			// 找回密码，功能还没做呢
			Toast.makeText(LoginActivity.this, "该功能尚在开发中，敬请期待……", Toast.LENGTH_SHORT).show();
			break;
		}
		
	}
	
	@SuppressLint("HandlerLeak") 
	class InnerHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_RESPONSE_FAIL:
				Toast.makeText(LoginActivity.this, "请求失败！响应码=" + msg.arg1, Toast.LENGTH_SHORT).show();
				break;

			case MESSAGE_RESPONSE_OK:
				// 获取响应的正文
				String responseResult = (String) msg.obj;
				// 将正文转换为Json对象
				JSONObject jsonObject = null; 
				
				try {
					// 将正文转换为Json对象
					jsonObject = new JSONObject(responseResult);
					// 获取状态码
					int state = jsonObject.getInt("state");
					// 判断状态码
					switch (state) {
					case 0:
						// 保存登录用户的信息，以便于后续的界面访问时使用
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
						
						// 提示
						Toast.makeText(LoginActivity.this, "登录成功！",
								Toast.LENGTH_SHORT).show();
						// 跳转到MainActivity
						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(intent);
						// 把当前Activity销毁
						finish();
						break;

					case 2:
					case 3:
						// 获取消息
						String message = jsonObject.getString("message");
						// 提示
						Toast.makeText(LoginActivity.this, "登录失败！" + message + "！", Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
			// 启用登录按钮
			btnLogin.setEnabled(true);
		}
	}
	
	static final int MESSAGE_RESPONSE_FAIL = -1;
	static final int MESSAGE_RESPONSE_OK = 1;
	String session;

	class WorkThread extends Thread {
		@Override
		public void run() {
			// 获取用户在输入框中输入的用户名和密码
			String username = etUsername.getText().toString();
			String password = etPassword.getText().toString();
			
			// 将用户输入的数据转换成例如这样的格式：username=chengheng&passwod=123456&nickname=chengheng
			ParamBuilder builder = new ParamBuilder();
			builder.add(PARAM_USERNAME, username);
			builder.add(PARAM_PASSWORD, password);
			String params = builder.build();
			System.out.println("提交的数据：" + params);
			
			// 准备联网
			HttpURLConnection conn = null; // 网络连接
			URL url = null; // 网址
			
			try {
				url = new URL(URL_LOGIN); // 创建网址对象
				conn = (HttpURLConnection) url.openConnection(); // 创建连接的对象
				
				conn.setRequestMethod(METHOD_POST); // 设置请求类型
				conn.addRequestProperty("Content-Length", params.length() + ""); // 配置参数：数据长度
				conn.addRequestProperty("contentType", "application/x-www-form-urlencoded"); // 配置参数：类型
				conn.addRequestProperty("Charset", "UTF-8"); // 配置参数：字符编码
				conn.setDoOutput(true); // 配置向服务输出数据
				
				conn.getOutputStream().write(params.getBytes()); // 向服务器输出请求参数，即提交用户名和密码
				
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
					
					// 获取Sesstion信息
					String headerCookie = conn.getHeaderField("Set-Cookie");
					session = headerCookie.split(";")[0];
					System.out.println("response ok, session data = " + session);
					
					
					// 测试结果
					System.out.println("response result = " + responseResult);
					// 把结果发给主线程
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
