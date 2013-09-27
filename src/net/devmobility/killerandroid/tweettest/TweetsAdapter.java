// This adapter links tweet objects into a single row layout
// that represents one item to display in a ListView.
// The adapter is set by the fragment class and then loaded
// into the main activity layout.
//
// @author Matt Mossman

package net.devmobility.killerandroid.tweettest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetsAdapter extends ArrayAdapter<Tweet> {
	
	private static final String TAG = TweetsAdapter.class.toString();
	
	private Context context;
	private int currentTweet = -1;
	
	public TweetsAdapter(Context context){
		super(context, R.layout.twitter_row);
		this.context = context;
		Tweets.getTweets().setContext(context);
		Tweets.getTweets().addAdapter(this);
		Tweets.getTweets().refresh();
	}
	
	@Override
	public Tweet getItem(int position) {
		currentTweet = position;
		return super.getItem(position);
	}
	
	public Tweet getCurrentTweet() {
		if ( currentTweet != -1 ) {
			return getItem(currentTweet);
		}
		return null;
	}

	@Override
	public int getCount() {
		return Tweets.getTweets().size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
		if ( row == null ) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(R.layout.twitter_row, parent, false);
		}
		final Tweet tweet = Tweets.getTweets().get(position);

		String text = tweet.getText();
		if ( text != null ) { // Tweet text
			TextView textView = (TextView) row.findViewById(R.id.tweet_text);
			textView.setText(text);
		}

		String date = null;
		try {
			date = getDate(tweet.getCreatedAt());
		} catch (ParseException e) {
			Log.e(TAG, e.toString());
		} catch (java.text.ParseException e) {
			Log.e(TAG, e.toString());
		}

		if ( date != null ) { // Tweet created at date
			TextView textView = (TextView) row.findViewById(R.id.tweet_date);
			textView.setText(date);
		}
		
		Drawable pic = Tweets.getTweets().getDrawable(tweet.getProfileImageURL());
		if ( pic != null ) { // HTTP URL to the profile (avatar) image
			ImageView imageView = (ImageView) row.findViewById(R.id.tweet_image);
			imageView.setImageDrawable(pic);
		}
		return row;
	}
	
	
	// this method is not necessarily thread safe
	public static String getDate(String dateStr) throws ParseException, java.text.ParseException
	{
	    final String twitterDate = "EEE MMM dd HH:mm:ss Z yyyy";
	    SimpleDateFormat dateFormat = new SimpleDateFormat(twitterDate, Locale.ENGLISH);
	    dateFormat.setLenient(true);
	    Date date = dateFormat.parse(dateStr);
	    final String displayDate = "MMM d yyyy hh:mm a";
	    dateFormat = new SimpleDateFormat(displayDate, Locale.ENGLISH);
	    dateFormat.setTimeZone(TimeZone.getDefault());
	    return dateFormat.format(date);
	}
	
}
