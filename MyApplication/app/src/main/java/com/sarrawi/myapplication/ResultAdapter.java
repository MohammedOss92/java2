package com.sarrawi.myapplication;

import java.io.IOException;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ResultAdapter extends BaseAdapter {
	MediaPlayer mp = new MediaPlayer();
	Boolean isPLAYING = false;
	private LayoutInflater inflater;
	private ArrayList<ResultRow> result;
	private SparseBooleanArray mSelectedItemsIds;
	private View toPro1;
	private View toSound1;
	private View fromPro1;
	private View fromSound1;
	private ArrayList<String> soundLess;
	
	
	public ResultAdapter(Context context, ArrayList<ResultRow> result) {
		this.inflater = LayoutInflater.from(context);
		this.result = result;
		mSelectedItemsIds = new SparseBooleanArray();
		soundLess = new ArrayList<String>();
		soundLess.add("Azerbaijani");		
		soundLess.add("Basque");
		soundLess.add("Belarusian");
		soundLess.add("Bengali");
		soundLess.add("Bulgarian");
		soundLess.add("Cebuano");
		soundLess.add("Esperanto");
		soundLess.add("Filipino");
		soundLess.add("Galician");
		soundLess.add("Georgian");
		soundLess.add("Gujarati");
		soundLess.add("Huasa");
		soundLess.add("Hebrew");
		soundLess.add("Hmong");
		soundLess.add("Igbo");
		soundLess.add("Irish");
		soundLess.add("Javanese");
		soundLess.add("Kannada");
		soundLess.add("Khmer");
		soundLess.add("Lao");
		soundLess.add("Lithuanian");
		soundLess.add("Maltese");
		soundLess.add("Maori");
		soundLess.add("Marathi");
		soundLess.add("Malay");
		soundLess.add("Persian");
		soundLess.add("Punjabi");
		soundLess.add("Slovenian");
		soundLess.add("Mongolian");
		soundLess.add("Nepali");
		soundLess.add("Somali");
		soundLess.add("Telugu");
		soundLess.add("Ukrainian");
		soundLess.add("Urdu");
		soundLess.add("Yiddish");
		soundLess.add("Yoruba");
		soundLess.add("Zulu");
		soundLess.add("Cebuano");
		soundLess.add("Hausa");
		soundLess.add("Hmong");
		soundLess.add("Igbo");
		soundLess.add("Javanese");
		soundLess.add("Khmer");
		soundLess.add("Lao");
		soundLess.add("Maori");
		soundLess.add("Marathi");
		soundLess.add("Mongolian");
		soundLess.add("Nepali");
		soundLess.add("Punjabi");
		soundLess.add("Somali");
		soundLess.add("Yoruba");
		soundLess.add("Zulu");
		

		mp.setScreenOnWhilePlaying(true);
		mp.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				isPLAYING = false;
				mp.reset();

			}
		});
		toPro1 = new View(context);
		toSound1 = new View(context);
		fromPro1 = new View(context);
		fromSound1 = new View(context);
	}

	@Override
	public int getCount() {
		return result.size();
	}

	public void selectView(int position, boolean value) {
		if (value)
			mSelectedItemsIds.put(position, value);
		else
			mSelectedItemsIds.delete(position);
		notifyDataSetChanged();
	}

	public int getSelectedCount() {
		return mSelectedItemsIds.size();
	}

	public SparseBooleanArray getSelectedIds() {
		return mSelectedItemsIds;
	}

	public void toggleSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));
	}

	public void remove(ResultRow object) {
		result.remove(object);
		notifyDataSetChanged();
	}

	public void removeSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int arg0) {
		return result.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
		
	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		View rowView = this.inflater.inflate(R.layout.result_row, null);
		TextView from = (TextView) rowView.findViewById(R.id.from);
		TextView fromText = (TextView) rowView.findViewById(R.id.fromText);
		TextView to = (TextView) rowView.findViewById(R.id.to);
		TextView toText = (TextView) rowView.findViewById(R.id.toText);

		from.setText(result.get(arg0).from);
		to.setText(result.get(arg0).to);
		fromText.setText(result.get(arg0).fromText);
		toText.setText(result.get(arg0).toText);
		
		
		ImageView fromShare = (ImageView) rowView.findViewById(R.id.fromShare);
		fromShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg2) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_SEND);				
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT,result.get(arg0).fromText);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				inflater.getContext().startActivity(Intent.createChooser(intent, "Share Text"));
			}
		});
		
		ImageView toShare = (ImageView) rowView.findViewById(R.id.toShare);
		toShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg2) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_SEND);				
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT,result.get(arg0).toText);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				inflater.getContext().startActivity(Intent.createChooser(intent, "Share Text"));
			}
		});
	
		final ImageView fromSound = (ImageView) rowView.findViewById(R.id.fromSound);
		ImageView fromMute = (ImageView) rowView.findViewById(R.id.fromMute);		
		final ProgressBar fromPro = (ProgressBar) rowView.findViewById(R.id.fromPro);
		
		if(soundLess.contains(result.get(arg0).from)){			
			fromMute.setVisibility(View.VISIBLE);
			fromSound.setVisibility(View.INVISIBLE);
		}else{
			fromSound.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					
					
					// TODO Auto-generated method stub
					fromSound.setVisibility(View.INVISIBLE);
					fromPro.setVisibility(View.VISIBLE);
					fromSound1 = fromSound;
					fromPro1 = fromPro;

					if (isPLAYING) {
						mp.stop();
						mp.reset();

					}
					try {
						isPLAYING = true;
						mp.setDataSource("http://translate.google.com/translate_tts?tl="
								+ result.get(arg0).fromCode
								+"&ie=UTF-8"
								+ "&q="
								+ result.get(arg0).fromText);
						mp.prepareAsync();					
										
						// mp.start();
					} catch (IOException e) {
						Log.i("error", "prepare() failed");
					}
				}
			});

		}
		
		
		
		final ImageView toSound = (ImageView) rowView.findViewById(R.id.toSound);
		ImageView toMute = (ImageView) rowView.findViewById(R.id.toMute);
		final ProgressBar toPro = (ProgressBar) rowView.findViewById(R.id.toPro);
		
		if(soundLess.contains(result.get(arg0).to)){
			toMute.setVisibility(View.VISIBLE);
			toSound.setVisibility(View.INVISIBLE);
		}else{			
			toSound.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// TODO Auto-generated method stub
					toSound.setVisibility(View.INVISIBLE);
					toPro.setVisibility(View.VISIBLE);
					toSound1 = toSound;
					toPro1 = toPro;
					if (isPLAYING) {
						mp.stop();
						mp.reset();

					}
					try {
						isPLAYING = true;
						mp.setDataSource("http://translate.google.com/translate_tts?tl="
								+ result.get(arg0).toCode
								+"&ie=UTF-8"
								+ "&q="
								+ result.get(arg0).toText);
						mp.prepareAsync();

					} catch (IOException e) {
						Log.i("error", "prepare() failed");
					}
				}
			});

		}
		
			
		
		mp.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp1) { // TODO
				mp.start();
				toPro1.setVisibility(View.INVISIBLE);
				toSound1.setVisibility(View.VISIBLE);
				fromPro1.setVisibility(View.INVISIBLE);
				fromSound1.setVisibility(View.VISIBLE);
			}
		});

		return rowView;
	}
}
