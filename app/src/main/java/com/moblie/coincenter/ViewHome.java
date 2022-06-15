package com.moblie.coincenter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moblie.coincenter.Http.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Handler;


public class ViewHome {
    public View _view;
    public Button btnLogin ;
    public Button btnRegister ;
    public EditText edAccount;
    public EditText edPassword ;


    private TextView tv_coin_total;



    public LinearLayout lyTop;
    public LinearLayout lyWalletList;
    public LinearLayout lyBottom;

    public FrameLayout lyTopBanner;
    public FrameLayout lyBottomMenu;
    public Boolean isCanClick = true;


    private FragmentManager fragmentMgr;
    private FragmentTopBanner fragmentTopBanner;



    public ViewHome(View view) {
        isCanClick = true;
        _view = view;


        //--- set top ---//
        lyTop = _view.findViewById(R.id.ly_top);


        //--- set bottom ---//
        lyBottom = _view.findViewById(R.id.ly_bottom);
        new MenuBottom(lyBottom).select( Bottom.HOME );



        /*
        View _menu_bottom = Manager.getInst().mainActivity.getLayoutInflater().inflate(R.layout.menu_bottom, null);
        lyBottom.addView(_menu_bottom);

        LinearLayout btnHome = _view.findViewById(R.id.btn_bottom_home);
        btnHome.setBackgroundColor(Manager.getInst().mainActivity.getResources().getColor(R.color.teal_200));
         */


        tv_coin_total = _view.findViewById(R.id.tv_total);
        lyWalletList = _view.findViewById(R.id.ly_wallet_list);
        //lyBottom = _view.findViewById(R.id.ly_bottom);

        Button btnDayGetCoin = _view.findViewById(R.id.btn_day_get_coin);
        btnDayGetCoin.setOnClickListener( SelfView.bswitch );



        //--- center btn ---//
        ImageView btnRecharge = _view.findViewById(R.id.c_btn_recharge);
        ImageView btnReinvest = _view.findViewById(R.id.c_btn_reinvest);
        ImageView btnWithdrawal = _view.findViewById(R.id.c_btn_withdrawal);
        ImageView btnShare = _view.findViewById(R.id.c_btn_share);
        ImageView btnMission = _view.findViewById(R.id.c_btn_mission);
        ImageView btnLogout = _view.findViewById(R.id.c_btn_logout);

        btnRecharge.setOnClickListener( SelfView.recharge );
        btnReinvest.setOnClickListener( new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( APP.appView == Constant.VIEW_REINVEST ) return;
                new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_reinvest );
            }
        });
        btnWithdrawal.setOnClickListener( new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( APP.appView == Constant.VIEW_WITHDRAWAL ) return;
                new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_withdrawal );
            }
        });
        btnShare.setOnClickListener( SelfView.share );
        btnMission.setOnClickListener( SelfView.mission );
        btnLogout.setOnClickListener( SelfView.logout );
        //btnLogout.setOnClickListener( SelfView.logout );


        APP.appView = Constant.VIEW_HOME;

        /**
         * --- start conn to serv to get data. ---
         *
         */
        try{
            // --- get user info --- //
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

                        ui_thread_update( res );
                        ui_member_info( res );
                    }

                    @Override
                    public void callback(String res) {}
                }
            ).start();


            // --- get coin list info --- //
            new Http(
                UrlCommand.API_COIN_LIST,
                new JSONObject(),
                new Callback(){
                    @Override
                    public void callback(JSONObject res) {
                        Tools.tttLog(UrlCommand.API_COIN_LIST +" callback = " + res.toString());
                        if(Tools.httpErr(res)) {
                            return;
                        }

                        ui_wallet_list( res );
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
    private void ui_marquee(String msg){
        TextView marquee = _view.findViewById(R.id.tv_marquee);
        marquee.setText(msg);
    }

    private void ui_thread_update(JSONObject _res){
        Manager.getInst().mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject _data = _res.getJSONObject("data");
                    int userId = _data.getInt("user_id");


                    //--- set view title ---//
                    new MenuTop(lyTop).setTitle(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString(R.string.app_top_title)
                            ,Integer.toString(userId)
                        )
                    );



                    //--- 跑馬燈 ---//
                    ui_marquee( String.format( " %s  %s ",
                            Manager.getInst().mainActivity.getResources().getString(R.string.home_message),
                            _data.getJSONObject("marquee_data").getString("text")
                    ));
                    //--- 總資產 ---//
                    tv_coin_total.setText( Double.toString(_data.getDouble("total_credit")) );



                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    private void ui_wallet_list( JSONObject _res){
        Manager.getInst().mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject _data = _res.getJSONObject("data");
                    JSONArray _coins = _data.getJSONArray("coins");


                    for ( int i = 0;  i < _coins.length(); i++) {
                        JSONObject _json = _coins.getJSONObject(i);
                        Tools.tttLog( "" + _json );

                        String coinName = "NUll" ;
                        String coinUnit = "NUll" ;

                        Tools.tttLog( "coinname = " + _json.getString("name") + _json.getString("name").getClass().getSimpleName() + _json.getString("name").length()  );
                        String jsonName = _json.getString("name");

                        if ( jsonName.equals("basic") ){
                            coinName = Manager.getInst().mainActivity.getResources().getString(R.string.home_basic);
                            coinUnit = Manager.getInst().mainActivity.getResources().getString(R.string.home_point);
                        }else if( jsonName.equals("promotion") ){
                            coinName = Manager.getInst().mainActivity.getResources().getString(R.string.home_promotion);
                            coinUnit = Manager.getInst().mainActivity.getResources().getString(R.string.home_USDT);
                        }else{
                            Tools.tttLog("else");
                        }

                        View _item = Manager.getInst().mainActivity.getLayoutInflater().inflate( Constant.ly_item_wallet_rd, null );
                        ImageView img_wallet = _item.findViewById(R.id.img_wallet);
                        TextView tv_name = _item.findViewById(R.id.tv_wallet_name);
                        TextView tv_coin = _item.findViewById(R.id.tv_wallet_coin);
                        TextView tv_coin_currency = _item.findViewById(R.id.tv_wallet_coin_currency);

                        if( jsonName.equals("promotion") ){
                            img_wallet.setImageResource(R.drawable.w2);
                        }
                        tv_name.setText( coinName );
                        tv_coin.setText(
                            String.format(
                                "%s %s",
                                Double.toString( _json.getDouble("amount") ),
                                coinUnit
                            )
                        );
                        tv_coin_currency.setText(
                            String.format(
                                "≈$ %s %s",
                                Double.toString( _json.getDouble("amount") ),
                                Manager.getInst().mainActivity.getResources().getString(R.string.home_USD)
                            )
                        );
                        if ( !jsonName.equals("coin_21") ) {
                            lyWalletList.addView(_item);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    private void ui_member_info(JSONObject _res){
        Manager.getInst().mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject _data = _res.getJSONObject("data");

                    int teamLevel = _data.getInt("team_level");
                    int walletLevel = _data.getInt("basic_level");
                    int recharge = _data.getInt("basic_real_recharge");
                    int userId = _data.getInt("user_id");

                    String memberLvStr = "";
                    switch (teamLevel){
                        case 0:
                            if ( walletLevel >= 1 ){
                                memberLvStr = MemberLevel.lv0;
                            }else if( recharge < 1000 ){
                                memberLvStr = MemberLevel.no;
                            }else{
                                memberLvStr = MemberLevel.lv0;
                            }
                            break;

                        case 1:
                            memberLvStr = MemberLevel.lv1;
                            break;

                        case 2:
                            memberLvStr = MemberLevel.lv2;
                            break;

                        case 3:
                            memberLvStr = MemberLevel.lv3;
                            break;

                        case 4:
                            memberLvStr = MemberLevel.lv4;
                            break;
                    }

                    TextView tvMemLevel = _view.findViewById(R.id.tv_home_mem_lvstr);
                    TextView tvMemLv = _view.findViewById(R.id.tv_home_mem_lv);
                    TextView tvMemId = _view.findViewById(R.id.tv_home_mem_id);
                    TextView tvRecharge = _view.findViewById(R.id.tv_real_recharge);

                    tvMemLevel.setText( memberLvStr );
                    tvMemLv.setText(
                        String.format("%s (lv.%s)",
                            memberLvStr,
                            Integer.toString(teamLevel)
                        )
                    );

                    tvMemId.setText(
                        String.format("%s %s",
                            Manager.getInst().mainActivity.getResources().getString( R.string.home_member ),
                            Integer.toString(userId)
                        )
                    );
                    tvRecharge.setText(
                        String.format(
                            Manager.getInst().mainActivity.getResources().getString( R.string.home_real_recharge ),
                            Integer.toString(recharge)
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

                    new Callback(){
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


