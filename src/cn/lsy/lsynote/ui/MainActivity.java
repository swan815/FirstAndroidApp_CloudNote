package cn.lsy.lsynote.ui;

import cn.lsy.lsynote.R;
import cn.lsy.lsynote.R.layout;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

	@Override
	protected boolean isHideTitleBar() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int getLayoutResId() {
		
		return R.layout.activity_main;
	}


    
}
