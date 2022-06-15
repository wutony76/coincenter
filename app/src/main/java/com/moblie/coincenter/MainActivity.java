package com.moblie.coincenter;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.InputStream;


//public class MainActivity extends AppCompatActivity {
public class MainActivity extends Activity {
    //private NoteReceiver smsServ;
    //private Handler mHandler;

    private NoteReceiver noteReceiver;
    public ConnServ connServ = null ;
    public Boolean isRegister;
    //private Activity selfActivity;
    //public List<String> asynThreaData = null;


    /*--- UI BUTTON ---*/
    private Button statusBtn;
    private Button connBtn;
    private Button testBtn;
    private Button backStartBtn;
    private Button closeBtn;
    private Button confBtn;
    private Button scanningBtn;
    private Button clearBtn;

    private Button postOpenBtn;
    private Button postCloseBtn;


    private EditText urlEditText;
    private EditText keyEditText;
    private EditText nameEditText;
    private EditText statusRecord;

    //private String recordMsg;
    //private String resumeMsg;

    Activity _activity = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        APP.mode = Config.ENV_DEV;
        //APP.mode = Config.ENV_ONLINE;



        // test code --------------------------------------------------------
        //setContentView(R.layout.activity_main);
        //setContentView(R.layout.main);
        //setContentView(R.layout.login);
        //R.layout.login to view
        //View logoin = getLayoutInflater().inflate(R.layout.login, null);
        //setContentView( R.layout.home );


        _activity = MainActivity.this;
        Tools.tttLog( " Start my app." );
        Tools.tttLog( "---This app OnCreate start.---" );
        //Log.e( Constant.TONY_TAG, "---This app OnCreate start.---" );





        //logic--------------------------------------------------------
        Manager.getInst().setContext(this);

