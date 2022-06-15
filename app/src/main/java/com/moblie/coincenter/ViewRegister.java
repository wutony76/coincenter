package com.moblie.coincenter;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Date;


public class ViewRegister {
    public View _view;
    public Button regbtnLogin;
    public Button btnCheckphone;
    public Button btnRegister;

    public EditText edAccount;
    public EditText edName;
    public EditText edPhone ;
    //public EditText edSMSCheckCode;
    public EditText edPassword ;
    public EditText edSafePassword ;
    public EditText edInvitecode ;
    public EditText edCheckCode ;

    public ImageView imgLoading;


    MainActivity thisActivity = Manager.getInst().mainActivity;
    Boolean isReSpend = true;


    public ViewRegister(View view) {
        _view = view;
        regbtnLogin = _view.findViewById(R.id.btn_reg_login);
        btnCheckphone = _view.findViewById(R.id.btn_reg_checkphone);
        btnRegister = _view.findViewById(R.id.btn_reg_register);

        edAccount = _view.findViewById(R.id.ed_reg_account);
        edName = _view.findViewById(R.id.ed_reg_name);
        edPhone = _view.findViewById(R.id.ed_reg_phone);
        //edSMSCheckCode = _view.findViewById(R.id.ed_reg_phone_checkcode);
        edPassword = _view.findViewById(R.id.ed_reg_password);
        edSafePassword = _view.findViewById(R.id.ed_reg_safepass);
        edInvitecode = _view.findViewById(R.id.ed_reg_invite_code);
        edCheckCode = _view.findViewById(R.id.ed_reg_check_code);

        imgLoading = _view.findViewById(R.id.img_reg_loading);


        regbtnLogin.setOnClickListener( loginClickListener );
        imgLoading.setOnClickListener( refreshCatcha );

        btnCheckphone.setOnClickListener( clickSpendPhoneCheck );
        btnRegister.setOnClickListener( registerClickListener );



        // --- 取得驗證碼 --- //
        String url =  String.format(
                "%s?t=%s&q=%s",
                Tools.getBaseApiURL()+UrlCommand.API_CAPTCHA_IMG,
                new Date().getTime(),
                APP.sessid
        );
        Tools.tttLog( "captcha_url :" + url );
        //sample
        //String url = "http://2.bp.blogspot.com/-0xuT_HEy68c/TwKoq7vSFoI/AAAAAAAAAMo/jurxwaxlYFE/s650/Spawn_collection_banner.jpg";
        new DownloadImageTask(imgLoading).execute( url );
        //newCaptchaImage();

        APP.appView = Constant.VIEW_REGISTER;
    }

    private void newCaptchaImage(){
        Tools.tttLog( "run newCaptchaImage." );


        JSONObject postData = new JSONObject();

        new Http(
            String.format("%s?q=%s", Tools.getBaseApiURL()+UrlCommand.API_CAPTCHA_CLEAR, APP.sessid),
            postData,
            Http.GET,
            new Http.Callback(){
                @Override
                public void callback(JSONObject res) {
                    try{
                        Tools.tttLog("get is success" );

                    }catch (Exception e){}
                }

                @Override
                public void callback(String res) {
                    try{
                        Tools.tttLog("get is success" );

                        String url =  String.format(
                                "%s?t=%s&q=%s",
                                Tools.getBaseApiURL()+UrlCommand.API_CAPTCHA_IMG,
                                new Date().getTime(),
                                APP.sessid
                        );
                        Tools.tttLog( "captcha_url :" + url );
                        new DownloadImageTask(imgLoading).execute( url );

                    }catch (Exception e){}

                }
            }
        ).start();
        /*


         */
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
                            btnCheckphone.setText(
                                Manager.getInst().mainActivity.getResources().getString(R.string.register_btn_checkphone)
                            );
                        }
                    });

                }else{
                    nowTime ++;
                    thisActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnCheckphone.setText(
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



    //--- register ---//
    View.OnClickListener registerClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText edSMSCheckCode = _view.findViewById(R.id.ed_reg_phone_checkcode);

            String strAccount = edAccount.getText().toString();
            String strName = edName.getText().toString();
            String strPhone = edPhone.getText().toString();
            String strPhoneCode = edSMSCheckCode.getText().toString();

            String strPass = edPassword.getText().toString();
            String strSafePass = edSafePassword.getText().toString();
            String strInvite = edInvitecode.getText().toString();
            String strCheckCode = edCheckCode.getText().toString();


            if( strAccount.length() < 5 ){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.register_err_account)
                );
                return;
            }
            if( strPhone.length() != 10 ){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.register_err_phone)
                );
                return;
            }
            if( strPass.length() < 5 ){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.register_err_pass)
                );
                return;
            }
            if( strSafePass.length() < 5 ){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.register_err_safepass)
                );
                return;
            }

            if( strName.length() <= 0 || strPhoneCode.length() <= 0 || strInvite.length() <= 0 || strCheckCode.length() <= 0 ){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.register_errs)
                );
                return;
            }


            // --- http post --- //
            try {
                //isCanClick = false;
                JSONObject postData = new JSONObject();
                postData.put("username", strAccount);
                postData.put("name", strName);
                postData.put("number", strPhone);
                postData.put("phone_check_code", strPhoneCode);

                postData.put("password", strPass);
                postData.put("c_password", strSafePass);
                postData.put("invite_code", strInvite);
                postData.put("attach", strCheckCode);


                new Http(
                    UrlCommand.API_REGISTER,
                    postData,

                    new Http.Callback(){
                        @Override
                        public void callback(JSONObject res) {
                            try{
                                Tools.tttLog(UrlCommand.API_REGISTER + ", callback = " + res.toString());

                                // --- res err pross --- //
                                if(Tools.httpErr(res)) {
                                    //isCanClick = true;
                                    return;
                                }
                                //APP.access_token = res.getString( "access_token" );
                                Manager.getInst().sysToast(
                                    thisActivity.getResources().getString(R.string.register_success)
                                );
                                new Actions( thisActivity ).changeView( Constant.ly_login );


                            }catch (Exception e){
                                //--- no data ---//
                                Manager.getInst().sysToast(
                                    thisActivity.getResources().getString(R.string.https_login_api_err)
                                );
                            }
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
    //--- register end. ---//

    //--- spend phone ---//
    View.OnClickListener clickSpendPhoneCheck = new Button.OnClickListener() {
        public void onClick(View v) {
            if( isReSpend == false ) {
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.register_phone_respend)
                );
                return;
            }


            EditText ed_reg_phone = _view.findViewById(R.id.ed_reg_phone);
            String strPhone = ed_reg_phone.getText().toString();

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


    View.OnClickListener refreshCatcha = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            newCaptchaImage();
        }
    };
}

