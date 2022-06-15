package com.moblie.coincenter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moblie.coincenter.Http.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ViewBswitch {
    public View _view;
    public Boolean isCanClick = true;
    private LinearLayout lyTop;
    private MainActivity thisActivity = Manager.getInst().mainActivity;


    private EditText edGetCoin;
    private EditText edSafePass;


    private ProgressBar bar;

    public ViewBswitch(View view) {
        isCanClick = true;
        _view = view;


        //--- set view title ---//
        lyTop = _view.findViewById(R.id.ly_top);
        MenuTop menuTop = new MenuTop(lyTop);
        menuTop.setTitle( thisActivity.getResources().getString(R.string.top_minning) );
        ImageView backBtn = menuTop.getBackBtn();
        Func.topBack(backBtn);
        Button btnGet = _view.findViewById(R.id.btn_day_minning);
        btnGet.setOnClickListener( clickGetListener );

        edGetCoin = _view.findViewById(R.id.tv_day_minning_coin);
        edSafePass = _view.findViewById(R.id.tv_day_safe_pass);

        bar = _view.findViewById(R.id.bar_point);
        bar.setProgress(100);

        Button btnRecharge =_view.findViewById(R.id.btn_bs_recharge);
        btnRecharge.setOnClickListener(SelfView.recharge);




        APP.appView = Constant.VIEW_BSWITCH;
        /**
         * --- start conn to serv to get data. ---
         *
         */
        try{
            // --- get share_friend --- //
            new Http(
                UrlCommand.API_BSWITCH,
                new JSONObject(),
                new Callback(){
                    @Override
                    public void callback(JSONObject res) {
                        Tools.tttLog(UrlCommand.API_BSWITCH +" callback = " + res.toString());
                        if(Tools.httpErr(res)) {
                            return;
                        }
                        ui_thread_update( res );


                        //--- get_user_info ---//
                        try{
                            new Http(
                                UrlCommand.API_USER_INFO,
                                new JSONObject(),
                                new Callback(){
                                    @Override
                                    public void callback(JSONObject res) {
                                        Tools.tttLog(UrlCommand.API_USER_INFO +" callback = " + res.toString());
                                        if(Tools.httpErr(res)) {
                                            return;
                                        }
                                        ui_thread_update2( res );
                                    }

                                    @Override
                                    public void callback(String res) {}
                                }
                            ).start();

                        }catch (Exception e){
                            Tools.tttLog( "Err. " + e.toString() );
                        }
                        //--- get_user_info end. ---//
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
            @SuppressLint({"StringFormatInvalid", "ResourceAsColor"})
            @Override
            public void run() {
                try {
                    //--- 處理資料 ---//
                    JSONObject _data = _res.getJSONObject("data");
                    Double basicCredit = _data.getDouble("basic_credit");
                    Double promotionCredit = _data.getDouble("promotion_credit");

                    Double outLimit = _data.getDouble("withdraw_basic_limit_total");
                    Double todayCoin = _data.getDouble("today_withdraw_basic");

                    Double baseDayGet = _data.getDouble("withdraw_basic_limit");
                    Double fastDayGet = _data.getDouble("withdraw_basic_limit_extra");
                    String dayGetDesc = _data.getString("withdraw_basic_limit_total_desc");



                    //--- init ui ---//
                    TextView tvBaseWallet = _view.findViewById(R.id.tv_base_wallet);
                    TextView tvPromotionWallet = _view.findViewById(R.id.tv_promotion_wallet);

                    TextView tvC1 = _view.findViewById(R.id.tv_c1);
                    TextView tvC2 = _view.findViewById(R.id.tv_c2);
                    TextView tvC3 = _view.findViewById(R.id.tv_c3);
                    TextView tvC4 = _view.findViewById(R.id.tv_c4);


                    tvBaseWallet.setText(
                        String.format(
                            "%s %s",
                            Double.toString(basicCredit),
                            thisActivity.getResources().getString(R.string.bswitch_point)
                        )
                    );

                    tvPromotionWallet.setText(
                        String.format(
                            "%s %s",
                            Double.toString(promotionCredit),
                            thisActivity.getResources().getString(R.string.bswitch_USDT)
                        )
                    );


                    tvC1.setText(
                        String.format(
                            thisActivity.getResources().getString(R.string.bswitch_t1),
                            Double.toString(baseDayGet)
                        )
                    );
                    tvC2.setText(
                        String.format(
                            thisActivity.getResources().getString(R.string.bswitch_t2),
                            Double.toString(fastDayGet)
                        )
                    );
                    tvC3.setText(
                        String.format(
                            thisActivity.getResources().getString(R.string.bswitch_t3),
                            dayGetDesc
                        )
                    );
                    tvC4.setText(
                        String.format(
                            thisActivity.getResources().getString(R.string.bswitch_t4),
                            Double.toString(todayCoin),
                            Double.toString(outLimit)
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

    private void ui_thread_update2(JSONObject _res){
        Manager.getInst().mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    //--- 處理資料 ---//
                    JSONObject _data = _res.getJSONObject("data");
                    //Double _realRecharge = _data.getDouble("basic_real_recharge");
                    Double _bonusPointLimit = _data.getDouble("bonus_point_limit");
                    Double _bonusPoint = _data.getDouble("bonus_point");
                    Double _realGetCoin = _bonusPointLimit - _bonusPoint;
                    Double barParties = _realGetCoin/_bonusPointLimit*100;
                    bar.setProgress( (int)Math.round(barParties) );

                    _realGetCoin = Math.round(_realGetCoin*100.0)/100.0;
                    _bonusPointLimit = Math.round(_bonusPointLimit*100.0)/100.0;



                    //--- init ui ---//
                    TextView tvBar = _view.findViewById(R.id.tv_bs_c1);
                    tvBar.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.bswitch_bar_c1),
                            Double.toString(_realGetCoin),
                            Double.toString(_bonusPointLimit)
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


                            String _id = "";

                            //--- 處理資料 ---//
                            try {
                                JSONObject _data = res.getJSONObject("data");
                                _id = _data.getString("id");
                                //Tools.tttLog( "tttttttttt _id = "+_id );

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            Manager.getInst().sysToast(
                                Manager.getInst().mainActivity.getResources().getString(R.string.bswitch_get_success)
                            );


                            new Actions(thisActivity).changeView( Constant.ly_bswitch_log, _id );

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



