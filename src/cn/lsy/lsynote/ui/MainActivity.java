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
        tvTitleText.setText(nickname+"的云笔记");
        
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
	
	// EditText：PopupWindow中的输入框，用于输入创建的文件夹的名字
	EditText etNewFolderName;
	// Button：PopupWindow中的按钮
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
				Toast.makeText(MainActivity.this, "请求失败！响应码=" + msg.arg1,
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
					switch (state) {
					case 1:
						// 登录过期，即session验证失败！
						Toast.makeText(MainActivity.this, "创建文件夹失败！" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(MainActivity.this, LoginActivity.class);
						startActivity(intent);
						finish();
						break;

					case 0:
						// 添加成功，则处理PopupWindow
						etNewFolderName.setText(null);
						newFolderPopupWindow.dismiss();
						// 使用Toast提示，后续应该改为刷新列表
						Toast.makeText(MainActivity.this, "创建文件夹成功！", Toast.LENGTH_SHORT).show();
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
			// 获取用户输入的新的文件夹的名称
			String name = etNewFolderName.getText().toString();
			// 获取当前登录用户的数据
			SharedPreferences sp = getSharedPreferences("user-info",
					MODE_PRIVATE);
			String userId = sp.getString("id", null);
			String session = sp.getString("session", null);

			// 处理提交的参数
			ParamBuilder builder = new ParamBuilder();
			builder.add(PARAM_USER_ID, userId);
			builder.add(PARAM_DIRECTORY_NAME, name);
			String params = builder.build();
			System.out.println("提交的数据：" + params);

			// 准备联网
			HttpURLConnection conn = null; // 网络连接
			URL url = null; // 网址




			try {
				url = new URL(URL_DIRECTORY_ADD); // 创建网址对象
				conn = (HttpURLConnection) url.openConnection(); // 创建连接的对象
				
				conn.setRequestMethod(METHOD_POST); // 设置请求类型
				conn.addRequestProperty("Content-Length", params.length() + ""); // 配置参数：数据长度
				conn.addRequestProperty("contentType",
						"application/x-www-form-urlencoded"); // 配置参数：类型
				conn.addRequestProperty("Charset", "UTF-8"); // 配置参数：字符编码
				conn.addRequestProperty("Cookie", session);
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
			// 判断，仅当popupWindow不存在时创建它
			if (newFolderPopupWindow == null) {
				// 加载popup的视图
				View contentView = getLayoutInflater().inflate(
						R.layout.popup_new_folder, null);
				// 获取popup的视图中的必要控件
				etNewFolderName = (EditText) contentView
						.findViewById(R.id.et_new_folder_name);
				btnNewFolder = (Button) contentView
						.findViewById(R.id.btn_new_folder);
				// 配置popup的视图中的控件
				btnNewFolder.setOnClickListener(this);
				// 创建popup对象
				newFolderPopupWindow = new PopupWindow();
				// 配置popup：[必要] 宽度
				newFolderPopupWindow.setWidth((int) (getResources()
						.getDisplayMetrics().widthPixels * 0.8));
				// 配置popup：[必要] 高度
				newFolderPopupWindow
						.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
				// 配置popup：[必要] 内容
				newFolderPopupWindow.setContentView(contentView);
				// 配置popup：允许获得焦点
				newFolderPopupWindow.setFocusable(true);
				// 配置popup：允许点击外部
				newFolderPopupWindow.setOutsideTouchable(true);
				// 配置popup：背景颜色
				int color = Color.parseColor("#ffffff");
				ColorDrawable drawable = new ColorDrawable(color);
				newFolderPopupWindow.setBackgroundDrawable(drawable);
				// 配置popup：当消失时取消遮罩
				newFolderPopupWindow.setOnDismissListener(new OnDismissListener(){

					@Override
					public void onDismiss() {
						unmaskWindow();
						
					}
					
				});
			}
			// 显示
			newFolderPopupWindow.showAtLocation(flContainer, Gravity.CENTER, 0,
					0);
			// 将界面背景设置遮罩
			maskWindow();
			break;

		case R.id.ib_new_note:
		case R.id.ib_sort:
			Toast.makeText(MainActivity.this, "该功能尚在开发中，敬请期待……",
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
