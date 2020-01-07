package nk.bluefrog.library.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import nk.bluefrog.library.R;
import nk.bluefrog.library.utils.Helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


/**
 * <p>
 * - Implement ResponseListener to your activity
 * <p>
 * - send corresponding data in constructor
 * <p>
 * Ex :
 * <p>
 * RestService callRestApi = new RestService(Activity.this, .. , .. , .. , ..);
 * <p>
 * you can append parmeters to url through setParams method :
 * callRestApi.setParams(..);
 * <p>
 * you can set time out to call api through setTimeOut method :
 * callRestApi.setTimeOut(3000);
 * <p>
 * finally add this line :
 * callRestApi.execute();
 * <p>
 * you will get response in onSuccess if data came from server without issues otherwise onError will call
 *
 * @author Subhash
 */
public class RestService extends AsyncTask<Void, Integer, String> {

    private ProgressDialog progressDialog;
    private String ws;
    private Context context;
    private ResponseListener iResponseCallBack;
    private int responseCode;
    private boolean showPD;
    private String message;
    private String methodType;

    private final int ERROR_NETWORK = 1;
    private final int ERROR_TIME_OUT = 2;
    private final int ERROR_HTTP = 3;
    private int resultCode = 0;
    private Map<String, String> postParams;
    private int timeOutInMillis = 0;
    private String httpErrorMsg = "";

    /**
     * @param context      activity context Ex: Activity.this
     * @param listener     ResponseListener
     * @param responseCode request code that you expected in success or failure response
     * @param url          Base url
     * @param methodType   Type of method GET or POST
     */
    public RestService(Context context, ResponseListener listener, int responseCode, String url, String methodType) {
        this(context, listener, responseCode, url, methodType, true);
    }

    /**
     * @param context      activity context Ex: Activity.this
     * @param listener     ResponseListener
     * @param responseCode request code that you expected in success or failure response
     * @param url          Base url
     * @param showProgress need to show progress dialog or not
     * @param methodType   Type of method GET or POST
     */
    public RestService(Context context, ResponseListener listener, int responseCode, String url, String methodType, boolean showProgress) {
        this(context, listener, responseCode, url, methodType, showProgress, null);
    }

    /**
     * @param context      activity context Ex: Activity.this
     * @param listener     ResponseListener
     * @param responseCode request code that you expected in success or failure response
     * @param url          Base url
     * @param message      message that you want to display on progress dialog
     * @param methodType   Type of method GET or POST
     */
    public RestService(Context context, ResponseListener listener, int responseCode, String url, String methodType, String message) {
        this(context, listener, responseCode, url, methodType, true, message);
    }

    /**
     * @param context      activity context Ex: Activity.this
     * @param listener     ResponseListener
     * @param responseCode request code that you expected in success or failure response
     * @param url          Base url
     * @param showProgress need to show progress dialog or not
     * @param message      message that you want to display on progress dialog
     * @param methodType   Type of method GET or POST
     */
    public RestService(Context context, ResponseListener listener, int responseCode, String url, String methodType, boolean showProgress, String message) {

        this.context = context;
        this.ws = url;
        this.iResponseCallBack = listener;
        this.responseCode = responseCode;
        this.showPD = showProgress;
        this.message = message;
        this.methodType = methodType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (showPD) {
            try {
                progressDialog = new ProgressDialog(context);
                progressDialog.setCancelable(false);
                if (message == null) {
//                    progressDialog.setMessage("Loading data");
                    progressDialog.setMessage(context.getString(R.string.please_wait_serverhit));
                } else {
                    progressDialog.setMessage(message);
                }
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String doInBackground(Void... params) {

        String response = null;
        if (Helper.isNetworkAvailable(context)) {
            try {
                response = callApi(ws);
            } catch (IOException e) {
                resultCode = ERROR_HTTP;
                httpErrorMsg = e.getMessage();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            resultCode = ERROR_NETWORK;
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (iResponseCallBack != null && response != null) {
            iResponseCallBack.onSuccess(responseCode, response);
        } else {
            if (iResponseCallBack == null) {
                Log.e("Error", "Listener not implemented");
            } else if (resultCode == ERROR_NETWORK) {
                iResponseCallBack.onError(responseCode, "Network is not available");
            } else if (resultCode == ERROR_TIME_OUT) {
                iResponseCallBack.onError(responseCode, "Time out");
            } else if (resultCode == ERROR_HTTP) {
                iResponseCallBack.onError(responseCode, httpErrorMsg);
            } else {
                iResponseCallBack.onError(responseCode, "Unable to get data");
            }
        }

    }

    private String encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            if (params != null) {
                boolean first = true;
                for (Map.Entry<String, String> entry : params.entrySet()) {

                    if (first)
                        first = false;
                    else
                        encodedParams.append("&");

                    encodedParams.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    encodedParams.append("=");
                    encodedParams.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
            }
            return encodedParams.toString();
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }


    private String callApi(String ws) throws IOException {
        String api = ws;
        if (methodType.equals(ResponseListener.GET)) {
            api = api + encodeParameters(postParams, "UTF-8");
        }
        URL url = new URL(api);
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            if (timeOutInMillis > 0) {
                //set timeout to read input stream
                connection.setReadTimeout(timeOutInMillis);
                //timeout to establish connection with server
                connection.setConnectTimeout(timeOutInMillis);
            }
            connection.setRequestMethod(methodType);
            connection.setDoInput(true);

            if (methodType.equals(ResponseListener.POST)) {
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(encodeParameters(postParams, "UTF-8"));
                writer.flush();
                writer.close();
                os.close();
            }
            //open communication link
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
//                throw new IOException("Http error code :" + responseCode);
                resultCode = ERROR_HTTP;
                httpErrorMsg = "Connection error " + responseCode;
                return null;
            }
            //get Input stream
            inputStream = connection.getInputStream();
            if (inputStream != null) {
                result = readInputStream(inputStream);
            }

        } catch (SocketTimeoutException e) {
            //When time outdated
            e.printStackTrace();
            resultCode = ERROR_TIME_OUT;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private String readInputStream(InputStream in) throws IOException {

        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }
        return total.toString();
    }

    /**
     * @param postParams parameter map that you want to append to url
     * @return RestService context
     */
    public RestService setParams(Map<String, String> postParams) {
        this.postParams = postParams;
        return this;
    }

    /**
     * Set time out to your request
     *
     * @param timeoutInMillis time in milliseconds
     * @return RestService context
     */
    public RestService setTimeOut(int timeoutInMillis) {
        this.timeOutInMillis = timeoutInMillis;
        return this;
    }

}
