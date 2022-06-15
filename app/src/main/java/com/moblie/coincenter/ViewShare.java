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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class ViewShare {
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



    public ViewShare(View view) {
        isCanClick = true;
        _view = view;


        //--- set view title ---//
        lyTop = _view.findViewById(R.id.ly_top);
        new MenuTop(lyTop).setTitle(
            Manager.getInst().mainActivity.getResources().getString(R.string.top_share)
        );


        //--- bottom btn group ---//
        lyBottom = _view.findViewById(R.id.ly_bottom);
        new MenuBottom(lyBottom).select( Bottom.SHARE );


        Button btn_share_copy = _view.findViewById(R.id.btn_share_copy);
        btn_share_copy.setOnClickListener( copyClickListener );



        APP.appView = Constant.VIEW_SHARE;


        /**
         * --- start conn to serv to get data. ---
         *
         */
        try{
            // --- get share_friend --- //
            new Http(
                UrlCommand.API_SHARE,
                new JSONObject(),
                new Callback(){
                    @Override
                    public void callback(JSONObject res) {
                        Tools.tttLog(UrlCommand.API_SHARE +" callback = " + res.toString());

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
            @Override
            public void run() {
                try {
                    //--- 處理資料 ---//
                    JSONObject _data = _res.getJSONObject("data");
                    String _url = _data.getString("share_url");
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap qrImg = encoder.encodeBitmap(
                        _url,
                        BarcodeFormat.QR_CODE,250,250);


                    //--- init ui ---//
                    TextView tvShareUrl = _view.findViewById(R.id.tv_share_url);
                    ImageView imgShareQR = _view.findViewById(R.id.img_share_QR);

                    //--- 設定ui ---//
                    tvShareUrl.setText(_url);
                    imgShareQR.setImageBitmap(qrImg);


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


