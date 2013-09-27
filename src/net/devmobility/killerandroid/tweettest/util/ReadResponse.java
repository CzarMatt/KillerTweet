package net.devmobility.killerandroid.tweettest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;

import android.util.Log;

public abstract class ReadResponse {
	public static String readResponse(HttpsURLConnection connection) {
		try {
			StringBuilder stringBuilder = new StringBuilder();

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				stringBuilder.append(line + System.getProperty("line.separator"));
			}
			return stringBuilder.toString();
		} catch (IOException e) {
			Log.e("***ReadResponse", "Exception:", e);
			return new String();
		}
	}
}
