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
        
      //�ж��Ƿ���ʾ������
        if (isHideTitleBar()) {
        	
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
     		ActionBar actionBar = getActionBar();
     		if (actionBar != null) {
				actionBar.hide();
			}
		}
          
        //������Ҫ��ʾ��layout
        setContentView(getLayoutResId());
    }
    
    /**
     * �Ƿ����ر�����
     * @return
     */
    protected abstract boolean isHideTitleBar();
    /**
     * ��ȡ��Ҫ��ʾ��layout��id
     * @return
     */
    protected abstract int getLayoutResId();


    
}
