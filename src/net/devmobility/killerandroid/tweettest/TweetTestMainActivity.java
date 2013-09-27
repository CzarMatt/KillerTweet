// This is the main entry point class. UI layouts are loaded
// and then a fragment layout is loaded into the main layout
// after tweets are gathered.
//
// @author Matt Mossman

package net.devmobility.killerandroid.tweettest;

import net.devmobility.killerandroid.tweettest.util.SystemUiHider;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TweetTestMainActivity extends FragmentActivity {
	
	private static final String TAG = TweetTestMainActivity.class.toString();
	
	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final boolean TOGGLE_ON_CLICK = true;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	private SystemUiHider mSystemUiHider;
	private boolean refreshing = true;
	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_just_do_it_main);
		editText = (EditText) findViewById(R.id.edit_text);
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);
		
		mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
				// Cached values.
				int mControlsHeight;
				int mShortAnimTime;

				@Override
				@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
				public void onVisibilityChange(boolean visible) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
						if (mControlsHeight == 0) {
							mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
							}
							controlsView.animate().translationY(visible ? 0 : mControlsHeight).setDuration(mShortAnimTime);
						} else {
							controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
						}

						if (visible && AUTO_HIDE) {
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		
		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});
		
		controlsView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			}
		});

		Button button = (Button) findViewById(R.id.refresh_button);
		button.setOnTouchListener(mDelayHideTouchListener);
		button.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
				if (Tweets.getTweets().canRefresh()) {
					Tweets.getTweets().refresh(editText.getText().toString());
				}
			}
		});
		
		
		Toast.makeText(this, "Tweets refresh every minute.", Toast.LENGTH_LONG).show();
		
		//TODO This will never stop.
		refreshTweetsTask();
		
		
		// Call to get bearer token
		//new JustDoItRequestBearerToken().execute("https://api.twitter.com/oauth2/token");
		
		//tweets.refresh();
		
		//dialog = ProgressDialog.show(this, "Download", "downloading");
		
	}
	
	@Override
	protected void onPause() {
		refreshing = false;
		super.onPause();
	}

	@Override
	protected void onResume() {
		refreshing = true;
		refreshTweetsTask();
		super.onResume();
	}

	@Override
	protected void onStart() {
		refreshing = true;
		super.onStart();
	}

	@Override
	protected void onStop() {
		refreshing = false;
		super.onStop();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(300);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};
	
	Handler timerHandler = new Handler();
	Runnable timerRunnable = new Runnable() {
		@Override
		public void run() {
			if (!refreshing) {
				return;
			}
			if (Tweets.getTweets().canRefresh()) {
				Tweets.getTweets().refresh();
			}
			refreshTweetsTask();
		}
	};
	
	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	private void refreshTweetsTask () {
		timerHandler.removeCallbacks(timerRunnable);
		timerHandler.postDelayed(timerRunnable, 3 * 60 * 1000);  // 3 minutes
	}
	
}