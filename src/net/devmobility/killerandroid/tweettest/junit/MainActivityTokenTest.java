package net.devmobility.killerandroid.tweettest.junit;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.*;

import android.util.Base64;

public class MainActivityTokenTest {

	@Test
	public void testGetEncodedKey() {
		String consumerKey = "YOUR_CONSUMER_KEY_HERE";
		String consumerSecret = "YOU_CONSUMER_SECRET_HERE";

		try {
			String encodedConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
			String encodedConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");

			String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;
			byte[] encodedBytes = Base64.encode(fullKey.getBytes(), Base64.NO_WRAP);
			assertTrue(
					"Encoded Token:",
					encodedBytes.equals("ENCODED_BYTES_HERE"));
		} catch (UnsupportedEncodingException e) {
			fail("Not yet implemented");
		}

	}

}
