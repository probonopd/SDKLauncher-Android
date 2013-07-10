package org.readium.sdklauncher_android;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BookListAdapter extends BaseAdapter {
	
	private final List<String> mData;
	private final Context context;
	private final BookItemEnabler enabler;
	
	public interface BookItemEnabler {
		boolean isEnabled(int position);
	}
	
	public BookListAdapter(Context context, List<String> list) {
		this(context, list, null);
	}
	
	public BookListAdapter(Context context, List<String> list, BookItemEnabler enabler) {
		this.mData = list;
		this.context = context;
		this.enabler = (enabler != null) ? enabler : new BookItemEnabler() {
			
			@Override
			public boolean isEnabled(int position) {
				return true;
			}
		};
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.book_item, null);
//			holder.img = (ImageView) convertView.findViewById(R.id.afd_img);
//			holder.title = (TextView) convertView.findViewById(R.id.afd_title);
			holder.info = (TextView) convertView.findViewById(R.id.bookname_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
//		holder.img.setBackgroundResource((Integer) mData.get(position).get(
//				"img"));
//		holder.title.setText((String) mData.get(position).get("title"));
		holder.info.setText(mData.get(position));
		holder.info.setEnabled(enabler.isEnabled(position));
		return convertView;
	}
	
	public final class ViewHolder {
//		public ImageView img;
//		public TextView title;
		public TextView info;
	}
}
