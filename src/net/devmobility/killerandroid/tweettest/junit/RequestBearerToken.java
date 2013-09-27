// This class queries Twitter for a bearer token based on my
// Twitter credentials. The response object is parsed out and
// my bearer token is saved for later API requests.
//
// @author Matt Mossman

package net.devmobility.killerandroid.tweettest.junit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import net.devmobility.killerandroid.tweettest.util.ReadResponse;
import net.devmobility.killerandroid.tweettest.util.WriteRequest;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class RequestBearerToken extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... string) {
		HttpsURLConnection connection = null;
		// Keys generated by Twitter.com from my profile
		String encodedCredentials = getEncodedKey("YOUR_CONSUMER_KEY_HERE", "YOUR_CONSUMER_SECRET_HERE");
		Log.d("****Encoded Credentials:", encodedCredentials);
		
		try {
			URL url = new URL(string[0]);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Host", "api.twitter.com");
			connection.setRequestProperty("User-Agent", "YOUR_APPNAME_HERE");
			connection.setRequestProperty("Authorization", "Basic "	+ encodedCredentials);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			connection.setRequestProperty("Content-Length", "29");
			connection.setUseCaches(false);

			WriteRequest.writeRequest(connection, "grant_type=client_credentials"); 

			JSONObject obj = new JSONObject(ReadResponse.readResponse(connection));
			
			String tokenType = (String) obj.get("token_type");
			String token = (String) obj.get("access_token");

			Log.d("***Token Type: ", tokenType);
			Log.d("***Token: ", token);
			return ((tokenType.equals("bearer")) && (token != null)) ? token : "";
			
		} catch (MalformedURLException e) {
			try {
				throw new IOException("Invalid endpoint URL specified.", e);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			Log.e("***GetTweets", "IOException: ", e);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return encodedCredentials;
	}

	protected static String getEncodedKey(String consumerKey, String consumerSecret) {
		try {
			String encodedConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
			String encodedConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");

			String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;
			byte[] encodedBytes = Base64.encode(fullKey.getBytes(), Base64.NO_WRAP);
			return new String(encodedBytes);
		} catch (UnsupportedEncodingException e) {
			return new String();
		}
	}
	
}
