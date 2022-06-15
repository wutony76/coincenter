package com.moblie.coincenter;

import static com.moblie.coincenter.Bottom.*;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.InputStream;


public class MenuBottom {

    private View _menu_bottom;
    LinearLayout btnHome;
    LinearLayout btnMission;
    LinearLayout btnShare;
    LinearLayout btnMember;

    public MenuBottom( LinearLayout lyBottom ){
        _menu_bottom = Manager.getInst().mainActivity.getLayoutInflater().inflate(R.layout.menu_bottom, null);
        lyBottom.addView(_menu_bottom);

        btnHome = _menu_bottom.findViewById(R.id.btn_bottom_home);
        btnMission = _menu_bottom.findViewById(R.id.btn_bottom_mission);
        btnShare = _menu_bottom.findViewById(R.id.btn_bottom_share);
        btnMember = _menu_bottom.findViewById(R.id.btn_bottom_member);

        btnHome.setOnClickListener( homeClickListener );
        //btnMission.setOnClickListener( missionClickListener );
        btnMission.setOnClickListener( SelfView.mission );
        btnShare.setOnClickListener( SelfView.share );
        btnMember.setOnClickListener( memberClickListener );
    }


    // --- check select --- //
    public void select( int selectBtn ){

        LinearLayout selectBottomBtn = null;
        switch (selectBtn){
            case R.string.bottom_home:
                selectBottomBtn = btnHome;
                break;

            case R.string.bottom_mission:
                selectBottomBtn = btnMission;
                break;

            case R.string.bottom_share:
                selectBottomBtn = btnShare;
                break;

            case R.string.bottom_member:
                selectBottomBtn = btnMember;
                break;
        }

        selectBottomBtn.setBackgroundColor(Manager.getInst().mainActivity.getResources().getColor(R.color.teal_200));

    }


    // --- on click listener --- //
    View.OnClickListener homeClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Tools.tttLog( "Goto HOME." );
            if( APP.appView == Constant.VIEW_HOME ) return;
            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_home );
        }
    };

    View.OnClickListener memberClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Tools.tttLog( "Goto MEMBER." );
            if( APP.appView == Constant.VIEW_MEMBER ) return;
            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_member );
        }
    };

    View.OnClickListener missionClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Tools.tttLog( "Goto MISSION." );
            if( APP.appView == Constant.VIEW_MISSION ) return;
            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_mission );
        }
    };




}
