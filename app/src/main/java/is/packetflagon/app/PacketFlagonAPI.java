package is.packetflagon.app;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


public class PacketFlagonAPI
{
    protected static String APIURL = "https://packetflagon.is/api/1/index.php?action=";
    //protected static String APIKey = "c74f7e1d7d7c0be4df9488a6d8240db3";
    String APIKey = "";


    public PacketFlagonAPI(String key)
    {
        APIKey = key;
    }

    public APIReturn CreatePAC(String friendlyName, String description, String password, String URLs, boolean localProxy) throws IOException
    {
        List<NameValuePair> payload = new ArrayList<NameValuePair>();
        payload.add(new BasicNameValuePair("friendlyname", friendlyName));
        payload.add(new BasicNameValuePair("description", description));
        if(password != "")
            password = md5(password);

        payload.add(new BasicNameValuePair("password", password));
        payload.add(new BasicNameValuePair("urls", URLs));
        if(localProxy) {
            payload.add(new BasicNameValuePair("localproxy", "1"));
        }
        else {
            payload.add(new BasicNameValuePair("localproxy", "0"));
        }
        payload.add(new BasicNameValuePair("auth", md5(APIKey + URLs)));


        return makeRequest(payload,"create_pac");
    }

    public APIReturn GetPacDetails(String hash)
    {
        //PAC pac = new PAC();

        List<NameValuePair> payload = new ArrayList<NameValuePair>();
        payload.add(new BasicNameValuePair("hash", hash));
        payload.add(new BasicNameValuePair("auth", md5(APIKey + hash)));

        return makeRequest(payload,"get_pac");
    }

    public APIReturn PushToS3(String hash)
    {
        //PAC pac = new PAC();

        List<NameValuePair> payload = new ArrayList<NameValuePair>();
        payload.add(new BasicNameValuePair("hash", hash));
        payload.add(new BasicNameValuePair("auth", md5(APIKey + hash)));

        return makeRequest(payload,"push_to_s3");
    }


    public APIReturn RemoveURL(String hash, String url, String password)
    {
        Log.e("Remove","hash: " + hash + " password: " + password + " url: " + url);

        List<NameValuePair> payload = new ArrayList<NameValuePair>();
        payload.add(new BasicNameValuePair("hash", hash));
        payload.add(new BasicNameValuePair("password", md5(password)));
        payload.add(new BasicNameValuePair("url", url));
        payload.add(new BasicNameValuePair("auth", md5(APIKey + url)));

        return makeRequest(payload,"remove_url_from_pac");
    }

    public APIReturn AddURL(String hash, String url, String password)
    {
        List<NameValuePair> payload = new ArrayList<NameValuePair>();
        payload.add(new BasicNameValuePair("hash", hash));
        payload.add(new BasicNameValuePair("password", md5(password)));
        payload.add(new BasicNameValuePair("url", url));
        payload.add(new BasicNameValuePair("auth", md5(APIKey + url)));

        return makeRequest(payload,"add_url_to_pac");
    }

    private APIReturn makeRequest(List<NameValuePair> payload, String purpose)
    {
        APIReturn apiReturn = new APIReturn();
        URL url = null;
        HttpURLConnection urlConnection = null;
        String payloadQuery = "";

        //Ensure we add our API key to the payload regardless of purpose
        payload.add(new BasicNameValuePair("api", APIKey));

        try
        {
            url = new URL(APIURL+purpose);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            apiReturn.success = false;
            apiReturn.message = e.getMessage();
            return apiReturn;
        }

        try
        {
            urlConnection = (HttpsURLConnection) url.openConnection();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            apiReturn.success = false;
            apiReturn.message = e.getMessage();
            return apiReturn;
        }

        try
        {
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            payloadQuery = getQuery(payload);
            urlConnection.setFixedLengthStreamingMode(payloadQuery.getBytes().length);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStreamWriter post = new OutputStreamWriter(urlConnection.getOutputStream());
            post.write(payloadQuery);
            post.flush();

            /*Log.e("Post", "Processing response");
            Log.e("Post",urlConnection.getResponseMessage());
            Log.e("Post",String.valueOf(urlConnection.getResponseCode()));*/
            String response = "";
            Scanner inStream = new Scanner(urlConnection.getInputStream());
            while (inStream.hasNextLine())
            {
                response+=(inStream.nextLine());
            }

            Log.e("Response",response);

            JSONObject jsonResponse = new JSONObject(response);

            //All Responses
            apiReturn.success = jsonResponse.getBoolean("success");

            if(jsonResponse.has("hash"))
                apiReturn.hash = jsonResponse.getString("hash");

            if(jsonResponse.has("message"))
                apiReturn.message = jsonResponse.getString("message");

            //PAC specific
            if(jsonResponse.has("friendlyname"))
                apiReturn.friendlyName = jsonResponse.getString("friendlyname");

            if(jsonResponse.has("description"))
                apiReturn.description = jsonResponse.getString("description");

            if(jsonResponse.has("url"))
                apiReturn.s3URL = jsonResponse.getString("url");

            if(jsonResponse.has("urls"))
            {
                List<BlockedURL> urlList = new ArrayList<BlockedURL>();
                List<Boolean> blockedList = new ArrayList<Boolean>();

                JSONArray urls = jsonResponse.getJSONArray("urls");

                for (int i = 0; i < urls.length(); i++)
                {
                    urlList.add(new BlockedURL(((JSONObject) urls.get(i)).getString("url"), ((JSONObject) urls.get(i)).getBoolean("blocked")));
                }
                apiReturn.urls = urlList;

            }

            if(jsonResponse.has("localproxy")) {
                try {
                    apiReturn.localProxy = jsonResponse.getBoolean("localproxy");
                }
                catch (JSONException j)
                {
                    try
                    {
                        if(jsonResponse.getInt("localproxy") == 0)
                        {
                            apiReturn.localProxy = false;
                        }
                        else
                        {
                            apiReturn.localProxy = true;
                        }
                    }
                    catch (JSONException j2)
                    {
                        apiReturn.localProxy = false;
                    }
                }
            }

            if(jsonResponse.has("ro")) {
                try {
                    apiReturn.passwordProtected = jsonResponse.getBoolean("ro");
                }
                catch (JSONException j)
                {
                    try
                    {
                        if(jsonResponse.getInt("ro") == 0)
                        {
                            apiReturn.passwordProtected = false;
                        }
                        else
                        {
                            apiReturn.passwordProtected = true;
                        }
                    }
                    catch (JSONException j2)
                    {
                        apiReturn.passwordProtected = false;
                    }
                }
            }

            //URLs

        }
        catch (IOException e)
        {
            e.printStackTrace();
            apiReturn.success = false;
            apiReturn.message = "There was a general IO error processng the response from the API";
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            apiReturn.success = false;
            apiReturn.message = "There was an error parsing the JSON response from the API";
        }
        finally
        {
                urlConnection.disconnect();
        }

        return apiReturn;
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }
        Log.e("getQuery",result.toString());
        return result.toString();
    }
}
