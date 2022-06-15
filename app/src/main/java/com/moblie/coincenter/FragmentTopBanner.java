package com.moblie.coincenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


public class FragmentTopBanner extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // 在這個方法中取得並定義Fragment的介面元件
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause()
    {
        // 當Fragment要從螢幕消失時執行此方法
        super.onPause();
    }



}
