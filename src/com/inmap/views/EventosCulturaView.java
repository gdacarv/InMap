package com.inmap.views;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inmap.salvadorshop.R;
import com.inmap.server.InputStreamHandler;
import com.inmap.server.StringInputStreamHandler;
import com.inmap.server.Utils;

public class EventosCulturaView extends RelativeLayout {
	
	private ListView mListView;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public EventosCulturaView(Context context) {
		super(context);
		buildViews();
		com.inmap.utils.Utils.executeInParallel(new EventsHandlerAsyncTask());
	}
	
	private class Evento{
		String atividade, titulo, data, hora, id;
		
		@Override
		public String toString() {
			return "Evento id: " + id + " atividade: " + atividade + " titulo: " + titulo + " data: " + data + "hora: " + hora;
		}
	}

	private class EventsHandlerAsyncTask extends AsyncTask<Void, Evento, Evento[]>{

		@Override
		protected Evento[] doInBackground(Void... params) {
			List<Evento> list = new ArrayList<Evento>();
			try {
				InputStreamHandler handler = new EventosCulturaStringInputStreamHandler(list);
				for(int page = 1; true; page++) {
					list.clear();
					int sizeBefore = list.size();
					Utils.loadURL(getUrl(page), handler);
					if(sizeBefore == list.size())
						return list.toArray(new Evento[list.size()]);
					publishProgress(list.toArray(new Evento[list.size()]));
				}
			} catch (Exception e) {
				e.printStackTrace();
				return list.toArray(new Evento[list.size()]);
			}
		}
		
		protected void onProgressUpdate(Evento... values) {
			onNewEventsReceived(values);
		}
		
		@Override
		protected void onPostExecute(Evento[] values) {
			super.onPostExecute(values);
			onNewEventsReceived(values);
		}
	}

	public String getUrl(int page) {
		return "http://www.livrariacultura.com.br/scripts/eventos/busca/busca.asp?p=" + page + "&unidade=16&ord=data&list=lista";
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onNewEventsReceived(Evento[] values) {
		findViewById(R.id.layout_loading_cultura).setVisibility(View.INVISIBLE);
		@SuppressWarnings("unchecked")
		ArrayAdapter<Evento> adapter = (ArrayAdapter<Evento>)mListView.getAdapter();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
			adapter.addAll(values);
        else
            for(Evento item : values)
                adapter.add(item);
		adapter.notifyDataSetChanged();
		if(adapter.getCount() == 0)
			findViewById(R.id.layout_error_cultura).setVisibility(View.VISIBLE);
	}
	
	private void buildViews() {
		inflate(getContext(), R.layout.layout_cultura_eventos, this);
		mListView = (ListView) findViewById(R.id.list_cultura_eventos);
		mListView.setAdapter(new EventosAdapter(getContext()));
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Evento item = (Evento) parent.getItemAtPosition(position);
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("http://www.livrariacultura.com.br/scripts/eventos/resenha/resenha.asp?nevento="+item.id));
				getContext().startActivity(i);
			}
		});
		findViewById(R.id.btn_cultura_tryagain).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				findViewById(R.id.layout_loading_cultura).setVisibility(View.VISIBLE);
				findViewById(R.id.layout_error_cultura).setVisibility(View.INVISIBLE);
				com.inmap.utils.Utils.executeInParallel(new EventsHandlerAsyncTask());
			}
		});
	}

	private class EventosCulturaStringInputStreamHandler extends StringInputStreamHandler{

		private List<Evento> mList;

		public EventosCulturaStringInputStreamHandler(List<Evento> list) {
			mList = list;
		}

		@Override
		protected void handleString(String string) {
			int index = 0;
			while((index = string.indexOf("listaEventos_interno", index)) != -1) {
				Evento evento = new Evento();
				index = string.indexOf("nevento=", index) + 8;
				evento.id = string.substring(index, string.indexOf('"', index)).trim(); 
				index = string.indexOf("atividade\">", index) + 11;
				evento.atividade = string.substring(index, string.indexOf('<', index)).trim();
				index = string.indexOf("<h4>", index) + 4;
				index = string.indexOf('>', index) + 1;
				evento.titulo = string.substring(index, string.indexOf('<', index)).trim(); 
				index = string.indexOf("Data", index) + 4;
				index = string.indexOf('>', index) + 1;
				String datehour = string.substring(index, string.indexOf('<', index)).trim().replace("&agrave;", "à").replace("&atilde;", "ã");
				int asIndex = datehour.indexOf("às");
				if(asIndex != -1) {
					evento.data = datehour.substring(0, asIndex).trim();
					evento.hora = datehour.substring(asIndex + 2, datehour.length()).trim();
				} else {
					int lastSpaceIndex = datehour.lastIndexOf(' ');
					if(datehour.length() - lastSpaceIndex <= 3)
						lastSpaceIndex = datehour.substring(0, lastSpaceIndex).lastIndexOf(' ');
					evento.data = datehour.substring(0, lastSpaceIndex).trim();
					evento.hora = datehour.substring(lastSpaceIndex, datehour.length()).trim();
				}
				mList.add(evento);
			}
		}
	}
	
	private class EventosAdapter extends ArrayAdapter<Evento>{

		public EventosAdapter(Context context) {
			super(context, 0, new ArrayList<Evento>());
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = inflate(getContext(), R.layout.listitem_eventos, null);
			Evento evento = getItem(position);
			((TextView) convertView.findViewById(R.id.txt_event_title)).setText(evento.titulo);
			((TextView) convertView.findViewById(R.id.txt_event_type)).setText(evento.atividade);
			((TextView) convertView.findViewById(R.id.txt_event_date)).setText(evento.data);
			((TextView) convertView.findViewById(R.id.txt_event_hour)).setText(evento.hora);
			return convertView;
		}
	}
}