        //permission---------------------------------------------------
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ){
            Log.e(Constant.TONY_TAG, "check permission");

            ActivityCompat.requestPermissions(
                Manager.getInst().mainActivity,
                new String[]{
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS
                },
                Constant.PRESSION_REQCODE
            );
        }





        //use phone serv--------------------------------------------------------
        //smsServ = new NoteReceiver( Manager.getInst().mainActivity, mHandler);
        IntentFilter itFilter = new IntentFilter( Constant.SMS_REGISTER_SERV );
        itFilter.setPriority(1000); //設定優先順序
        noteReceiver = new NoteReceiver();
        isRegister = noteReceiver.isRegister;
        registerReceiver( noteReceiver, itFilter );
    }


    //--- PROCESS --------------------------------------------------------
    public void ui_addRecord( String record ){
        String allRecord = statusRecord.getText().toString().trim();
        allRecord = String.format( "%1$s \n", record )+ allRecord;
        //Log.e( Constant.TONY_TAG,  allRecord );

        String finalAllRecord = allRecord;
        Manager.getInst().mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusRecord.setText(finalAllRecord);
            }
        });
    }

    /*--- clickListener ---*/
    private Button.OnClickListener statusClickListener = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.e( Constant.TONY_TAG, "---click.status.---" );
            ui_addRecord( getResources().getString(R.string.app_hr) );

            Boolean isRun = Tools.getCurrentTask(Manager.getInst().mainActivity);
            Log.e(Constant.TONY_TAG, "isRun = "+isRun);
            String isPostConnStatus = "";
            String isRegisterStatus = "";
            String strStatus = "";

            if (isRun){
                strStatus = String.format(
                    getResources().getString(R.string.app_status),
                    getResources().getString(R.string.app_status_open)
                );
            }else{
                strStatus = String.format(
                    getResources().getString(R.string.app_status),
                    getResources().getString(R.string.app_status_close)
                );
            }

            if (connServ != null){
                isPostConnStatus = "訊息Post程序:-關閉-";
            }else{
                isPostConnStatus = "訊息Post程序:-開啟-";
            }

            if(isRegister){
                isRegisterStatus = "-開啟-";
            }else{
                isRegisterStatus = "-關閉-";
            }


            ui_addRecord( isPostConnStatus );
            ui_addRecord( " 廣播接收器 : "+ isRegisterStatus );
            ui_addRecord( strStatus );

            ui_addRecord( getResources().getString(R.string.app_hr) );
        }
    };

    private Button.OnClickListener connClickListener = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.e( Constant.TONY_TAG, "---click.re_conn_register.---" );
            ui_addRecord( getResources().getString(R.string.app_hr) );
            //Boolean isServ = Tools.isServiceRunning(Manager.getInst().mainActivity, "NoteReceiver");
            //Boolean isServ = Tools.isServiceRunning(Manager.getInst().mainActivity, Constant.REGISTER_SERV);
            //Log.e(Constant.TONY_TAG, ""+ isServ);

            /* un register Serv. */
            unregisterReceiver(noteReceiver);
            ui_addRecord( getResources().getString(R.string.app_un_register) );


            new Thread(new Runnable() {
                @Override
                public void run() {
                    /* register Serv. */
                    IntentFilter itFilter = new IntentFilter( Constant.SMS_REGISTER_SERV );
                    noteReceiver = new NoteReceiver();
                    registerReceiver( noteReceiver, itFilter );

                    Manager.getInst().mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ui_addRecord( getResources().getString(R.string.app_register) );
                            ui_addRecord( getResources().getString(R.string.app_hr) );

                            Manager.getInst().sysToast(
                                Manager.getInst().mainActivity.getResources().getString(R.string.app_register)
                            );
                        }
                    });
                    /* register Serv. END */
                }
            }).start();

        }
    };

    private Button.OnClickListener testClickListener = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.e( Constant.TONY_TAG, "---click.test_conn_register.---" );
            ui_addRecord( getResources().getString(R.string.app_hr) );


            IntentFilter itFilter = new IntentFilter( Constant.APP_REGISTER_SERV );
            NoteReceiver newReceiver = new NoteReceiver();
            registerReceiver( newReceiver, itFilter );

            ui_addRecord( getResources().getString(R.string.app_test_register) );


            /*
            * --- 廣播識別碼 ---
            * 不能使用 "android.provider.Telephony.SMS_RECEIVED"
            * "android.provider.Telephony.SMS_RECEIVED" 是系統內部服務
            *
            * */
            Intent it = new Intent( Constant.APP_REGISTER_SERV ); //設定廣播識別碼
            it.putExtra("sender_name", "TEST-Xavier"); //設定廣播夾帶參數
            sendBroadcast(it); //發送廣播訊息


            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        //Manager.getInst().mainActivity.runFunc();
                        unregisterReceiver(newReceiver); //撤銷廣播接收器
                        ui_addRecord( getResources().getString(R.string.app_test_unregister) );
                        ui_addRecord( getResources().getString(R.string.app_hr) );

                        //Thead.sleep(4*1000);
                        //doTask();
                        Log.e( Constant.TONY_TAG, "---click.test_close_register. END---" );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    };

    private Button.OnClickListener backStartClickListener = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.e( Constant.TONY_TAG, "---click.back_start.---" );

            ui_addRecord( getResources().getString(R.string.app_hr) );
            ui_addRecord( String.format(
                getResources().getString(R.string.app_status),
                getResources().getString(R.string.app_back_start)
            ));


            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识    
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);

            ui_addRecord( getResources().getString(R.string.app_hr) );
            //Manager.getInst().mainActivity.finish();



            Manager.getInst().sysToast(
                Manager.getInst().mainActivity.getResources().getString(R.string.app_back_start)
            );

        }
    };

    private Button.OnClickListener closeClickListener = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.e( Constant.TONY_TAG, "---click.back_start.---" );

            ui_addRecord( String.format(
                getResources().getString(R.string.app_status),
                getResources().getString(R.string.app_status_close)
            ));
            Manager.getInst().sysToast(
                Manager.getInst().mainActivity.getResources().getString(R.string.app_status_close)
            );

            Manager.getInst().mainActivity.onDestroy();
            Manager.getInst().mainActivity.finish();
            System.exit(0);
        }
    };

    private Button.OnClickListener confClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e( Constant.TONY_TAG, "---click. SAVE conf.---" );

            //-- save data --
            String urlStr = urlEditText.getText().toString().trim();
            String keyStr = keyEditText.getText().toString().trim();
            String nameStr = nameEditText.getText().toString().trim();

            if ( urlStr.isEmpty() || keyStr.isEmpty() || nameStr.isEmpty() ){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.app_et)
                );
                return;
            }

            SharedPreferences setting = getSharedPreferences("SMS_CONF", MODE_PRIVATE);
            setting.edit().putString("URL", urlStr) .commit();
            setting.edit().putString("KEY", keyStr) .commit();
            setting.edit().putString("NAME", nameStr) .commit();

            Log.e( Constant.TONY_TAG, "---click. SAVE urlStr.---" +urlStr );
            Log.e( Constant.TONY_TAG, "---click. SAVE keyStr.---"+keyStr );
            Log.e( Constant.TONY_TAG, "---click. SAVE nameStr.---"+nameStr );



            //--- UI msg update ---//
            ui_addRecord( getResources().getString(R.string.app_hr) );
            ui_addRecord( "設定實名: " + nameStr );
            ui_addRecord( "設定密鑰: " + keyStr );
            ui_addRecord( "設定URL: " + urlStr );

            ui_addRecord( " --設定POST連線程序 CONF-- " );
            ui_addRecord( getResources().getString(R.string.app_hr) );


            //--- Re conn Serv ---//
            //connServ.interrupt();
            //connServ = null;
            connServ = new ConnServ();
            connServ.start();

            Manager.getInst().sysToast(
                Manager.getInst().mainActivity.getResources().getString(R.string.app_re_conn)
            );


        }
    };

    private Button.OnClickListener scanningClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e( Constant.TONY_TAG, "---click. Scanning.---" );

            if( Manager.getInst().mainActivity.connServ.getPostLen() > 0 ){
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.app_post_send_unsuccess)
                );
                return;
            }
            new ScanningSMS().start();

        }
    };

    private Button.OnClickListener clearnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e( Constant.TONY_TAG, "---click. Clearn.---" );
            statusRecord.setText("");

            Manager.getInst().sysToast(
                Manager.getInst().mainActivity.getResources().getString(R.string.app_clear_record)
            );
        }
    };

    private Button.OnClickListener appopenClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e( Constant.TONY_TAG, "---click. Appopen.---" );

            if ( connServ != null ) {
                Manager.getInst().sysToast(
                    Manager.getInst().mainActivity.getResources().getString(R.string.app_runpost_conn)
                );
                return;
            }
            connServ = new ConnServ();
            connServ.start();

            Manager.getInst().sysToast(
                Manager.getInst().mainActivity.getResources().getString(R.string.app_openpost_conn)
            );
        }
    };

    private Button.OnClickListener appcloseClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.e( Constant.TONY_TAG, "---click. Appclose.---" );

            if ( connServ != null ){
                connServ.interrupt();
                connServ = null;
            }

            Manager.getInst().sysToast(
                Manager.getInst().mainActivity.getResources().getString(R.string.app_closepost_conn)
            );
        }
    };




    //--- CYCLE --------------------------------------------------------
    @Override
    public void onBackPressed(){
        Tools.tttLog( "---click.onBackPressed.---");
        super.onBackPressed();
    }


    @Override
    protected void onStop(){
        super.onStop();
        Tools.tttLog( "---click.onStop.---");
    }


    @Override
    protected void onStart(){
        super.onStart();
        Tools.tttLog( "---click.onStart.---");
        if ( connServ == null ){
            //connServ = new ConnServ();
            //connServ.start();
        }


        //--- load view ---//
        if (APP.appView != -1 || APP.access_token != null || APP.sessid != null){
            new Actions( _activity ).changeView(Constant.ly_home);
            return;
        }
        new Actions( _activity ).changeView(Constant.ly_login);
    }


    @Override
    protected void onRestart(){
        super.onRestart();
        Tools.tttLog( "---click.onRestart.---");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Tools.tttLog( "---click.onResume.---");
    }


    @Override
    protected void onPause(){
        super.onPause();
        Tools.tttLog( "---click.onPause.---");
        //resumeMsg = statusRecord.getText().toString().trim();
        //Log.e(Constant.TONY_TAG, "---click.resumeMsg.---" +resumeMsg);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tools.tttLog( "---click.onDestroy.---");


        if ( connServ != null ){
            connServ.interrupt();
            connServ = null;
        }
    }

}

