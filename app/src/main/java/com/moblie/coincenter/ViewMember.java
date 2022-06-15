package com.moblie.coincenter;

import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.moblie.coincenter.Http.Callback;

import org.json.JSONException;
import org.json.JSONObject;


public class ViewMember {
    public View _view;

    public LinearLayout lyTop;
    public LinearLayout lyBottom;

    public ViewMember(View view) {
        //isCanClick = true;
        _view = view;


        //--- set view title ---//
        lyTop = _view.findViewById(R.id.ly_top);
        new MenuTop(lyTop).setTitle(
            Manager.getInst().mainActivity.getResources().getString(R.string.top_my)
        );


        //--- bottom btn group ---//
        lyBottom = _view.findViewById(R.id.ly_bottom);
        new MenuBottom(lyBottom).select( Bottom.MEMBER );


        //Button btn_share_copy = _view.findViewById(R.id.btn_share_copy);
        //btn_share_copy.setOnClickListener( copyClickListener );


        LinearLayout btnTeamReport = _view.findViewById(R.id.ly_member_team);
        LinearLayout btnMyReport = _view.findViewById(R.id.ly_member_report);
        LinearLayout btnElist = _view.findViewById(R.id.ly_member_elist);
        LinearLayout btnBswitch = _view.findViewById(R.id.ly_member_bswitch);
        LinearLayout btnShare = _view.findViewById(R.id.ly_member_share);
        LinearLayout btnChangePassword = _view.findViewById(R.id.ly_member_edit_password);
        //LinearLayout btnDeleteAccount = _view.findViewById(R.id.ly_member_delete_account);
        LinearLayout btnLogout = _view.findViewById(R.id.ly_member_logout);


        btnTeamReport.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if( APP.appView == Constant.VIEW_REPORT_TEAM ) return;
                new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_report_team );
            }
        });
        btnMyReport.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if( APP.appView == Constant.VIEW_REPORT_TEAM ) return;
                new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_report_my );
            }
        });
        btnElist.setOnClickListener(
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( APP.appView == Constant.VIEW_ELIST ) return;
                    new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_elist );
                }
            }
        );
        btnBswitch.setOnClickListener( SelfView.bswitch );
        btnShare.setOnClickListener( SelfView.share );
        btnChangePassword.setOnClickListener(
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( APP.appView == Constant.VIEW_CHANGE_PASSWORD ) return;
                    new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_change_password );
                }
            }
        );

        btnLogout.setOnClickListener( SelfView.logout );






        APP.appView = Constant.VIEW_MEMBER;

        /**
         * --- start conn to serv to get data. ---
         *

        try{
            // --- get share_friend --- //
            new Http(
                UrlCommand.API_SHARE,
                new JSONObject(),
                new Callback(){
                    @Override
                    public void callback(JSONObject res) {
                        Tools.tttLog(UrlCommand.API_SHARE +" callback = " + res.toString());

                        if(Tools.httpErr(res)) {
                            return;
                        }
                        //ui_thread_update( res );
                    }

                    @Override
                    public void callback(String res) {}
                }
            ).start();

        }catch (Exception e){
            Tools.tttLog( "Err. " + e.toString() );
        }
         */
    }


    //--- ui_update ---//
    private void ui_thread_update(JSONObject _res){
        Manager.getInst().mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //--- 處理資料 ---//
                    JSONObject _data = _res.getJSONObject("data");
                    String _url = _data.getString("share_url");
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap qrImg = encoder.encodeBitmap(
                        _url,
                        BarcodeFormat.QR_CODE,250,250);


                    //--- init ui ---//
                    TextView tvShareUrl = _view.findViewById(R.id.tv_share_url);
                    ImageView imgShareQR = _view.findViewById(R.id.img_share_QR);

                    //--- 設定ui ---//
                    tvShareUrl.setText(_url);
                    imgShareQR.setImageBitmap(qrImg);


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }
    //--- ui_update end. ---//




}


