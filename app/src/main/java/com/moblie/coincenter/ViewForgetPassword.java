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


public class ViewForgetPassword extends ViewChangePassword {
    public View _view;

    public EditText edPhone ;
    public EditText edPassword ;
    public EditText edPassword2 ;

    public Button btnCheckPhone;

    MainActivity thisActivity = Manager.getInst().mainActivity;
    Boolean isReSpend = true;


    public View nullView = null;
    public ViewForgetPassword(View view) {
        super(view);

        _view = view;

        //--- set view title ---//
        menuTop.setTitle( thisActivity.getResources().getString(R.string.top_forget_password) );
        //--- set view title end.  ---//


        edPhone = _view.findViewById(R.id.ed_chpass_phone);
        edPassword = _view.findViewById(R.id.ed_chpass_password);
        edPassword2 = _view.findViewById(R.id.ed_chpass_password2);

        btnCheckPhone = _view.findViewById(R.id.btn_chpass_checkphone);
        Button btnChpassOk = _view.findViewById(R.id.btn_chpass_ok);

        btnCheckPhone.setOnClickListener( clickSpendPhoneCheck );
        btnChpassOk.setOnClickListener( clickForgetListener );

        APP.appView = Constant.VIEW_FORGET_PASSWORD;
    }

    //--- change password ---//
    View.OnClickListener clickForgetListener = new Button.OnClickListener() {
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

                                new Actions( thisActivity ).changeView( Constant.ly_login );

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

}

