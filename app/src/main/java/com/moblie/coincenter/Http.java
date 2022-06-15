package com.moblie.coincenter;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Http extends Thread {
    public static final String POST = "POST";
    public static final String GET = "GET";

    private String api_url = "http://mixapi.coincenter-us.com/";
    private String base_url = "";
    private String http_method = POST;
    private JSONObject api_data = null;
    private Callback api_cb = null;



    public Http( String command_url , JSONObject jsData){
        this.base_url = Constant.URL_HTTP_ONLINE_;
        if( APP.mode == Config.ENV_DEV ){
            this.base_url = Constant.URL_DEV;
        }

        this.api_url = this.base_url + command_url;
        this.api_data = jsData;
    }

    public Http(String command_url, JSONObject jsData, Callback callback) {
        this.base_url = Constant.URL_HTTP_ONLINE_;
        if( APP.mode == Config.ENV_DEV ){
            this.base_url = Constant.URL_DEV;
        }

        this.api_url = this.base_url + command_url;
        this.api_data = jsData;
        this.api_cb = callback;
    }



    //--- use get ---//
    public Http(String _url, JSONObject jsData, String _http_method, Callback callback) {
        this.http_method = _http_method;
        this.api_url = _url;
        this.api_data = jsData;
        this.api_cb = callback;
    }



    interface Callback{
        void callback( JSONObject res );
        void callback( String res );
    }



    public void run() {
        Tools.tttLog( "http thread.");
        this.post( this.api_url, this.api_data, this.http_method );
    }


    private void post( String _url , Object _data, String _httpMethod){
        Tools.tttLog( "post = "+_data + "" );
        try {
            Tools.tttLog("api_url = " + _url);
            Tools.tttLog("api_data = " + _data);

            // ----- get post constant data ----- //
            HttpRes._ask = HttpRes._ask + 1;
            String access_token = APP.access_token;
            String sessid = APP.sessid;
            JSONObject params = this.api_data;
            int ask = HttpRes._ask;
            // ----- get post constant data end. ----- //


            if (access_token == null) access_token = "";
            if (sessid == null) sessid = "";



            HttpURLConnection urlConn = null;
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(3000);
            urlConn.setUseCaches(false);

            urlConn.setInstanceFollowRedirects(true);
            urlConn.setReadTimeout(3000);
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod(_httpMethod);
            //urlConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            urlConn.connect();



            // ----- post data ----- //
            JSONObject postJson = new JSONObject();
            postJson.put("access_token", URLEncoder.encode(access_token, "UTF-8"));
            postJson.put("sessid", URLEncoder.encode(sessid, "UTF-8"));
            postJson.put("ask", URLEncoder.encode(String.valueOf(ask), "UTF-8"));
            //postJson.put("params", URLEncoder.encode(params, "UTF-8"));
            postJson.put("params", params);

            String jsonStr = postJson.toString();
            Tools.tttLog(  "jsonStr = " + jsonStr );
            // ----- post data end. ----- //



            if (_httpMethod == POST){
                OutputStream out = urlConn.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//建立字元流物件並用高效緩衝流包裝它，便獲得最高的效率,傳送的是字串推薦用字元流，其它資料就用位元組流
                bw.write(jsonStr);//把json字串寫入緩衝區中
                bw.flush();//重新整理緩衝區，把資料傳送出去，這步很重要
                out.close();
                bw.close();//使用完關閉
            }

            Log.e(Constant.TONY_TAG, "getResponseCode = " + urlConn.getResponseCode());
            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = urlConn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String str = null;
                StringBuffer buffer = new StringBuffer();
                while ((str = br.readLine()) != null) {     //BufferedReader特有功能，一次讀取一行資料
                    buffer.append(str);
                }
                in.close();
                br.close();
                //Tools.tttLog("postdata = " + buffer.toString());

                String res_data = buffer.toString();
                //Tools.tttLog("msg = " + jsonObject.getString("msg") );
                //Tools.tttLog("msg = " + jsonObject.toString() );



                if (this.api_cb != null) {
                    if(_httpMethod == POST){
                        JSONObject jsonObject = new JSONObject( res_data );
                        this.api_cb.callback( jsonObject );
                    }else{
                        this.api_cb.callback( res_data );
                    }
                }

            }


        }catch( Exception e ){
            Tools.tttLog( "HTTP Err. = "+ e.toString() + "" );
        }

    }


}

