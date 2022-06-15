package com.moblie.coincenter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moblie.coincenter.Http.Callback;

import org.json.JSONException;
import org.json.JSONObject;


public class ViewReinvestLog {
    public View _view;
    //private String _logId;
    public Boolean isCanClick = true;
    private LinearLayout lyTop;
    private MainActivity thisActivity = Manager.getInst().mainActivity;
    String _logId;

    //private EditText edGetCoin;
    //private EditText edSafePass;

    public boolean isCheck = Boolean.FALSE;

    public ViewReinvestLog(View view, String reinvestId) {
        isCanClick = true;
        _view = view;
        _logId = reinvestId;

        //--- set view title ---//
        lyTop = _view.findViewById(R.id.ly_top);
        MenuTop menuTop = new MenuTop(lyTop);
        menuTop.setTitle( thisActivity.getResources().getString(R.string.reinvest_log) );



        Button btnLogOk = _view.findViewById(R.id.btn_reinvestlog_ok);
        btnLogOk.setOnClickListener(SelfView.home);

        APP.appView = Constant.VIEW_REINVEST_LOG;
        /**
         * --- start conn to serv to get data. ---
         *
         */

        try{
            new Http(
                String.format("%s?q=%s", UrlCommand.API_REINVEST_LOG, _logId),
                new JSONObject(),

                new Callback(){
                    @Override
                    public void callback(JSONObject res) {
                        Tools.tttLog("post res = "+res);
                        try{
                            if(Tools.httpErr(res)) {
                                return;
                            }

                            ui_thread_update( res );
                            check_reinvest_status();
                        }catch (Exception e){}
                    }

                    @Override
                    public void callback(String res) {}
                }
            ).start();

        }catch (Exception e){
            Tools.tttLog( "Err. " + e.toString() );
        }
    }
    public void check_reinvest_status(){
        Tools.tttLog("check_reinvest_status");
        int sleepTime = 3000;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //遲1s執行的程式碼

                Tools.tttLog( "is Loop." );
                try{
                    new Http(
                        String.format("%s?q=%s", UrlCommand.API_REINVEST_LOG, _logId),
                        new JSONObject(),
                        new Callback(){
                            @Override
                            public void callback(JSONObject res) {
                                try{
                                    Tools.tttLog("post res = "+res);
                                    if(Tools.httpErr(res)) {
                                        return;
                                    }
                                    ui_check_update( res );

                                }catch (Exception e){}
                            }

                            @Override
                            public void callback(String res) {}
                        }
                    ).start();
                }catch (Exception e){}

            }
        }, sleepTime);
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
                    String stateDesc = _data.getString("state_desc");

                    TextView logC1 = _view.findViewById(R.id.tv_reinvest_c1);
                    TextView logC2 = _view.findViewById(R.id.tv_reinvest_c2);
                    TextView logC3 = _view.findViewById(R.id.tv_reinvest_c3);
                    TextView logC4 = _view.findViewById(R.id.tv_reinvest_c4);


                    logC1.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.reinvest_log_id),
                            idDesc
                        )
                    );

                    logC2.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.reinvest_log_coin),
                            coinDesc
                        )
                    );
                    logC3.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.reinvest_log_time),
                            timeDesc
                        )
                    );
                    logC4.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.reinvest_log_status),
                            stateDesc
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
    private void ui_check_update(JSONObject _res) {

        Manager.getInst().mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject _data = null;
                    _data = _res.getJSONObject("data");
                    int isWait = _data.getInt("is_wait");
                    String stateDesc = _data.getString("state_desc");



                    TextView logC4 = _view.findViewById(R.id.tv_reinvest_c4);
                    logC4.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.reinvest_log_status),
                            stateDesc
                        )
                    );

                    if (isWait == 0){
                        isCheck = true;
                    }else{
                        check_reinvest_status();
                    }

                }catch (Exception e){}
            }
        });


    }
    //--- ui_update end. ---//


}


