// This class represents a list fragment layout for where my tweets
// will populate once gathered from the API calls.
//
// @author Matt Mossman

package net.devmobility.killerandroid.tweettest;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TweetTestListFragment extends ListFragment {

	private static final String TAG = TweetTestListFragment.class.toString();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_fragment, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.d(TAG, "***GETTING HERE***");

		TweetsAdapter adapter = new TweetsAdapter(getActivity());
		setListAdapter(adapter);
	}
}