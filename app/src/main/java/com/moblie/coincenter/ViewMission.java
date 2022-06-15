package com.moblie.coincenter;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.moblie.coincenter.Http.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class ViewMission {
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


    //private FragmentManager fragmentMgr;
    //private FragmentTopBanner fragmentTopBanner;
    private MainActivity thisActivity = Manager.getInst().mainActivity;



    public ViewMission(View view) {
        isCanClick = true;
        _view = view;


        //--- set view title ---//
        lyTop = _view.findViewById(R.id.ly_top);
        new MenuTop(lyTop).setTitle(
            Manager.getInst().mainActivity.getResources().getString(R.string.top_mission)
        );


        //--- bottom btn group ---//
        lyBottom = _view.findViewById(R.id.ly_bottom);
        new MenuBottom(lyBottom).select( Bottom.MISSION );


        //Button btn_share_copy = _view.findViewById(R.id.btn_share_copy);
        //btn_share_copy.setOnClickListener( copyClickListener );




        APP.appView = Constant.VIEW_MISSION;
        /**
         * --- start conn to serv to get data. ---
         *
         */
        try{
            // --- get share_friend --- //
            new Http(
                UrlCommand.API_MISSION,
                new JSONObject(),
                new Callback(){
                    @Override
                    public void callback(JSONObject res) {
                        Tools.tttLog(UrlCommand.API_MISSION +" callback = " + res.toString());
                        if(Tools.httpErr(res)) {
                            return;
                        }

                        ui_thread_update( res );
                        //ui_member_info( res );
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
                    String startTime = _data.getString("mission_start_time");
                    String endTime = _data.getString("mission_end_time");
                    int pushCount = _data.getInt("push_count");
                    int inviteCount = _data.getInt("invite_count");
                    //int awardIndex = 0;
                    String nowAward = "";


                    JSONArray _mission_list = _data.getJSONArray("mission_list");
                    JSONArray _mission_success_list = _data.getJSONArray("mission_success");

                    //List<Integer> _mission_success_list = (List<Integer>)_data.get("mission_success");
                    //_mission_success_list.put(1);
                    //_mission_success_list.put(2);


                    //--- init ui ---//
                    TextView tvPushCount = _view.findViewById(R.id.tv_push_count);
                    TextView tvInviteCount = _view.findViewById(R.id.tv_invite_count);
                    TextView tvStartTime = _view.findViewById(R.id.tv_start_time);
                    TextView tvEndTime = _view.findViewById(R.id.tv_end_time);
                    TextView tvNowAward = _view.findViewById(R.id.tv_now_award);
                    LinearLayout missionList = _view.findViewById(R.id.ly_mission_list);



                    //--- 設定ui ---//
                    tvStartTime.setText(
                        String.format(
                            thisActivity.getResources().getString(R.string.mission_start_time),
                            startTime
                        )
                    );

                    tvEndTime.setText(
                        String.format(
                            thisActivity.getResources().getString(R.string.mission_end_time),
                            endTime
                        )
                    );
                    tvPushCount.setText( Integer.toString(pushCount) );
                    tvInviteCount.setText( Integer.toString(inviteCount) );


                    for ( int i = 0;  i < _mission_list.length(); i++) {
                        JSONObject _json = _mission_list.getJSONObject(i);
                        //Tools.tttLog("" + _json);
                        Integer _mid = _json.getInt("mid");
                        String _push = _json.getString("push");
                        String _downLine = _json.getString("down_line");
                        String _dayFastParties = _json.getString("day_fast_parties");
                        Boolean _isSuccess = Boolean.FALSE;
                        String _missionDesc = thisActivity.getResources().getString(R.string.mission_award_unsuccess);

                        for ( int j = 0;  j < _mission_success_list.length(); j++) {
                            Integer _smid = _mission_success_list.getInt(j);
                            if ( _smid == _mid ){
                                //awardIndex = _mid;
                                nowAward = String.format(
                                    thisActivity.getResources().getString(R.string.mission_award_content1),
                                    _downLine,
                                    _dayFastParties+"%"
                                );
                                tvNowAward.setText(nowAward);
                                _isSuccess = Boolean.TRUE;;
                            }
                        }
                        View _item;
                        if(_isSuccess) {
                            _item = thisActivity.getLayoutInflater().inflate(R.layout.item_mission_success_rd, null);
                        }else{
                            _item = thisActivity.getLayoutInflater().inflate(R.layout.item_mission_rd, null);
                        }
                        View _space_item = thisActivity.getLayoutInflater().inflate( R.layout.item_space_10_rd, null );
                        //LinearLayout ly_item_mission = _view.findViewById(R.id.ly_item_mission);
                        TextView tv1 = _item.findViewById(R.id.tv1);
                        TextView tv2 = _item.findViewById(R.id.tv2);
                        TextView tv3 = _item.findViewById(R.id.tv3);


                        if(_isSuccess){
                            _missionDesc = thisActivity.getResources().getString(R.string.mission_award_success);

                        }
                        tv1.setText(
                            String.format(
                                thisActivity.getResources().getString(R.string.mission_award_content1),
                                _downLine,
                                _dayFastParties+"%"
                            )
                        );
                        tv2.setText(
                            String.format(
                                thisActivity.getResources().getString(R.string.mission_award_content2),
                                _push
                            )
                        );
                        tv3.setText( _missionDesc );



                        missionList.addView(_item);
                        missionList.addView(_space_item);
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



    ClipboardManager clipboard = null;
    ClipData clipData = null;

    //--- onclick listener ---//
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


