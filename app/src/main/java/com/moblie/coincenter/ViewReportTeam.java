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


public class ViewReportTeam {
    public View _view;
    public Boolean isCanClick = true;
    private LinearLayout lyTop;
    private MainActivity thisActivity = Manager.getInst().mainActivity;

    private Integer curPage = 1;

    public ViewReportTeam(View view) {
        isCanClick = true;
        _view = view;


        //--- set view title ---//
        lyTop = _view.findViewById(R.id.ly_top);
        MenuTop menuTop = new MenuTop(lyTop);
        menuTop.setTitle( thisActivity.getResources().getString(R.string.report_team2_title) );
        ImageView backBtn = menuTop.getBackBtn();
        Func.topBack(backBtn);
        //Button btnGet = _view.findViewById(R.id.btn_day_minning);
        //btnGet.setOnClickListener( clickGetListener );



        //Button btnReinvestOk = _view.findViewById(R.id.btn_reinvest_ok);
        //btnReinvestOk.setOnClickListener(clickReinvestListener);
        APP.appView = Constant.VIEW_REPORT_TEAM;

        /**
         * --- start conn to serv to get data. ---
         *
         */
        try{
            new Http(
                UrlCommand.API_TEAM,
                new JSONObject(),
                new Callback(){
                    @Override
                    public void callback(JSONObject res) {
                        Tools.tttLog(UrlCommand.API_TEAM +" callback = " + res.toString());
                        if(Tools.httpErr(res)) {
                            return;
                        }
                        //ui_thread_update( res );
                    }
                    @Override
                    public void callback(String res) {}
                }
            ).start();



            JSONObject postData = new JSONObject();
            //postData.put("curr_page", currPage);
            postData.put("cur_page", curPage);
            postData.put("page_size", 50);

            new Http(
                UrlCommand.API_TEAM_LOG,
                postData,
                new Callback(){
                    @Override
                    public void callback(JSONObject res) {
                        Tools.tttLog(UrlCommand.API_TEAM_LOG +" callback = " + res.toString());
                        if(Tools.httpErr(res)) {
                            return;
                        }
                        ui_list_update( res );
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
                    Integer mem1 = _data.getInt("level_1");
                    Integer mem2 = _data.getInt("level_2");
                    Integer mem3 = _data.getInt("level_3");

                    //--- init ui ---//
                    TextView tvMem1= _view.findViewById(R.id.tv_member1);
                    TextView tvMem2 = _view.findViewById(R.id.tv_member2);
                    TextView tvMem3 = _view.findViewById(R.id.tv_member3);

                    tvMem1.setText(
                        String.format(
                            "%s",
                            Integer.toString(mem1)
                        )
                    );

                    tvMem2.setText(
                        String.format(
                            "%s",
                            Integer.toString(mem2)
                        )
                    );

                    tvMem3.setText(
                        String.format(
                            "%s",
                            Integer.toString(mem3)
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
    private void ui_list_update(JSONObject _res){
        Manager.getInst().mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    //--- 處理資料 ---//
                    JSONObject _data = _res.getJSONObject("data");
                    JSONArray _objsList = _data.getJSONArray("objs");
                    Integer allPage = _data.getInt("page_total");
                    Integer thisPage = _data.getInt("cur_page");

                    LinearLayout lyElist = _view.findViewById(R.id.ly_list);
                    Button btnLoadMore = _view.findViewById(R.id.btn_elist_load);
                    btnLoadMore.setOnClickListener( clickLoadMoreListener );

                    for ( int i = 0;  i < _objsList.length(); i++) {
                        JSONObject _json = _objsList.getJSONObject(i);
                        String desc = _json.getString("desc");
                        String time = _json.getString("create_time");
                        String amount_desc = _json.getString("amount_desc");

                        View _item = thisActivity.getLayoutInflater().inflate(R.layout.item_report_team_rd, null);
                        TextView tvCol1 = _item.findViewById(R.id.tv_col1);
                        TextView tvCol2 = _item.findViewById(R.id.tv_col2);
                        TextView tvCol3 = _item.findViewById(R.id.tv_col3);
                        tvCol1.setText( time );
                        tvCol2.setText( amount_desc );
                        tvCol3.setText( desc );


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
            curPage +=1 ;

            try{
                JSONObject postData = new JSONObject();
                postData.put("cur_page", curPage);
                postData.put("page_size", 50);

                new Http(
                    UrlCommand.API_TEAM_LOG,
                    postData,
                    new Callback(){
                        @Override
                        public void callback(JSONObject res) {
                            Tools.tttLog(UrlCommand.API_TEAM_LOG +" Load callback = " + res.toString());
                            if(Tools.httpErr(res)) {
                                return;
                            }
                            //ui_elist_load_update( res );
                            ui_list_update( res );
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



