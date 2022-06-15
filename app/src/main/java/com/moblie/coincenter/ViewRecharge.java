package com.moblie.coincenter;

import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
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

import org.json.JSONException;
import org.json.JSONObject;


public class ViewRecharge {
    public View _view;
    public Boolean isCanClick = true;
    private MainActivity _activity ;


    public ViewRecharge(View view) {
        isCanClick = true;
        _activity = Manager.getInst().mainActivity;
        _view = view;



        View _view_rule = _activity.getLayoutInflater().inflate( R.layout.recharge_rule, null);
        _activity.setContentView(_view_rule);

        Button btnOk = _view_rule.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener( clickCheckRule );


        APP.appView = Constant.VIEW_RECHARGE;
    }



    //--- ui_update ---//
    private void ui_thread_update(JSONObject _res){
        Manager.getInst().mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //--- 處理資料 ---//
                    JSONObject _info = _res.getJSONObject("info");
                    String _address = _info.getString("address");
                    Tools.tttLog( "_address = " + _address );

                    //String _url = _data.getString("share_url");
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap qrImg = encoder.encodeBitmap(
                        _address,
                        BarcodeFormat.QR_CODE,250,250);


                    //--- init ui ---//
                    TextView tvAddress = _view.findViewById(R.id.tv_address);
                    ImageView imgAddrQR = _view.findViewById(R.id.img_addr_QR);


                    //--- 設定ui ---//
                    tvAddress.setText(_address);
                    imgAddrQR.setImageBitmap(qrImg);


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }
    //--- ui_update end. ---//



    LinearLayout lyTop;


    //--- onclick listener ---//
    View.OnClickListener clickCheckRule = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Tools.tttLog("CLICK.");
            _activity.setContentView(_view);

            //--- set view title ---//
            lyTop = _view.findViewById(R.id.ly_top);
            MenuTop menuTop = new MenuTop(lyTop);
            menuTop.setTitle( _activity.getResources().getString(R.string.top_recharge) );
            ImageView backBtn = menuTop.getBackBtn();
            backBtn.setOnClickListener(SelfView.home);



            //--- ui ---//
            Button btnCopyAddr = _view.findViewById(R.id.btn_addr_copy);
            Button btnRechargeOk = _view.findViewById(R.id.btn_recharge_ok);
            btnCopyAddr.setOnClickListener( clickCopyAddr );
            btnRechargeOk.setOnClickListener( clickRechargeSuccess );


            /**
             * --- start conn to serv to get data. ---
             *
             */
            try{
                JSONObject postData = new JSONObject();
                postData.put("coin_name", "USDT_TRC20");
                //postData.put("access_token", APP.access_token);

                // --- get share_friend --- //
                new Http(
                    UrlCommand.API_RECHARGE,
                    postData,
                    new Callback(){
                        @Override
                        public void callback(JSONObject res) {
                            Tools.tttLog(UrlCommand.API_RECHARGE +" callback = " + res.toString());

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
    };


    ClipboardManager clipboard = null;
    ClipData clipData = null;
    View.OnClickListener clickCopyAddr = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            Tools.tttLog( "click to COPY." );
            try{
                TextView tvAddress = _view.findViewById(R.id.tv_address);
                //Tools.tttLog( "click to COPY." + tvAddress.getText().toString().length()  );
                clipboard = (ClipboardManager) Manager.getInst().mainActivity.getSystemService( Context.CLIPBOARD_SERVICE );
                clipData = ClipData.newPlainText(null, tvAddress.getText().toString());
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
    View.OnClickListener clickRechargeSuccess = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {

            /**
             * --- start conn to serv to get data. ---
             *
             */
            try{
                JSONObject postData = new JSONObject();
                postData.put("type", "basic");


                // --- get share_friend --- //
                new Http(
                    UrlCommand.API_RECHARGE_FINISH,
                    postData,
                    new Callback(){
                        @Override
                        public void callback(JSONObject res) {
                            Tools.tttLog(UrlCommand.API_RECHARGE_FINISH +" callback = " + res.toString());

                            if(Tools.httpErr(res)) {
                                return;
                            }
                            if( APP.appView == Constant.VIEW_HOME ) return;
                            new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_home );
                        }

                        @Override
                        public void callback(String res) {}
                    }
                ).start();

            }catch (Exception e){
                Tools.tttLog( "Err. " + e.toString() );
                if( APP.appView == Constant.VIEW_HOME ) return;
                new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_home );
            }

            //if( APP.appView == Constant.VIEW_HOME ) return;
            //new Actions( Manager.getInst().mainActivity ).changeView( Constant.ly_home );
        }
    };

}


