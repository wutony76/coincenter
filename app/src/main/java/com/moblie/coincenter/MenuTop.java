package com.moblie.coincenter;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MenuTop {

    private View _menu_top;
    TextView tvTitle;
    ImageView btnBack;

    LinearLayout btnHome;
    LinearLayout btnMission;
    LinearLayout btnShare;
    LinearLayout btnMember;

    public MenuTop(LinearLayout lyTop ){
        _menu_top = Manager.getInst().mainActivity.getLayoutInflater().inflate(R.layout.menu_top, null);
        lyTop.addView(_menu_top);

        tvTitle = _menu_top.findViewById(R.id.tv_member_id);
        btnBack = _menu_top.findViewById(R.id.img_back_top);


        ImageView btnCustomer =  _menu_top.findViewById(R.id.btn_customer);
        btnCustomer.setOnClickListener( clickCustomerListener );
    }

    public void setTitle( String title){
        tvTitle.setText( title );
    }

    public void showBack(){
        btnBack.setVisibility(View.VISIBLE);
    }

    public ImageView getBackBtn(){
        btnBack.setVisibility(View.VISIBLE);
        return btnBack;
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
            Tools.tttLog( "Goto HOME." );
            if( APP.appView == Constant.VIEW_MEMBER ) return;
            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_member );
        }
    };


    View.OnClickListener clickCustomerListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String lineUrl="https://line.me/ti/g2/1cjeDLthVbjN9Pxn_mPae8t166W6oaFISW4LDQ?utm_source=invitation&utm_medium=link_copy&utm_campaign=default";
            Uri uri = Uri.parse(lineUrl);           //要跳轉的網址
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            Manager.getInst().mainActivity.startActivity( intent );
        }
    };




}
