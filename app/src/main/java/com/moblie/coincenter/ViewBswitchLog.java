package com.moblie.coincenter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moblie.coincenter.Http.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class ViewBswitchLog {
    public View _view;
    //private String _logId;
    public Boolean isCanClick = true;
    private LinearLayout lyTop;
    private MainActivity thisActivity = Manager.getInst().mainActivity;

    //private EditText edGetCoin;
    //private EditText edSafePass;


    public ViewBswitchLog(View view, String minningId) {
        isCanClick = true;
        _view = view;
        String _logId = minningId;


        //--- set view title ---//
        lyTop = _view.findViewById(R.id.ly_top);
        MenuTop menuTop = new MenuTop(lyTop);
        menuTop.setTitle( thisActivity.getResources().getString(R.string.bswitch_log) );
        /*
        ImageView backBtn = menuTop.getBackBtn();
        Func.topBack(backBtn);
        Button btnGet = _view.findViewById(R.id.btn_day_minning);
        btnGet.setOnClickListener( clickGetListener );
         */

        //edGetCoin = _view.findViewById(R.id.tv_day_minning_coin);
        //edSafePass = _view.findViewById(R.id.tv_day_safe_pass);
        Button btnLogOk = _view.findViewById(R.id.btn_log_ok);
        btnLogOk.setOnClickListener(SelfView.home);


        APP.appView = Constant.VIEW_BSWITCH_LOG;

        /**
         * --- start conn to serv to get data. ---
         *
         */
        try{
            //--- http get ---//
            new Http(
                String.format("%s?q=%s", Tools.getBaseApiURL()+UrlCommand.API_BSWITCH_EXCHANGE_LOG, _logId),
                new JSONObject(),
                Http.GET,
                new Http.Callback(){
                    @Override
                    public void callback(JSONObject res) {
                        /*
                        * POST Somthing... callback.
                        * */
                        try{
                            //Tools.tttLog("get1 is success" + res );
                        }catch (Exception e){}
                    }

                    @Override
                    public void callback(String res) {
                        try{
                            Tools.tttLog("get r="+res);
                            JSONObject _jsonRes = new JSONObject( res );

                            if(Tools.httpErr(_jsonRes)) {
                                return;
                            }

                            ui_thread_update( _jsonRes );
                        }catch (Exception e){}
                    }
                }
            ).start();

        }catch (Exception e){
            Tools.tttLog( "Err. " + e.toString() );
        }
    }



    //--- ui_update ---//
    private void ui_thread_update(JSONObject _res){
        Manager.getInst().mainActivity.runOnUiThread(new Runnable() {
            @SuppressLint({"StringFormatInvalid", "ResourceAsColor"})
            @Override
            public void run() {
                try {
                    //--- 處理資料 ---//
                    JSONObject _data = _res.getJSONObject("data");
                    String idDesc = _data.getString("id");
                    String coinDesc = _data.getString("amount");
                    String timeDesc = _data.getString("create_time");

                    TextView logC1 = _view.findViewById(R.id.tv_log_c1);
                    TextView logC2 = _view.findViewById(R.id.tv_log_c2);
                    TextView logC3 = _view.findViewById(R.id.tv_log_c3);

                    logC1.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.bswitch_log_id),
                            idDesc
                        )
                    );
                    logC2.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.bswitch_log_coin),
                            coinDesc
                        )
                    );
                    logC3.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.bswitch_log_time),
                            timeDesc
                        )
                    );


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }
    //--- ui_update end. ---//



    ClipboardManager clipboard = null;
    ClipData clipData = null;

    //--- onclick listener ---//
    View.OnClickListener clickGetListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {


            /*
            String getCoin = edGetCoin.getText().toString();
            String getSafePass = edSafePass.getText().toString();

            if ( getCoin.length() <=0 || getSafePass.length() < 5){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.bswitch_get_err)
                );
                return;
            }


            try{
                JSONObject postData = new JSONObject();
                postData.put("amount", getCoin);
                postData.put("security_password", getSafePass);

                // --- day get coin --- //
                new Http(
                    UrlCommand.API_BSWITCH_EXCHANGE,
                    postData,
                    new Callback(){
                        @Override
                        public void callback(JSONObject res) {
                            Tools.tttLog(UrlCommand.API_BSWITCH_EXCHANGE +" callback = " + res.toString());

                            if(Tools.httpErr(res)) {
                                return;
                            }

                            Manager.getInst().sysToast(
                                Manager.getInst().mainActivity.getResources().getString(R.string.bswitch_get_success)
                            );






                            //if( APP.appView == Constant.VIEW_HOME ) return;
                            //new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_home );
                        }

                        @Override
                        public void callback(String res) {}
                    }
                ).start();

            }catch (Exception e){
                Tools.tttLog( "Err. " + e.toString() );
                //if( APP.appView == Constant.VIEW_HOME ) return;
                //new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_home );
            }

             */

        }
    };


    View.OnClickListener copyClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Tools.tttLog( "click to COPY." );

            try{
                TextView tvShareUrl = _view.findViewById(R.id.tv_share_url);
                clipboard = (ClipboardManager) Manager.getInst().mainActivity.getSystemService( Context.CLIPBOARD_SERVICE );
                clipData = ClipData.newPlainText(null, tvShareUrl.getText().toString());
                clipboard.setPrimaryClip(clipData);

                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString( R.string.share_copy_success )
                );
            }catch (Exception e){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString( R.string.share_copy_err )
                );
            }

        }
    };

}


