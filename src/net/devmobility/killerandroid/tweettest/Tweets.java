// This class extends ArrayList which is a container for my
// tweet obejcts parsed out of the return response API from
// Twitter. Pics are stored via Hashmap. API requests are 
// made asynchronously and parsed into a JSONArray.
//
// @author Matt Mossman

package net.devmobility.killerandroid.tweettest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.net.ssl.HttpsURLConnection;

import net.devmobility.killerandroid.tweettest.util.ReadResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

public class Tweets extends ArrayList<Tweet> {

	private static final long serialVersionUID = -2573639618440656059L;

	private static final String TAG = Tweets.class.toString();
	
	private Map<String, Drawable> profilePics;
	private Drawable defaultDrawable;
	private List<TweetsAdapter> adapters;

	private static Tweets tweets;
	private Semaphore lock = new Semaphore(1);
	
	private Context context;
	
	private Tweets() {
		adapters = new ArrayList<TweetsAdapter>();
		//TODO This should probably be a LRU Cache for better memory management.
		profilePics = new HashMap<String, Drawable>();
		//refresh();
	}
	
	public boolean canRefresh() {
		return lock.tryAcquire();
	}
	
	public void refresh() {
		refresh("nyancat");
	}
	
	public void refresh(String search) {
		clear();
		new GetTweetsTask().execute(search);
	}
	
	public static Tweets getTweets() {
		if (tweets == null) {
			tweets = new Tweets();
		}
		return tweets;
	}

	public void setContext(Context context) {
		this.context = context;
		defaultDrawable = context.getResources().getDrawable(R.drawable.ic_launcher);
	}
	
	public void addAdapter(TweetsAdapter adapter) {
		adapters.add(adapter);
	}

	private void buildTweets(JSONObject tweetsObject) {
		try {
			JSONArray tweetArray = tweetsObject.getJSONArray("statuses");

			for (int i = 0; i < tweetArray.length(); i++) {
				JSONObject statusObject = tweetArray.getJSONObject(i);
				String text = statusObject.getString("text");
				JSONObject userObject = statusObject.getJSONObject("user");
				String profileImageURL = userObject.getString("profile_image_url");
				String createdAt = statusObject.getString("created_at");
				Tweet tweet = new Tweet(profileImageURL, text, createdAt);
				add(tweet);
				download(tweet);
			}
		} catch (JSONException e) {
			//TODO Toast something here about malformed JSON
			Log.e(TAG, "Unable to build tweets", e);
		}
	}
	
	public Drawable getDrawable(String imageURL) {
		Drawable drawable = profilePics.get(imageURL);
		if (drawable != null) {
			return drawable;
		}
		return defaultDrawable;
	}
	
	private void download(Tweet tweet) {
		DownloadImageTask task = new DownloadImageTask(tweet);
		task.execute(context);
	}

	private class GetTweetsTask extends AsyncTask<String, Void, Void> {
		private static final String BEARER_TOKEN = "YOUR_BEARER_TOKEN_HERE";
		private static final String SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json?&count=20&lang=en&q=%23";
		
		
		private ProgressDialog dialog = new ProgressDialog(context);
		
	    protected void onPreExecute() {
	        this.dialog.setMessage("Deploying pigeons. . .");
	        this.dialog.show();
	    }
		
		@Override
		public Void doInBackground(String... params) {
			HttpsURLConnection connection = null;
	
			try {
				URL url = new URL(SEARCH_URL + params[0]);
				Log.d("****URL:", url.toString());
				connection = (HttpsURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Host", "api.twitter.com");
				connection.setRequestProperty("User-Agent", "JustDoIt");
				connection.setRequestProperty("Authorization", "Bearer " + BEARER_TOKEN);
				connection.setUseCaches(false);
	
				JSONObject obj = new JSONObject(ReadResponse.readResponse(connection));
	
				buildTweets(obj);
				
				Log.d("***GetTweets:", obj.toString());
				
			} catch (MalformedURLException e) {
				Log.e("***GetTweets", "Invalid endpoint URL specified.", e);
				
			} catch (IOException e) {
				Log.e("***GetTweets", "IOException: ", e);
			} catch (JSONException e) {
				e.printStackTrace();
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
				lock.release();
			}
			return null;
		}
		protected void onPostExecute(Void result) {
	        if (this.dialog.isShowing()) {
	            this.dialog.dismiss();
	        }
		}
	}
	
	
	private void notifyAdapters() {
		((Activity) context).runOnUiThread(new Runnable() {
		    public void run() {
		    	for ( ArrayAdapter<Tweet> adapter : adapters ) {
					adapter.notifyDataSetChanged();
				}
		    }
		});
		
	}
	
	class DownloadImageTask extends AsyncTask<Context, Integer, Void> {

		private Tweet tweet;
	    
		DownloadImageTask(Tweet tweet) {
			this.tweet = tweet;
		}

		@Override
		protected Void doInBackground(Context... params) {
			String profileImageURL = tweet.getProfileImageURL();
			Drawable pic = profilePics.get(profileImageURL);
			if (pic == null) {
				try {
					InputStream is = (InputStream) new URL(profileImageURL).getContent();
					pic = Drawable.createFromStream(is, "src");
					profilePics.put(profileImageURL, pic);
				} catch (MalformedURLException e) {
					Log.e(TAG, "Unable to get drawable at " + profileImageURL + " - malformed url", e);
				} catch (IOException e) {
					Log.e(TAG, "Unable to get drawable at " + profileImageURL + " - read failed", e);
				}
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			notifyAdapters();
			super.onPostExecute(result);
		}
	}
	
}
