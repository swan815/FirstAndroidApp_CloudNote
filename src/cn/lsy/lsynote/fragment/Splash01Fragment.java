package cn.lsy.lsynote.fragment;

import cn.lsy.lsynote.R;
import cn.lsy.lsynote.R.layout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Splash01Fragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.splash_01, null);
		return view;
	}
}
