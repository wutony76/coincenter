package com.moblie.coincenter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moblie.coincenter.Http.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ViewReportMy {
    public View _view;
    public Boolean isCanClick = true;
    private LinearLayout lyTop;
    private MainActivity thisActivity = Manager.getInst().mainActivity;

    private Integer currPage = 3;

    public ViewReportMy(View view) {
        isCanClick = true;
        _view = view;


        //--- set view title ---//
        lyTop = _view.findViewById(R.id.ly_top);
        MenuTop menuTop = new MenuTop(lyTop);
        menuTop.setTitle( thisActivity.getResources().getString(R.string.report_team_title) );
        ImageView backBtn = menuTop.getBackBtn();
        Func.topBack(backBtn);
        //Button btnGet = _view.findViewById(R.id.btn_day_minning);
        //btnGet.setOnClickListener( clickGetListener );



        //Button btnReinvestOk = _view.findViewById(R.id.btn_reinvest_ok);
        //btnReinvestOk.setOnClickListener(clickReinvestListener);
        APP.appView = Constant.VIEW_REPORT_MY;

        /**
         * --- start conn to serv to get data. ---
         *
         */
        try{
            new Http(
                UrlCommand.API_REPORT_MY,
                new JSONObject(),
                new Callback(){
                    @Override
                    public void callback(JSONObject res) {
                        Tools.tttLog(UrlCommand.API_REPORT_MY +" callback = " + res.toString());
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
                    Double totalCredit = _data.getDouble("total");
                    Double dayTotalCredit = _data.getDouble("day_total");
                    JSONArray month_total = _data.getJSONArray("month_total");
                    JSONArray _rowsList = _data.getJSONArray("member_rows");

                    totalCredit = Math.round(totalCredit*100.0)/100.0;
                    dayTotalCredit = Math.round(dayTotalCredit*100.0)/100.0;



                    //--- init ui ---//
                    LinearLayout ly_report_list = _view.findViewById(R.id.ly_report_list);
                    TextView tvDayCredit= _view.findViewById(R.id.tv_day_credit);
                    TextView tvTeamCredit = _view.findViewById(R.id.tv_teamreport_credit);

                    TextView tvMonth1 = _view.findViewById(R.id.tv_teamreport_month1);
                    TextView tvMonth2 = _view.findViewById(R.id.tv_teamreport_month2);
                    TextView tvMonth3 = _view.findViewById(R.id.tv_teamreport_month3);

                    tvDayCredit.setText(
                        String.format(
                            "%s %s",
                            Double.toString(dayTotalCredit),
                            thisActivity.getResources().getString(R.string.report_team_USDT)
                        )
                    );
                    tvTeamCredit.setText(
                        String.format(
                            "%s %s",
                            Double.toString(totalCredit),
                            thisActivity.getResources().getString(R.string.report_team_USDT)
                        )
                    );


                    tvMonth1.setText(
                        String.format(
                            "%s %s",
                            Double.toString( month_total.getDouble(0) ),
                            thisActivity.getResources().getString(R.string.report_team_USDT)
                        )
                    );
                    tvMonth2.setText(
                        String.format(
                            "%s %s",
                            Double.toString(month_total.getDouble(1)),
                            thisActivity.getResources().getString(R.string.report_team_USDT)
                        )
                    );
                    tvMonth3.setText(
                        String.format(
                            "%s %s",
                            Double.toString(month_total.getDouble(2)),
                            thisActivity.getResources().getString(R.string.report_team_USDT)
                        )
                    );







                    //--- report my list ---//
                    for ( int i = 0;  i < _rowsList.length(); i++) {
                        JSONObject _json = _rowsList.getJSONObject(i);
                        String time  = _json.getString("time");
                        String member_id  = _json.getString("member_id");
                        int pn  = _json.getInt("pn");
                        Double amount  = _json.getDouble("amount");
                        amount = Math.round(amount*100.0)/100.0;
                        String amountDesc = "";


                        View _item = thisActivity.getLayoutInflater().inflate(R.layout.item_report_my_rd, null);
                        TextView tvCol1 = _item.findViewById(R.id.tv_col1);
                        TextView tvCol2 = _item.findViewById(R.id.tv_col2);
                        TextView tvCol3 = _item.findViewById(R.id.tv_col3);

                        if(pn == 1){
                            amountDesc = "+" + amount;
                            tvCol3.setTextColor( thisActivity.getResources().getColor(R.color.teal_700) );
                        }else{
                            amountDesc = "-" + amount;
                            tvCol3.setTextColor( thisActivity.getResources().getColor(R.color.teal_700) );
                        }
                        tvCol1.setText( time );
                        tvCol2.setText( member_id );
                        tvCol3.setText( amountDesc );

                        ly_report_list.addView( _item );
                    }
                    //--- report my list end. ---//




                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }
    private void ui_elist_update(JSONObject _res){
        Manager.getInst().mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    //--- 處理資料 ---//
                    JSONObject _data = _res.getJSONObject("data");
                    JSONArray _objsList = _data.getJSONArray("objs");
                    Integer allPage = _data.getInt("total_page");
                    Integer thisPage = _data.getInt("cur_page");


                    LinearLayout lyElist = _view.findViewById(R.id.ly_elist);
                    Button btnLoadMore = _view.findViewById(R.id.btn_elist_load);
                    btnLoadMore.setOnClickListener( clickLoadMoreListener );

                    for ( int i = 0;  i < _objsList.length(); i++) {
                        JSONObject _json = _objsList.getJSONObject(i);
                        int walletType = _json.getInt("wallet_type");
                        int blanceType1 = _json.getInt("type1");
                        String walletDesc = "";

                        View _item = thisActivity.getLayoutInflater().inflate(R.layout.item_elist_rd, null);
                        TextView tvCol1 = _item.findViewById(R.id.tv_col1);
                        TextView tvCol2 = _item.findViewById(R.id.tv_col2);
                        TextView tvCol3 = _item.findViewById(R.id.tv_col3);
                        TextView tvCol4 = _item.findViewById(R.id.tv_col4);
                        TextView tvCol5 = _item.findViewById(R.id.tv_col5);


                        if (walletType == 1){
                            walletDesc = thisActivity.getString(R.string.elist_basic);
                        }else if (walletType == 2){
                            walletDesc = thisActivity.getString(R.string.elist_promotion);
                        }else if (walletType == 3){
                            walletDesc = thisActivity.getString(R.string.elist_getpoint);
                        }else{
                            walletDesc = Integer.toString(walletType);
                        }

                        if(blanceType1 == 2){
                            tvCol3.setText(
                                String.format(
                                    "-%s",
                                    _json.getString("credit")
                                )
                            );
                            //tvCol4.setTextColor(Color.RED);
                            tvCol3.setTextColor( thisActivity.getResources().getColor(R.color.red_f44336) );

                        }else if(blanceType1 == 1){
                            tvCol3.setText(
                                String.format(
                                    "+%s",
                                    _json.getString("credit")
                                )
                            );
                            tvCol3.setTextColor( thisActivity.getResources().getColor(R.color.teal_700) );
                        }

                        tvCol1.setText( _json.getString("time"));
                        tvCol2.setText( walletDesc );
                        //tvCol3.setText( _json.getString("credit"));
                        tvCol4.setText( _json.getString("balance_credit") );
                        tvCol5.setText( _json.getString("type2_desc"));

                        lyElist.addView( _item );
                    }
                    if( thisPage < allPage ){
                        btnLoadMore.setVisibility(View.VISIBLE);
                    }else{
                        btnLoadMore.setVisibility(View.INVISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }
    //--- ui_update end. ---//


    View.OnClickListener clickLoadMoreListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            currPage +=1 ;

            try{
                JSONObject postData = new JSONObject();
                postData.put("cur_page", currPage);
                postData.put("page_size", 50);

                new Http(
                    UrlCommand.API_ELIST,
                    postData,
                    new Callback(){
                        @Override
                        public void callback(JSONObject res) {
                            Tools.tttLog(UrlCommand.API_ELIST +" Load callback = " + res.toString());
                            if(Tools.httpErr(res)) {
                                return;
                            }
                            //ui_elist_load_update( res );
                            ui_elist_update( res );
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



