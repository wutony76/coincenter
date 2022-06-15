package com.moblie.coincenter;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moblie.coincenter.Http.Callback;

import org.json.JSONException;
import org.json.JSONObject;


public class ViewWithdrawal {
    public View _view;
    public Boolean isCanClick = true;
    private LinearLayout lyTop;
    private MainActivity thisActivity = Manager.getInst().mainActivity;

    EditText edCoin;
    EditText edAddr;
    EditText edSafePass;


    public ViewWithdrawal(View view) {
        isCanClick = true;
        _view = view;


        //--- set view title ---//
        lyTop = _view.findViewById(R.id.ly_top);
        MenuTop menuTop = new MenuTop(lyTop);
        menuTop.setTitle( thisActivity.getResources().getString(R.string.top_withdrawal) );
        ImageView backBtn = menuTop.getBackBtn();
        Func.topBack(backBtn);

        edCoin = _view.findViewById(R.id.tv_withdrawal_coin);
        edAddr = _view.findViewById(R.id.tv_withdrawal_addr);
        edSafePass = _view.findViewById(R.id.tv_withdrawal_safe_pass);

        Button btnWithdrawalOk = _view.findViewById(R.id.btn_withdrawal_ok);
        btnWithdrawalOk.setOnClickListener(clickWithdrawalListener);



        APP.appView = Constant.VIEW_WITHDRAWAL;
        /**
         * --- start conn to serv to get data. ---
         *
         */
        try{
            new Http(
                UrlCommand.API_WITHDRAW_PREPARE,
                new JSONObject(),
                new Callback(){
                    @Override
                    public void callback(JSONObject res) {
                        Tools.tttLog(UrlCommand.API_WITHDRAW_PREPARE +" callback = " + res.toString());
                        if(Tools.httpErr(res)) {
                            return;
                        }
                        ui_thread_update( res );
                    }

                    @Override
                    public void callback(String res) {}
                }
            ).start();

        }catch (Exception e){
            Tools.tttLog( "Err. " + e.toString() );
        }
    }



    //--- ui_update ---//
    private void ui_thread_update(JSONObject _res){
        Manager.getInst().mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    //--- 處理資料 ---//
                    JSONObject _data = _res.getJSONObject("data");
                    //Double basicWallet = _data.getDouble("basic_credit");
                    Double promotionWallet = _data.getDouble("promotion_credit");
                    Double feeWallet = _data.getDouble("withdrawal_fee");

                    //basicWallet = Math.round(basicWallet*100.0)/100.0;
                    promotionWallet = Math.round(promotionWallet*100.0)/100.0;
                    if( feeWallet == 0.0) feeWallet = 6.0;  //fee default.

                    //--- init ui ---//
                    TextView tvPromotionWallet = _view.findViewById(R.id.tv_promotion_wallet);
                    TextView tvWithdrawalP1 = _view.findViewById(R.id.tv_withdrawal_p1);
                    TextView tvWithdrawalFree = _view.findViewById(R.id.tv_withdrawal_free);


                    tvWithdrawalP1.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.withdrawal_p1),
                            Double.toString(promotionWallet) +  thisActivity.getResources().getString(R.string.bswitch_USDT)
                        )
                    );

                    tvPromotionWallet.setText(
                        String.format(
                            "%s %s",
                            Double.toString(promotionWallet),
                            thisActivity.getResources().getString(R.string.bswitch_USDT)
                        )
                    );

                    tvWithdrawalFree.setText(
                        String.format(
                            thisActivity.getResources().getString(R.string.reinvest_free),
                            Double.toString(feeWallet)
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


    View.OnClickListener clickWithdrawalListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            String strEdCoin = edCoin.getText().toString();
            String strEdSafePass = edSafePass.getText().toString();
            String strEdAddr = edAddr.getText().toString();
            Tools.tttLog(  strEdCoin+" - "+strEdSafePass+" - "+strEdAddr);

            if ( strEdCoin.length() <=0 || strEdSafePass.length() < 5 || strEdAddr.length() != 34){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.withdrawal_err)
                );
                return;
            }


            try{
                JSONObject postData = new JSONObject();
                postData.put("amount", strEdCoin);
                postData.put("type", "2");
                postData.put("address", strEdAddr);
                postData.put("password", strEdSafePass);

                new Http(
                    UrlCommand.API_WITHDRAW,
                    postData,
                    new Callback(){
                        @Override
                        public void callback(JSONObject res) {
                            Tools.tttLog(UrlCommand.API_WITHDRAW +" callback = " + res.toString());
                            if(Tools.httpErr(res)) {
                                return;
                            }

                            String _id = "";
                            try {
                                JSONObject _data = res.getJSONObject("data");
                                _id = _data.getString("req_id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Manager.getInst().sysToast(
                                Manager.getInst().mainActivity.getResources().getString(R.string.withdraw_success)
                            );
                            new Actions(thisActivity).changeView( Constant.ly_withdrawLog, _id );
                        }

                        @Override
                        public void callback(String res) {}
                    }
                ).start();

            }catch (Exception e){
                Tools.tttLog( "Err. " + e.toString() );
            }

        }
    };
}



