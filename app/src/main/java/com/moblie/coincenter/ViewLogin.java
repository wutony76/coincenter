package com.moblie.coincenter;

import android.util.Log;
import android.widget.Button;


import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class ViewLogin {
    public View _view;
    public Button btnLogin ;
    public Button btnRegister ;
    public EditText edAccount;
    public EditText edPassword ;

    public Boolean isCanClick = true;


    public ViewLogin(View view) {
        isCanClick = true;
        _view = view;
        btnLogin = _view.findViewById(R.id.btn_sumbit_login);
        btnRegister = _view.findViewById(R.id.btn_to_register);
        edAccount = _view.findViewById(R.id.ed_login_account);
        edPassword = _view.findViewById(R.id.ed_login_password);
        TextView btnForgotPassword = _view.findViewById(R.id.tv_forgot_password);


        btnLogin.setOnClickListener( loginClickListener );
        btnRegister.setOnClickListener( registerClickListener );
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_forget_password );
            }
        });


        APP.appView = Constant.VIEW_LOGIN;



        // --- check session --- //
        try{
            JSONObject postData = new JSONObject();
            postData.put("sessid", APP.sessid);
            postData.put("access_token", APP.access_token);

            new Http(
                UrlCommand.API_CHECK_SESSION,
                postData,
                new Http.Callback(){
                    @Override
                    public void callback(JSONObject res) {
                        try{
                            //Constant.access_token = res.getString( "access_token" );
                            Tools.tttLog("check_session, callback = " + res.toString());
                            APP.sessid = res.getString( "sessid" );

                        }catch (Exception e){}
                    }

                    @Override
                    public void callback(String res) {

                    }
                }
            ).start();
        }catch (Exception e){
            Tools.tttLog( "Err. " + e.toString() );
        }
    }


    View.OnClickListener loginClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Tools.tttLog( edPassword.getText().toString() );
            Tools.tttLog( edAccount.getText().toString() );
            Tools.tttLog( "click login." );
            Tools.tttLog( "isCanClick = " + isCanClick );

            String strAccout = edAccount.getText().toString();
            String strPass = edPassword.getText().toString();





            // --- check input ---//




            if( Tools.isCheckNull(strAccout) || Tools.isCheckNull(strPass) ){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.https_login_null)
                );
                return;
            }

            // --- check can click btn --- //
            if(!isCanClick) return;

            // --- http post --- //
            try {
                isCanClick = false;
                JSONObject postData = new JSONObject();
                postData.put("mobile", URLEncoder.encode(strAccout, "UTF-8"));
                postData.put("password", URLEncoder.encode(strPass, "UTF-8"));
                postData.put("url", "android app.");

                new Http(
                    UrlCommand.API_LOGIN,
                    postData,

                    new Http.Callback(){
                        @Override
                        public void callback(JSONObject res) {
                            try{
                                Tools.tttLog("login, callback = " + res.toString());

                                // --- res err pross --- //
                                if(Tools.httpErr(res)) {
                                    isCanClick = true;
                                    return;
                                }

                                APP.access_token = res.getString( "access_token" );
                                Manager.getInst().sysToast(
                                    Manager.getInst().mainActivity.getResources().getString(R.string.https_login_success)
                                );
                                new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_home );


                            }catch (Exception e){
                                //--- no data ---//
                                Manager.getInst().sysToast(
                                    Manager.getInst().mainActivity.getResources().getString(R.string.https_login_api_err)
                                );
                            }
                            isCanClick = true;
                        }

                        @Override
                        public void callback(String res) {

                        }
                    }

                ).start();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };


    View.OnClickListener registerClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Tools.tttLog( "click to register." );
            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_register );
        }
    };






}

