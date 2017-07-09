package cn.lsy.lsynote.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
      //判断是否不显示标题栏
        if (isHideTitleBar()) {
        	
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
     		ActionBar actionBar = getActionBar();
     		if (actionBar != null) {
				actionBar.hide();
			}
		}
          
        //设置需要显示的layout
        setContentView(getLayoutResId());
    }
    
    /**
     * 是否隐藏标题栏
     * @return
     */
    protected abstract boolean isHideTitleBar();
    /**
     * 活取需要显示的layout的id
     * @return
     */
    protected abstract int getLayoutResId();


    
}
