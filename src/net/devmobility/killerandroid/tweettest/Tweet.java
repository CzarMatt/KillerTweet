// This class represents a single tweet and all the
// information we can (want) to pull it.
//
// @author Matt Mossman

package net.devmobility.killerandroid.tweettest;

public class Tweet {

	private static final String TAG = Tweet.class.toString();
	
	private String profileImageURL;
	private String text;
	private String createdAt;
	
	public Tweet(String profileImageURL, String text, String createdAt) {
		this.profileImageURL = profileImageURL;
		this.text = text;
		this.createdAt = createdAt;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getText() {
		return text;
	}
	
	public String getProfileImageURL() {
		return profileImageURL;
	}

}
