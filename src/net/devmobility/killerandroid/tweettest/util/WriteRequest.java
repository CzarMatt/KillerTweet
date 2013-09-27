package net.devmobility.killerandroid.tweettest.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.net.ssl.HttpsURLConnection;

public abstract class WriteRequest {
	public static boolean writeRequest(HttpsURLConnection connection, String textBody) {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			bufferedWriter.write(textBody);
			bufferedWriter.flush();
			bufferedWriter.close();

			return true;
		} catch (IOException e) {
			return false;
		}
	}
}