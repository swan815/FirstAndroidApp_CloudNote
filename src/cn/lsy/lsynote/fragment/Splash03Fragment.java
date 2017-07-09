package cn.lsy.lsynote.fragment;

import cn.lsy.lsynote.R;
import cn.lsy.lsynote.R.layout;
import cn.lsy.lsynote.ui.LoginActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class Splash03Fragment extends BaseFragment implements OnClickListener {
	
	//����    ���ֶ���
	View view;
	
	Button btnStart;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.splash_03, null);
		
		//��ʼ��
		btnStart = (Button) view.findViewById(R.id.btn_start); 
		
		
		btnStart.setOnClickListener(this);
		
		
		//����
		
		return view;
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(getActivity(),LoginActivity.class);
		startActivity(intent);
		//���ٵ�ǰ��Activity������ֱ�ӻص�����
		getActivity().finish();
		//��ǵ�ǰ�������״�����
		SharedPreferences sp = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean("isFirstRun", false);
		editor.commit();
	}
}
