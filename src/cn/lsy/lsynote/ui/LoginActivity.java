package cn.lsy.lsynote.ui;

import cn.lsy.lsynote.R;
import cn.lsy.lsynote.R.layout;
import cn.lsy.lsynote.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	protected boolean isHideTitleBar() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.activity_login;
	}



}
