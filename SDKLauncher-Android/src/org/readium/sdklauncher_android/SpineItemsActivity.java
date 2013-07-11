package org.readium.sdklauncher_android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.readium.sdklauncher_android.model.OpenPageRequest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.readium.model.epub3.Container;
import com.readium.model.epub3.Package;
import com.readium.model.epub3.SpineItem;

public class SpineItemsActivity extends Activity {

	protected static final String TAG = "SpineItemsActivity";
	private Context context;
    private Button back;
	private Package pckg;
	private int containerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spine_items);

        context = this;
        back = (Button) findViewById(R.id.backToBookView1);
        Intent intent = getIntent();
        if (intent.getFlags() == Intent.FLAG_ACTIVITY_NEW_TASK) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String value = extras.getString(Constants.BOOK_NAME);
                back.setText(value);
                containerId = extras.getInt(Constants.CONTAINER_ID);
                Container container = ContainerHolder.getInstance().get(containerId);
                pckg = container.getDefaultPackage();
            }
        }

        final ListView items = (ListView) findViewById(R.id.spineItems);

        List<SpineItem> spineItems = new ArrayList<SpineItem>();
        if (pckg != null) {
        	spineItems = pckg.getSpineItems();
        }

        this.setListViewContent(items, spineItems);

        initListener();
    }

    private void setListViewContent(ListView view, final List<SpineItem> spineItems) {
    	final List<String> list = new ArrayList<String>();
    	for (SpineItem si : spineItems) {
			list.add(si.getIdRef());
		}
        BookListAdapter bookListAdapter = new BookListAdapter(this, list);
        view.setAdapter(bookListAdapter);
        view.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                    long arg3) {
                Toast.makeText(context, "this is item " + list.get(position),
                        Toast.LENGTH_SHORT).show();
        		Intent intent = new Intent(SpineItemsActivity.this, WebViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		intent.putExtra(Constants.CONTAINER_ID, containerId);

        		OpenPageRequest openPageRequest = OpenPageRequest.fromIdref(spineItems.get(position).getIdRef());
        		try {
					intent.putExtra(Constants.OPEN_PAGE_REQUEST_DATA, openPageRequest.toJSON().toString());
            		startActivity(intent);
				} catch (JSONException e) {
					Log.e(TAG, ""+e.getMessage(), e);
				}
            }
        });
    }

    private void initListener() {
        back.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
