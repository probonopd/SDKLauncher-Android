package org.readium.sdklauncher_android;

import java.util.Arrays;

import org.readium.sdklauncher_android.BookListAdapter.BookItemEnabler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.readium.model.epub3.Container;

public class BookDataActivity extends Activity {

	private Context context;
    private Button back;
    private TextView bookname;
	private Container container;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_data);

        context = this;
        back = (Button) findViewById(R.id.backToContainerView);
        bookname = (TextView) findViewById(R.id.bookname);
        Intent intent = getIntent();
        if (intent.getFlags() == Intent.FLAG_ACTIVITY_NEW_TASK) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String value = extras.getString(Constants.BOOK_NAME);
                bookname.setText(value);
                container = ContainerHolder.getInstance().get(extras.getInt(Constants.CONTAINER_ID));
            }
        }

        initMetadata();
        initPageList();
        initBookmark();

        initListener();
    }

    private void initBookmark() {
        int number = 0;
        final ListView bookmark = (ListView) findViewById(R.id.bookmark);
        String bookmarks = "Bookmarks(" + number + ")";
        String[] bookmark_values = new String[] { bookmarks };

        Class<?>[] classList = new Class<?>[] { BookmarksActivity.class };
        this.setListViewContent(bookmark, bookmark_values, classList, new BookItemEnabler() {
			
			@Override
			public boolean isEnabled(int position) {
				return false;
			}
		});
	}

	private void initPageList() {
        final ListView pageList = (ListView) findViewById(R.id.pageList);
        String[] pageList_values = new String[] {
                getString(R.string.list_of_figures),
                getString(R.string.list_of_illustrations),
                getString(R.string.list_of_tables),
                getString(R.string.page_list),
                getString(R.string.table_of_contents) };

        Class<?>[] classList = new Class<?>[] { 
        		ListOfFiguresActivity.class,
        		ListOfIllustrationsActivity.class,
        		ListOfTablesActivity.class,
        		PageListActivity.class,
        		TableOfContentsActivity.class };
        this.setListViewContent(pageList, pageList_values, classList, new BookItemEnabler() {
			
			@Override
			public boolean isEnabled(int position) {
				return position == 4;
			}
		});
	}

	private void initMetadata() {
        final ListView metadata = (ListView) findViewById(R.id.metaData);
        String[] metadata_values = new String[] { 
        		getString(R.string.metadata),
                getString(R.string.spine_items) };

        Class<?>[] classList = new Class<?>[] { 
        		MetaDataActivity.class,
        		SpineItemsActivity.class };
        this.setListViewContent(metadata, metadata_values, classList);
	}

	private void setListViewContent(ListView view, String[] stringArray,final Class<?>[] classes) {
    	setListViewContent(view, stringArray, classes, null);
    }

    private void setListViewContent(ListView view, String[] stringArray,final Class<?>[] classes, BookItemEnabler enabler) {
        BookListAdapter bookListAdapter = new BookListAdapter(this, Arrays.asList(stringArray), enabler);
        view.setAdapter(bookListAdapter);
        view.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                
                Toast.makeText(context, "this is item " + Integer.toString(arg2),
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(),
                		classes[arg2]);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.BOOK_NAME, bookname.getText());
                intent.putExtra(Constants.CONTAINER_ID, container.getNativePtr());
                
                startActivity(intent);
            }
        });
    }
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	if (container != null) {
    		ContainerHolder.getInstance().remove(container.getNativePtr());
    	}
    }

    private void initListener() {
        back.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
            	onBackPressed();
            }
        });
    }
}
