package com.moblie.coincenter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


public class Actions {

    public Activity _activity;
    public Actions(Activity activity){
        _activity = activity;
    }





    public void changeView( int _layout ) {
        main( _layout );
    }


    public void changeView( int _layout, String _arg ) {
        main( _layout, _arg );
    }



    private void main( int _layout ){
        //--- 載入指定xml layout ---//
        View view = _activity.getLayoutInflater().inflate(_layout, null);


        //--- 確保UI 更新再 UI thread. ---//
        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _activity.setContentView(view);

                switch (_layout){
                    case Constant.ly_login:
                        Tools.tttLog("view_login");
                        new ViewLogin(view);
                        break;
                    case  Constant.ly_register:
                        Tools.tttLog("view_register");
                        new ViewRegister(view);
                        break;

                    case Constant.ly_forget_password:
                        new ViewForgetPassword(view);
                        break;


                    //--- center view ---//
                    case Constant.ly_recharge:
                        Tools.tttLog("view_recharge");
                        new ViewRecharge(view);
                        break;
                    case Constant.ly_bswitch:
                        Tools.tttLog("view_bswitch");
                        new ViewBswitch(view);
                        break;
                    case Constant.ly_reinvest:
                        new ViewReinvest(view);
                        break;
                    case Constant.ly_withdrawal:
                        new ViewWithdrawal(view);
                        break;
                    case Constant.ly_elist:
                        new ViewElist(view);
                        break;
                    case Constant.ly_report_my:
                        new ViewReportMy(view);
                        break;
                    case Constant.ly_report_team:
                        new ViewReportTeam(view);
                        break;
                    case Constant.ly_change_password:
                        new ViewChangePassword(view);
                        break;




                    //--- bottom menu ---//
                    case Constant.ly_home:
                        Tools.tttLog("view_home");
                        new ViewHome(view);
                        break;
                    case Constant.ly_mission:
                        Tools.tttLog("view_mission");
                        new ViewMission(view);
                        break;
                    case Constant.ly_share:
                        Tools.tttLog("view_share");
                        new ViewShare(view);
                        break;
                    case Constant.ly_member:
                        Tools.tttLog("view_member");
                        new ViewMember(view);
                        break;

                }

            }
        });

    }

    private void main( int _layout, String _arg ) {
        //--- 載入指定xml layout ---//
        View view = _activity.getLayoutInflater().inflate(_layout, null);

        //--- 確保UI 更新再 UI thread. ---//
        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _activity.setContentView(view);

                switch (_layout){
                    //--- center view ---//
                    case Constant.ly_bswitch_log:
                        new ViewBswitchLog(view, _arg);
                        break;
                    case Constant.ly_withdrawLog:
                        new ViewWithdrawLog(view, _arg);
                        break;
                    case Constant.ly_reinvest_log:
                        new ViewReinvestLog(view, _arg);
                        break;
                }

            }
        });
    }



}
