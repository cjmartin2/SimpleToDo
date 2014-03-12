package in.craigjmart.simpletodo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class TodoActivity extends Activity {
	ArrayList<String> items;
	ArrayAdapter<String> itemsAdapter;
	ListView lvItems;
    ParseObject toDoItems;
	
	private final int REQUEST_CODE = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        Parse.initialize(this, "M6gXamHEH5Evs59YwKswZvqEyb38ljbVUDiI5OUY", "Uteake7xbUDDBV23I9fnLrN70AKDxhIBJ6yOut9O");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("toDoItems");
        //cheating here, but know I only have 1 row in this table, so just get it
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject parseItems, ParseException e) {
                if (e == null) {
                    //set the toDoItems if they exist
                    toDoItems = parseItems;
                } else {
                    toDoItems = new ParseObject("toDoItems");
                }
                readItems();
                lvItems = (ListView) findViewById(R.id.lvItems);
                itemsAdapter = new ArrayAdapter<String>(TodoActivity.this, android.R.layout.simple_list_item_1, items);
                lvItems.setAdapter(itemsAdapter);

                setupListViewListener();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo, menu);
        return true;
    }
    
    public void addTodoItem(View v) {
    	EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
    	itemsAdapter.add(etNewItem.getText().toString());
    	
    	//adding a Toast because if your list fills the screen, and you add another item, you don't see it show up . . . think it is a nice to have.
    	Toast.makeText(this, getResources().getString(R.string.added_toast, etNewItem.getText().toString()), Toast.LENGTH_SHORT).show();
    	etNewItem.setText("");
    	saveItems();
	}
    
    private void setupListViewListener() {
    	lvItems.setOnItemLongClickListener(new OnItemLongClickListener(){
    		@Override
    		public boolean onItemLongClick(AdapterView<?> aView, View item, int pos, long id){
    			String lastRemovedItem = items.get(pos);
    			items.remove(pos);
    			itemsAdapter.notifyDataSetInvalidated();
    			
    			//is this the best way to handle the TodoActivity context?
    			Toast.makeText(TodoActivity.this, getResources().getString(R.string.removed_toast, lastRemovedItem), Toast.LENGTH_SHORT).show();
    			saveItems();
    			return true;
    		}
    	});
    	
    	lvItems.setOnItemClickListener(new OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> aView, View item, int pos, long id){
    			openEditView(pos);
    		}
		});
	}
    
    private void readItems() {
    	File filesDir = getFilesDir();
    	File todoFile = new File(filesDir, "todo.txt");
    	
    	try{
    		items = new ArrayList<String>(FileUtils.readLines(todoFile));
    	} catch(IOException e){
    		items = new ArrayList<String>();
    		e.printStackTrace();
    	}

        ArrayList<String> parseItems = (ArrayList)toDoItems.getList("items");
        if(parseItems == null){
            parseItems = new ArrayList<String>();
        }

        //add any parse items to the local cache
        ArrayList<String> parseCopy = new ArrayList<String>(parseItems);
        //ghetto way to remove dupes, realized Parse already supports local caching if I set the 
        //cache policy, so gave up on doing this a 'pretty' way.
        parseCopy.removeAll(items);
        items.addAll(parseCopy);
	}
    
    private void saveItems() {
    	File filesDir = getFilesDir();
    	File todoFile = new File(filesDir, "todo.txt");
    	
    	try{
    		FileUtils.writeLines(todoFile, items);
    	} catch(IOException e){
    		e.printStackTrace();
    	}

        //store new items list in Parse
        toDoItems.put("items", items);
        toDoItems.saveInBackground();
	}
    
    private void openEditView(int pos) {
    	//create the intent
    	Intent i = new Intent(TodoActivity.this, EditItemActivity.class);
    	
    	i.putExtra("itemPos", pos);
    	i.putExtra("itemString", items.get(pos));
    	
    	startActivityForResult(i, REQUEST_CODE);
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
    	if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
    		//get our data
    		int itemPos = i.getIntExtra("itemPos", 0);
    		String itemName = i.getStringExtra("itemString");
    		
    		items.remove(itemPos);
    		items.add(itemPos, itemName);
			itemsAdapter.notifyDataSetInvalidated();
    		
    		// Toast the name to display temporarily on screen
    		Toast.makeText(this, getResources().getString(R.string.updated_toast, itemName), Toast.LENGTH_SHORT).show();
    		saveItems();
    	}
    } 
    
}
