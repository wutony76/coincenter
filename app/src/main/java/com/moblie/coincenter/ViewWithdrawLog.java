package com.moblie.coincenter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moblie.coincenter.Http.Callback;

import org.json.JSONException;
import org.json.JSONObject;


public class ViewWithdrawLog {
    public View _view;
    //private String _logId;
    public Boolean isCanClick = true;
    private LinearLayout lyTop;
    private MainActivity thisActivity = Manager.getInst().mainActivity;

    //private EditText edGetCoin;
    //private EditText edSafePass;


    public ViewWithdrawLog(View view, String withdrawId) {
        isCanClick = true;
        _view = view;
        String _logId = withdrawId;

        //--- set view title ---//
        lyTop = _view.findViewById(R.id.ly_top);
        MenuTop menuTop = new MenuTop(lyTop);
        menuTop.setTitle( thisActivity.getResources().getString(R.string.withdraw_log) );



        Button btnLogOk = _view.findViewById(R.id.btn_withlog_ok);
        btnLogOk.setOnClickListener(SelfView.home);

        APP.appView = Constant.VIEW_WITHDRAW_LOG;
        /**
         * --- start conn to serv to get data. ---
         *
         */

        try{
            //--- http get ---//
            new Http(
                String.format("%s?q=%s", Tools.getBaseApiURL()+UrlCommand.API_WITHDRAW_LOG, _logId),
                new JSONObject(),
                Http.GET,
                new Callback(){
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
                    //String idDesc = _data.getString("id");
                    String reqIdDesc = _data.getString("req_id");
                    String coinDesc = _data.getString("amount");
                    String feeDesc = _data.getString("fee");
                    String stateDesc = _data.getString("state_desc");
                    String timeDesc = _data.getString("time_desc");
                    String typeDesc = thisActivity.getResources().getString(R.string.withdrawal_wallet);
                    String remarkDesc = _data.getString("remark");


                    TextView logC1 = _view.findViewById(R.id.tv_withlog_id);
                    TextView logC2 = _view.findViewById(R.id.tv_withlog_type);
                    TextView logC3 = _view.findViewById(R.id.tv_withlog_coin);
                    TextView logC4 = _view.findViewById(R.id.tv_withlog_fee);
                    TextView logC5 = _view.findViewById(R.id.tv_withlog_time);
                    TextView logC6 = _view.findViewById(R.id.tv_withlog_status);
                    TextView logC7 = _view.findViewById(R.id.tv_withlog_mask);


                    logC1.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.withdraw_log_id),
                            reqIdDesc
                        )
                    );

                    logC2.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.withdraw_log_type),
                            typeDesc
                        )
                    );
                    logC3.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.withdraw_log_coin),
                            coinDesc
                        )
                    );
                    logC4.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.withdraw_log_fee),
                            feeDesc
                        )
                    );
                    logC5.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.withdraw_log_time),
                            timeDesc
                        )
                    );
                    logC6.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.withdraw_log_status),
                            stateDesc
                        )
                    );
                    logC7.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.withdraw_log_remark),
                            remarkDesc
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


}


