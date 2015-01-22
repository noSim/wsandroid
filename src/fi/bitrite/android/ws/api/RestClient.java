package fi.bitrite.android.ws.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import fi.bitrite.android.ws.R;
import fi.bitrite.android.ws.auth.NoAccountException;
import fi.bitrite.android.ws.auth.http.HttpAuthenticationFailedException;
import fi.bitrite.android.ws.auth.http.HttpAuthenticator;
import fi.bitrite.android.ws.auth.http.HttpSessionContainer;
import fi.bitrite.android.ws.util.Tools;
import fi.bitrite.android.ws.util.http.HttpException;
import fi.bitrite.android.ws.util.http.HttpUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for classes that use GET to either scrape the WS website for information
 * or interface with the REST API.
 */
public class RestClient {

    public static final String TAG = "RestClient";

    public JSONObject get(String simpleUrl) throws HttpException, JSONException, URISyntaxException, IOException {
        HttpClient client = HttpUtils.getDefaultClient();
        String json;
        JSONObject jsonObj;
        int responseCode;

        String url = HttpUtils.encodeUrl(simpleUrl);
        HttpGet get = new HttpGet(url);
        HttpContext context = HttpSessionContainer.INSTANCE.getSessionContext();

        HttpResponse response = client.execute(get, context);
        HttpEntity entity = response.getEntity();
        responseCode = response.getStatusLine().getStatusCode();

        json = EntityUtils.toString(entity, "UTF-8");

        if (responseCode != HttpStatus.SC_OK) {
            Log.i(TAG, "Non-200 HTTP response(" + Integer.toString(responseCode) + " for URL " + url);
            switch (responseCode) {
                case HttpStatus.SC_UNAUTHORIZED:    // 401, typically when not logged in
                    doRequiredAuth();
                    return get(url);
                case HttpStatus.SC_FORBIDDEN:
                case HttpStatus.SC_NOT_ACCEPTABLE:  // 406, Typically trying to log out when not logged in
                default:
                    throw new HttpException(Integer.toString(responseCode));
            }
        }

        jsonObj = new JSONObject(json);
        client.getConnectionManager().shutdown();

        return jsonObj;
    }

    // Bare post with no params
    public JSONObject post(String url, int recursionLimit) throws HttpException, IOException, JSONException, RestClientRecursionException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        return post(url, params, recursionLimit);
    }

    // Post with params
    public JSONObject post(String url, List<NameValuePair> params, int recursionLimit) throws HttpException, IOException, JSONException, HttpAuthenticationFailedException, RestClientRecursionException {
        HttpClient client = HttpUtils.getDefaultClient();
        String jsonString = "";
        JSONObject jsonObj;
        if (recursionLimit < 0) {
            throw new RestClientRecursionException("Recursion detected");
        }

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
        HttpContext httpContext = HttpSessionContainer.INSTANCE.getSessionContext();
        HttpResponse response = client.execute(httpPost, httpContext);
        HttpEntity entity = response.getEntity();

        int responseCode = response.getStatusLine().getStatusCode();

        if (responseCode != HttpStatus.SC_OK) {
            Log.i(TAG, "Non-200 HTTP response(" + Integer.toString(responseCode) + " for URL " + url);
            switch (responseCode) {
                case HttpStatus.SC_UNAUTHORIZED:    // 401, typically when not logged in
                    doRequiredAuth();
                    return post(url, params, --recursionLimit);
                case HttpStatus.SC_FORBIDDEN:
                case HttpStatus.SC_NOT_ACCEPTABLE:  // 406, Typically trying to log out when not logged in
                default:
                    throw new HttpException(Integer.toString(responseCode));
            }
        }

        jsonString = EntityUtils.toString(entity, "UTF-8");

        try {
            jsonObj = new JSONObject(jsonString);
        } catch (JSONException e) {  // Assume it might have been an array [true]
            JSONArray jsonArray = new JSONArray(jsonString);
            jsonObj = new JSONObject();
            jsonObj.put("arrayresult", jsonArray);
        }

        client.getConnectionManager().shutdown();


        return jsonObj;
    }

    public static void reportError(Context context, Object obj) {
        if (obj instanceof Exception) {
            int rId = 0;
            if (obj instanceof NoAccountException) {
                rId = R.string.no_account;
            } else if (obj instanceof HttpAuthenticationFailedException) {
                rId = R.string.authentication_failed;
            } else if (obj instanceof HttpException) {
                rId = R.string.http_server_access_failure;
            } else if (obj instanceof IOException) {
                rId = R.string.io_error;
            } else if (obj instanceof JSONException) {
                rId = R.string.json_error;
            } else {
                // Unexpected error
                rId = R.string.http_unexpected_failure;
            }
            Exception e = (Exception)obj;
            String exceptionDescription = e.toString();
            if (e.getMessage() != null) exceptionDescription += " Message:" + e.getMessage();
            if (e.getCause() != null) exceptionDescription += " Cause: " + e.getCause().toString();
            Tools.gaReportException(context, "RestClient Exception: ", exceptionDescription);

            Toast.makeText(context, rId, Toast.LENGTH_LONG).show();
        }
        return;
    }

    protected void doRequiredAuth() throws NoAccountException, IOException, JSONException {
        HttpAuthenticator authenticator = new HttpAuthenticator();
        authenticator.authenticate();
    }

    public class RestClientRecursionException extends Exception {
        private static final long serialVersionUID = 1L;

        public RestClientRecursionException(Exception e) {
            super(e);
        }

        public RestClientRecursionException(String msg) {
            super(msg);
        }
    }


}
