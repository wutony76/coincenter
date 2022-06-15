package com.moblie.coincenter;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;




public class Tools {

    // --- 確認string空值 ---
    public static boolean isCheckNull( String text ){
        if( text.toString().trim().length() == 0 ){
            return true;
        }
        return false;
    }


    // --- App_log ---
    public static void tttLog( String tttMsg ){
        if( APP.mode == Config.ENV_DEV ){
            Log.e(Constant.TONY_TAG, "dev - " + tttMsg);
        }
    }


    public static String getBaseApiURL(){
        String base_url = Constant.URL_HTTP_ONLINE_;
        if( APP.mode == Config.ENV_DEV ){
            base_url = Constant.URL_DEV;
        }
        return base_url;
    }




    // --- http_error 處理 ---
    public static boolean httpErr( JSONObject res ){
        try{
            int _code = res.getInt("code");
            if (_code != 0 ){
                String _reason = res.getString("reason");

                //--- go login ---//
                if( _reason.indexOf("NoneType") >= 0 ){
                    Func.logout(); //change to login.
                    Manager.getInst().sysToast(
                        Manager.getInst().mainActivity.getResources().getString(R.string.https_again_login)
                    );
                    return false;
                }
                if( _reason.indexOf("not auth") >= 0 ){
                    Func.logout(); //change to login.
                    Manager.getInst().sysToast(
                        Manager.getInst().mainActivity.getResources().getString(R.string.https_again_login)
                    );
                    return false;
                }
                if( _reason.indexOf("請重新登入") >= 0 ){
                    Func.logout(); //change to login.
                    Manager.getInst().sysToast(
                        Manager.getInst().mainActivity.getResources().getString(R.string.https_again_login)
                    );
                    return false;
                }


                Manager.getInst().sysToast( _reason );
                return true;
            }
            return false;

        }catch (Exception e){
            Manager.getInst().sysToast(
                Manager.getInst().mainActivity.getResources().getString(
                    R.string.https_login_res_err
                )
            );
            return true;
        }
    }



    // 判斷 Serv Status.
    public static boolean isServiceRunning(Context mContext, String className) {
        Log.e( Constant.TONY_TAG, "className =" + className );

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        //List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);

        Log.e( Constant.TONY_TAG, "serviceList =" + serviceList.size() );
        if (!(serviceList.size() > 0)) {
            return false;
        }


        for (int i = 0; i < serviceList.size(); i++) {
            String name = serviceList.get(i).service.getClassName();
            Log.e( Constant.TONY_TAG, "getClassName =" + name );

            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }


    // 判斷 App 是否啟動
    public static boolean getCurrentTask(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取当前所有存活task的信息
        List<ActivityManager.RunningTaskInfo> appProcessInfos = activityManager.getRunningTasks(Integer.MAX_VALUE);
        //遍历，若task的name与当前task的name相同，则返回true，否则，返回false
        for (ActivityManager.RunningTaskInfo process : appProcessInfos) {
            if (process.baseActivity.getPackageName().equals(context.getPackageName())
                    || process.topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }



    public static Bitmap getBitmapFromURL(String imageUrl) {
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }


}



class Func{
    public static void topBack(ImageView backBtn)  {
        int backView = APP.appView;
        switch (backView){
            case Constant.VIEW_LOGIN:
                backBtn.setOnClickListener(SelfView.login);
                break;
            case Constant.VIEW_HOME:
                backBtn.setOnClickListener(SelfView.home);
                break;
            case Constant.VIEW_MEMBER:
                backBtn.setOnClickListener(SelfView.member);
                break;
            default:
                backBtn.setOnClickListener(SelfView.home);
                break;
        }
    }

    public static void logout(){
        APP.access_token = "";
        APP.sessid = "";
        new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_login );
    }
}




class SelfView {

    public static View.OnClickListener login = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Tools.tttLog( "Goto HOME." );
            if( APP.appView == Constant.VIEW_LOGIN ) return;
            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_login );
        }
    };

    public static View.OnClickListener bswitch = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Tools.tttLog( "Goto BSWITCH." );
            if( APP.appView == Constant.VIEW_BSWITCH ) return;
            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_bswitch );
        }
    };

    public static View.OnClickListener recharge = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Tools.tttLog( "Goto RECHARGE." );
            if( APP.appView == Constant.VIEW_RECHARGE ) return;
            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_recharge );
        }
    };
    public static View.OnClickListener mission = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Tools.tttLog( "Goto MISSION." );
            if( APP.appView == Constant.VIEW_MISSION ) return;
            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_mission );
        }
    };


    public static View.OnClickListener home = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( APP.appView == Constant.VIEW_HOME ) return;
            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_home );
        }
    };
    public static View.OnClickListener member = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if( APP.appView == Constant.VIEW_MEMBER ) return;
            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_member );
        }
    };
    public static View.OnClickListener share = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Tools.tttLog( "Goto HOME." );
            if( APP.appView == Constant.VIEW_SHARE ) return;
            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_share );
        }
    };
    public static View.OnClickListener logout = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Tools.tttLog("click selfview.");

            Func.logout();
            Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.https_logout_success)
            );


            /*
            APP.access_token = "";
            APP.sessid = "";
            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_login );

            Manager.getInst().sysToast(
                Manager.getInst().mainActivity.getResources().getString(R.string.https_logout_success)
            );
            */
        }
    };



}




