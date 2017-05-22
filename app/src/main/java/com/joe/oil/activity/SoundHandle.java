package com.joe.oil.activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.joe.oil.R;



public class SoundHandle  extends AsyncTask<String, String, String> {
	public void setContext(Context context)
	{
		this.context = context;
	}

	private Context context;

	@Override
	protected String doInBackground(String... params)
	{
		//OrderedItemDao dao = new OrderedItemDao(context);
		//List<OrderedEntity> data = dao.getAllOrderedItem();

     	MediaPlayer mPlayer = new MediaPlayer();
       	mPlayer = MediaPlayer.create(context, R.raw.notify);
       	mPlayer.start();
	

			


		return this.toString();
	}


}
