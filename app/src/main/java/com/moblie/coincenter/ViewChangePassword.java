package com.moblie.coincenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class ViewChangePassword {
    public View _view;

    public EditText edPhone ;
    public EditText edPassword ;
    public EditText edPassword2 ;

    public Button btnCheckPhone;

    MainActivity thisActivity = Manager.getInst().mainActivity;
    Boolean isReSpend = true;

    public LinearLayout lyTop;
    public MenuTop menuTop;
    public ViewChangePassword(View view) {
        _view = view;

        //--- set view title ---//
        lyTop = _view.findViewById(R.id.ly_top);
        menuTop = new MenuTop(lyTop);
        menuTop.setTitle( thisActivity.getResources().getString(R.string.top_change_password) );
        ImageView backBtn = menuTop.getBackBtn();
        Func.topBack(backBtn);
        //--- set view title end.  ---//


        edPhone = _view.findViewById(R.id.ed_chpass_phone);
        edPassword = _view.findViewById(R.id.ed_chpass_password);
        edPassword2 = _view.findViewById(R.id.ed_chpass_password2);

        btnCheckPhone = _view.findViewById(R.id.btn_chpass_checkphone);
        Button btnChpassOk = _view.findViewById(R.id.btn_chpass_ok);

        btnCheckPhone.setOnClickListener( clickSpendPhoneCheck );
        btnChpassOk.setOnClickListener( clickChangePassListener );

        APP.appView = Constant.VIEW_CHANGE_PASSWORD;
    }

    public Integer nowTime = 0;
    private void checkDelayTime( Integer delayTime ){
        Integer sleepTime = 1000;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if( nowTime == delayTime ) {
                    isReSpend = true;
                    thisActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnCheckPhone.setText(
                                Manager.getInst().mainActivity.getResources().getString(R.string.register_btn_checkphone)
                            );
                        }
                    });

                }else{
                    nowTime ++;
                    thisActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnCheckPhone.setText(
                                String.format("%s 秒", nowTime)
                            );
                        }
                    });
                    checkDelayTime(  delayTime);
                }
            }
        }, sleepTime);
    }



    View.OnClickListener loginClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e(Constant.TONY_TAG, "click to login." );
            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_login );
        }
    };



    //--- change password ---//
    View.OnClickListener clickChangePassListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText edSMSCheckCode = _view.findViewById(R.id.ed_chpass_phone_checkcode);

            String strPhone = edPhone.getText().toString();
            String strPass = edPassword.getText().toString();
            String strPass2 = edPassword2.getText().toString();
            String strPhoneCode = edSMSCheckCode.getText().toString();


            if( strPhone.length() != 10 ){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.register_err_phone)
                );
                return;
            }
            if( strPass.length() < 5 || strPass.length() < 5 ){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.register_err_pass)
                );
                return;
            }

            if( strPass != strPass2 ){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.change_password_again_check)
                );
                return;
            }


            // --- http post --- //
            try {
                //isCanClick = false;
                JSONObject postData = new JSONObject();
                postData.put("phone_number", strPhone);
                postData.put("password", strPass);
                postData.put("password_confirm", strPass2);
                postData.put("phone_check_code", strPhoneCode);

                new Http(
                    UrlCommand.API_CHANGE_PASSWORD,
                    postData,

                    new Http.Callback(){
                        @Override
                        public void callback(JSONObject res) {
                            try{
                                Tools.tttLog(UrlCommand.API_CHANGE_PASSWORD + ", callback = " + res.toString());

                                // --- res err pross --- //
                                if(Tools.httpErr(res)) {
                                    return;
                                }
                                Manager.getInst().sysToast(
                                    thisActivity.getResources().getString(R.string.change_password_success)
                                );

                                new Actions( thisActivity ).changeView( Constant.ly_member );

                            }catch (Exception e){}
                            //isCanClick = true;
                        }

                        @Override
                        public void callback(String res) {

                        }
                    }

                ).start();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    //--- change password end. ---//

    //--- spend phone ---//
    View.OnClickListener clickSpendPhoneCheck = new Button.OnClickListener() {
        public void onClick(View v) {
            if( isReSpend == false ) {
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.register_phone_respend)
                );
                return;
            }


            EditText ed_phone = _view.findViewById(R.id.ed_chpass_phone);
            String strPhone = ed_phone.getText().toString();

            if (strPhone.length() != 10){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.register_phone_err)
                );
            }


            try{
                JSONObject postData = new JSONObject();
                postData.put("number", strPhone);
                new Http(
                    UrlCommand.API_PHONE_SPEND,
                    postData,
                    new Http.Callback(){
                        @Override
                        public void callback(JSONObject res) {

                            try{
                                Tools.tttLog(UrlCommand.API_PHONE_SPEND +" callback = " + res.toString());
                                if(Tools.httpErr(res)) {
                                    return;
                                }

                                Manager.getInst().sysToast(
                                        Manager.getInst().mainActivity.getResources().getString(R.string.register_phone_spend)
                                );


                                JSONObject _data = res.getJSONObject("data");
                                Integer delayTime = _data.getInt("next_sec");
                                isReSpend = false;                //spend btn 開關
                                nowTime = 0;
                                checkDelayTime(delayTime);

                            }catch (Exception e){}
                        }

                        @Override
                        public void callback(String res) {}
                    }
                ).start();
            }catch (Exception e){}
        }
    };
    //--- spend phone end. ---//
}

