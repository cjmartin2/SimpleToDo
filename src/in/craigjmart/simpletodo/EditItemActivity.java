package in.craigjmart.simpletodo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends Activity {
	EditText etModifyItem;
	String itemString;
	int itemPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_item);
		
		itemString = getIntent().getStringExtra("itemString");
		itemPos = getIntent().getIntExtra("itemPos", 0);
		
		etModifyItem = (EditText) findViewById(R.id.etModifyItem);
		etModifyItem.setText(itemString);
		etModifyItem.setSelection(itemString.length());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_item, menu);
		return true;
	}
	
	public void saveItem(View v) {
		//get the new text from the text field, to send back
		String newText = etModifyItem.getText().toString();

		//prepare data
    	Intent i = new Intent();    	
    	i.putExtra("itemPos", itemPos);
    	i.putExtra("itemString", newText);
    	
    	setResult(RESULT_OK, i);
		
		//closes the activity and returns to first screen
		this.finish();
	}

}
