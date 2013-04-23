package com.inmap.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.inmap.salvadorshop.R;
import com.inmap.server.StringInputStreamHandler;
import com.inmap.utils.Utils;

public class CinemarkExtraFragment extends ExtraFragment {

	private View mRoot;

	@Override
	public String getTitle() {
		return getString(R.string.filmes_e_hor_rios);
	}

	@Override
	public int getIconResId() {
		return getIconResIdStatic();
	}

	public static int getIconResIdStatic() {
		return R.drawable.ico_eventos_lojas;
	}

	public static String getDescription(Resources res) {
		return res.getString(R.string.msg_description_cinemark);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.fragment_cinemark, container, false);
		mRoot.findViewById(R.id.btn_cinemark_tryagain).setOnClickListener(tryAgainOnClickListener);
		Utils.executeInParallel(new FetchMoviesAsyncTask());
		setReady(true);
		return mRoot;
	}

	private OnClickListener tryAgainOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mRoot.findViewById(R.id.layout_loading_cinemark).setVisibility(View.VISIBLE);
			mRoot.findViewById(R.id.layout_error_cinemark).setVisibility(View.INVISIBLE);
			Utils.executeInParallel(new FetchMoviesAsyncTask());
		}
	};

	private class Movie{
		String name, rating, time, link, audio;
	}

	private class FetchMoviesAsyncTask extends AsyncTask<Void, Void, List<Movie>>{

		private String date, legendas;

		@Override
		protected List<Movie> doInBackground(Void... params) {
			final List<Movie> movies = new ArrayList<CinemarkExtraFragment.Movie>();
			try {
				com.inmap.server.Utils.loadURL("http://www.cinemark.com.br/programacao/salvador/salvador/26/785", new StringInputStreamHandler() {

					@Override
					protected void handleString(String string) {
						int index = string.indexOf("class=\"datas-filmes\"");
						if(index < 0)
							return;
						index = string.indexOf("href", index + 20);
						index = getNextTextWithoutMark(string, index);
						date = string.substring(index, string.indexOf('<', index));
						int temp;
						while(index > 0 && (temp = string.indexOf("class=\"movie-info\"", index)) > 0) {
							Movie movie = new Movie();
							index = string.indexOf("exibicao/", temp+17) + 9;
							index = string.indexOf("title=\"", index) + 7;
							movie.audio = string.substring(index, string.indexOf('"', index));
							index = string.indexOf("censura/", index) + 8;
							index = string.indexOf("title=\"", index) + 7;
							movie.rating = string.substring(index, string.indexOf('"', index));
							index = string.indexOf("href=\"", index) + 6;
							movie.link = "http://www.cinemark.com.br" + string.substring(index, string.indexOf('"', index));
							index = getNextTextWithoutMark(string, index);
							movie.name = string.substring(index, string.indexOf('<', index)).replace("&nbsp;", "");
							int endTime = string.indexOf("</div>", index);
							movie.time = "";
							while((index = getNextTextWithoutMark(string, index)) > 0 && index < endTime) {
								movie.time += string.substring(index, string.indexOf('<', index));
							}
							movies.add(movie);
						}
						legendas = "";
						index = string.indexOf("legendas", index) + 8;
						while((index = string.indexOf("letra-legenda", index)) > 0) {
							index = getNextTextWithoutMark(string, index);
							legendas += string.substring(index, string.indexOf('<', index)) + " - ";
							index = getNextTextWithoutMark(string, index);
							legendas += string.substring(index, string.indexOf('<', index)).trim() + '\n';
						}
					}

					private int getNextTextWithoutMark(String string, int index) {
						while((index = string.indexOf('>', index)) > 0 && string.charAt(index+1) == '<') index++;
						return index < 0 ? index : index+1;
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			return movies;
		}

		@Override
		protected void onPostExecute(List<Movie> result) {
			super.onPostExecute(result);
			mRoot.findViewById(R.id.layout_loading_cinemark).setVisibility(View.INVISIBLE);
			if(result.size() == 0)
				mRoot.findViewById(R.id.layout_error_cinemark).setVisibility(View.VISIBLE);
			else {
				ListView list = (ListView) mRoot.findViewById(R.id.list_cinemark);
				list.setAdapter(new MoviesAdapter(date, result, legendas));
				list.setOnItemClickListener(onItemClickListener);
			}
		}
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(((Movie)parent.getItemAtPosition(position)).link));
			startActivity(i);
		}
	};
	
	private class MoviesAdapter extends ArrayAdapter<Movie>{

		private String date;
		private String legendas;

		public MoviesAdapter(String date, List<Movie> result, String legendas) {
			super(getActivity(), 0, result);
			this.date = date;
			this.legendas = legendas;
		}
		
		@Override
		public int getCount() {
			return super.getCount()+1;
		}
		
		@Override
		public int getViewTypeCount() {
			return 2;
		}
		
		@Override
		public int getItemViewType(int position) {
			return isInfo(position) ? 1 : 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = View.inflate(getContext(), isInfo(position) ? R.layout.listitem_movie_info : R.layout.listitem_movie, null);
			if(isInfo(position)) {
				((TextView)convertView.findViewById(R.id.txt_movie_period)).setText(String.format(getString(R.string.msg_movie_valid), date));
				((TextView)convertView.findViewById(R.id.txt_movie_legendas)).setText(legendas);
			}else {
				Movie movie = getItem(position);
				((TextView)convertView.findViewById(R.id.txt_movie_title)).setText(movie.name);
				((TextView)convertView.findViewById(R.id.txt_movie_rating)).setText(movie.audio + " - " + movie.rating);
				((TextView)convertView.findViewById(R.id.txt_movie_times)).setText(movie.time);
			}
			return convertView;
		}

		private boolean isInfo(int position) {
			return position == getCount()-1;
		}
		
		@Override
		public boolean isEnabled(int position) {
			return !isInfo(position);
		}
	}
}
