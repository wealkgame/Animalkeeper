package wealk.android.animalkeeper;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class Top extends ListActivity{
	public static int mScreenWidth,mScreenHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        mScreenWidth = display.getWidth();
        mScreenHeight = display.getHeight();
		
		
		this.setTitle("排行榜");
		final SQLiteHelper helpter = new SQLiteHelper(this);
		Cursor cursor = helpter.query();
		
		String[] from = {"_rank", "_name", "_score" };		
		int[] to = {R.id.mid ,  R.id.mname, R.id.mscore};			

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.show, cursor, from, to);
		ListView listView = getListView();
		listView.setAdapter(adapter);
		
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final long temp = arg3;
				builder.setMessage("删除").setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								helpter.delete((int)temp);
								Cursor c = helpter.query();
								String[] from = { "_rank", "_name", "_score" };
								int[] to = {R.id.mid, R.id.mname, R.id.mscore };

								SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(),
										R.layout.show, c, from, to);
								ListView listView = getListView();
								listView.setAdapter(adapter);
							}
						}).setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								
							}
						});
				AlertDialog ad = builder.create();
				ad.show();
			}
		});
		helpter.close();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){//返回上一界面，并且将所有数据初始化
			startActivity(new Intent(Top.this, GameStartMenu.class));
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
