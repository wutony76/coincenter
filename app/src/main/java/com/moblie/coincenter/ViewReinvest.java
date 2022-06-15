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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moblie.coincenter.Http.Callback;

import org.json.JSONException;
import org.json.JSONObject;


public class ViewReinvest {
    public View _view;
    public Boolean isCanClick = true;
    private LinearLayout lyTop;
    private MainActivity thisActivity = Manager.getInst().mainActivity;


    public ViewReinvest(View view) {
        isCanClick = true;
        _view = view;


        //--- set view title ---//
        lyTop = _view.findViewById(R.id.ly_top);
        MenuTop menuTop = new MenuTop(lyTop);
        menuTop.setTitle( thisActivity.getResources().getString(R.string.top_reinvest) );
        ImageView backBtn = menuTop.getBackBtn();
        Func.topBack(backBtn);
        //Button btnGet = _view.findViewById(R.id.btn_day_minning);
        //btnGet.setOnClickListener( clickGetListener );



        Button btnReinvestOk = _view.findViewById(R.id.btn_reinvest_ok);
        btnReinvestOk.setOnClickListener(clickReinvestListener);

        APP.appView = Constant.VIEW_REINVEST;
        /**
         * --- start conn to serv to get data. ---
         *
         */
        try{
            // --- get share_friend --- //
            new Http(
                UrlCommand.API_REINVEST_INFO,
                new JSONObject(),
                new Callback(){
                    @Override
                    public void callback(JSONObject res) {
                        Tools.tttLog(UrlCommand.API_REINVEST_INFO +" callback = " + res.toString());
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
                    Double basicWallet = _data.getDouble("basic_credit");
                    Double promotionWallet = _data.getDouble("promotion_credit");
                    Double feeWallet = _data.getDouble("fee");

                    basicWallet = Math.round(basicWallet*100.0)/100.0;
                    promotionWallet = Math.round(promotionWallet*100.0)/100.0;



                    //--- init ui ---//
                    TextView tvMaxReinvest = _view.findViewById(R.id.tv_max_reinvest);
                    TextView tvFee = _view.findViewById(R.id.tv_fee);
                    TextView tvPromotionWallet = _view.findViewById(R.id.tv_promotion_wallet);
                    TextView tvBaseWallet = _view.findViewById(R.id.tv_base_wallet);

                    tvPromotionWallet.setText(
                        String.format(
                            "%s %s",
                            Double.toString(promotionWallet),
                            thisActivity.getResources().getString(R.string.bswitch_USDT)
                        )
                    );
                    tvBaseWallet.setText(
                        String.format(
                            "%s %s",
                            Double.toString(basicWallet),
                            thisActivity.getResources().getString(R.string.bswitch_point)
                        )
                    );

                    tvMaxReinvest.setText(
                        String.format(
                            thisActivity.getResources().getString(R.string.reinvest_max_coin),
                            Double.toString(promotionWallet)
                        )
                    );
                    tvFee.setText(
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


    View.OnClickListener clickReinvestListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            TextView tvReinvestCoin = _view.findViewById(R.id.tv_reinvest_coin);
            TextView tvReinvestSafepass = _view.findViewById(R.id.tv_reinvest_safepass);
            String reinvestCoin = tvReinvestCoin.getText().toString();
            String reinvestSafePass = tvReinvestSafepass.getText().toString();

            if ( reinvestCoin.length() <=0 || reinvestSafePass.length() < 5){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.reinvest_get_err)
                );
                return;
            }



            try{
                JSONObject postData = new JSONObject();
                postData.put("amount", reinvestCoin);
                postData.put("security_password", reinvestSafePass);

                new Http(
                    UrlCommand.API_REINVEST,
                    postData,

                    new Callback(){
                        @Override
                        public void callback(JSONObject res) {
                            Tools.tttLog(UrlCommand.API_REINVEST +" callback = " + res.toString());
                            if(Tools.httpErr(res)) {
                                return;
                            }

                            String _id = "";
                            try {
                                JSONObject _data = res.getJSONObject("data");
                                _id = _data.getString("qid");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Manager.getInst().sysToast(
                                Manager.getInst().mainActivity.getResources().getString(R.string.reinvest_get_success)
                            );

                            new Actions(thisActivity).changeView( Constant.ly_reinvest_log, _id );
                            //new Actions(thisActivity).changeView( Constant.ly_bswitch_log, _id );
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



