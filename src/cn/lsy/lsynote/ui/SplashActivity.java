package cn.lsy.lsynote.ui;

import cn.lsy.lsynote.R;
import cn.lsy.lsynote.fragment.Splash01Fragment;
import cn.lsy.lsynote.fragment.Splash02Fragment;
import cn.lsy.lsynote.fragment.Splash03Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class SplashActivity extends BaseActivity {
	
	ViewPager vpPages;
	
	FragmentPagerAdapter adapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//读取偏好设置，判断当前是否是首次运行,
		//如果是，直接跳转到登录界面
		SharedPreferences sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
		boolean isFirstRun =  sp.getBoolean("isFirstRun", true);
		if (!isFirstRun) {
			Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
			startActivity(intent);
			finish();
		}
		
		vpPages = (ViewPager) findViewById(R.id.vp_pages);
		
		adapter = new InnerFragmentPagerAdapter(getSupportFragmentManager());
		
		vpPages.setAdapter(adapter);
		
		
	}
	
	class InnerFragmentPagerAdapter extends FragmentPagerAdapter{

		public InnerFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = new Splash01Fragment();
				break;
			case 1:
				fragment = new Splash02Fragment();
				break;
			case 2:
				fragment = new Splash03Fragment();
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}
		
		
	}

	@Override
	protected boolean isHideTitleBar() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int getLayoutResId() {
		
		return R.layout.activity_splash;
	}

	

}
