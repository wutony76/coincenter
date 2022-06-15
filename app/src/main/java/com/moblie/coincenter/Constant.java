package com.moblie.coincenter;



public class Constant {
    public static final String URL_DEV = "http://3.36.17.162:8203/";
    public static final String URL_HTTP_ONLINE_ = "http://mixapi.coincenter-us.com/";
    public static final String URL_HTTPS_ONLINE = "https://mixapi.coincenter-us.com/";


    /*  --- sms --- */
    public static final String SMS_REGISTER_SERV = "android.provider.Telephony.SMS_RECEIVED";
    public static final String APP_REGISTER_SERV = "app.SMS_RECEIVED";
    public static final int PRESSION_REQCODE = 100;


    /*  --- log tag --- */
    public static final String TONY_TAG = "ttt";
    public static final int ly_item_wallet_rd = R.layout.item_wallet_rd;

    public static final int ly_login = R.layout.login;
    public static final int ly_register = R.layout.register;

    public static final int ly_recharge = R.layout.recharge;
    public static final int ly_recharge_rule = R.layout.recharge_rule;
    public static final int ly_bswitch = R.layout.bswitch;
    public static final int ly_bswitch_log = R.layout.bswitch_log;
    public static final int ly_reinvest = R.layout.reinvest;
    public static final int ly_reinvest_log = R.layout.reinvest_log;
    public static final int ly_withdrawal = R.layout.withdrawal;
    public static final int ly_withdrawLog = R.layout.withdraw_log;
    public static final int ly_elist = R.layout.elist;
    public static final int ly_report_my = R.layout.report_my;
    public static final int ly_report_team = R.layout.report_team;
    public static final int ly_change_password = R.layout.changepass;
    public static final int ly_forget_password = R.layout.forgetpass;



    public static final int ly_home = R.layout.home;
    public static final int ly_share = R.layout.share;
    public static final int ly_member = R.layout.member;
    public static final int ly_mission = R.layout.mission;





    /*  --- app view --- */
    public static final int VIEW_LOGIN = 0x000001;
    public static final int VIEW_REGISTER = 0x000002;
    //public static final int VIEW_CHANGEPASS = 0x000003;
    public static final int VIEW_FORGET_PASSWORD = 0x00003;

    public static final int VIEW_RECHARGE = 0x000021;
    public static final int VIEW_BSWITCH = 0x000022;
    public static final int VIEW_BSWITCH_LOG = 0x000023;
    public static final int VIEW_REINVEST = 0x000024;
    public static final int VIEW_REINVEST_LOG = 0x000025;
    public static final int VIEW_WITHDRAWAL = 0x000026;
    public static final int VIEW_WITHDRAW_LOG = 0x000027;
    public static final int VIEW_ELIST = 0x000028;
    public static final int VIEW_REPORT_MY = 0x000029;
    public static final int VIEW_REPORT_TEAM = 0x000030;
    public static final int VIEW_CHANGE_PASSWORD = 0x000031;



    public static final int VIEW_HOME = 0x000011;
    public static final int VIEW_MISSION = 0x000012;
    public static final int VIEW_SHARE = 0x000013;
    public static final int VIEW_MEMBER = 0x000014;
}


class Config{
    public static final String ENV_DEV = "DEV";
    public static final String ENV_ONLINE = "ONLINE";
}



class UrlCommand{
    public static final String API_CHECK_SESSION = "check_session";
    public static final String API_CAPTCHA_CLEAR = "captcha/clear";
    public static final String API_CAPTCHA_IMG = "captcha/image";

    /*  --- api url --- */

    public static final String API_LOGIN = "login";
    public static final String API_REGISTER = "auth/register_v2";
    public static final String API_PHONE_SPEND = "auth/shortmsg_send_verify_new";
    public static final String API_CHANGE_PASSWORD = "auth/changepassword/change";

    public static final String API_USER_INFO = "user/get_info";
    public static final String API_COIN_LIST = "user/coin_list";
    public static final String API_SHARE = "user/get_share";
    public static final String API_MISSION = "user/mission";
    public static final String API_RECHARGE = "user/recharge";
    public static final String API_RECHARGE_FINISH = "user/recharge_finish";
    public static final String API_BSWITCH = "user/promotion_exchange_prepare";
    public static final String API_BSWITCH_EXCHANGE = "user/promotion_exchange";
    public static final String API_BSWITCH_EXCHANGE_LOG = "user/promotion_exchange_log";
    public static final String API_REINVEST_INFO = "user/reinvest_info";
    public static final String API_REINVEST = "user/reinvest";
    public static final String API_REINVEST_LOG = "user/reinvest_log";
    public static final String API_WITHDRAW_PREPARE = "user/withdraw_prepare";
    public static final String API_WITHDRAW = "user/withdraw";
    public static final String API_WITHDRAW_LOG = "user/withdrawlog";
    public static final String API_ELIST = "user/finance_list";
    public static final String API_REPORT_MY = "user/my_report";
    public static final String API_TEAM = "user/my_team";
    public static final String API_TEAM_LOG = "user/my_team_log";

}


class APP{
    public static String mode = null;

    /*  --- app variable --- */
    public static int appView = -1;

    /*  --- Constant data --- */
    public static String access_token = null;
    public static String sessid = null;

}

class HttpRes{
    public static int _ask = 0;
}


class MemberLevel{
    public static String no = Manager.getInst().mainActivity.getResources().getString(R.string.member_no) ;
    public static String lv0 = Manager.getInst().mainActivity.getResources().getString(R.string.member_lv0) ;
    public static String lv1 = Manager.getInst().mainActivity.getResources().getString(R.string.member_lv1) ;
    public static String lv2 = Manager.getInst().mainActivity.getResources().getString(R.string.member_lv2) ;
    public static String lv3 = Manager.getInst().mainActivity.getResources().getString(R.string.member_lv3) ;
    public static String lv4 = Manager.getInst().mainActivity.getResources().getString(R.string.member_lv4) ;
}


class Bottom{
    public static int HOME = R.string.bottom_home;
    public static int MISSION = R.string.bottom_mission ;
    public static int SHARE = R.string.bottom_share ;
    public static int MEMBER = R.string.bottom_member;
}








