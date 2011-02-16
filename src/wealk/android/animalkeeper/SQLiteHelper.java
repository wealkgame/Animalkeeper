package wealk.android.animalkeeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper{

	
	//需要定义SQLite的各个定义
	private static final String DB_NAME = "score.db";
	private static final String TABLE_NAME = "ScoreTable";
	private static final String CREATE_TABLE = " create table "
			+ " if not exists " + TABLE_NAME + " (_id integer primary key autoincrement,_name text,_score Integer,_rank Integer) ";
	
	private SQLiteDatabase mDB;
	
	SQLiteHelper(Context context) {
		super(context, DB_NAME, null, 2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		this.mDB = db;
		db.execSQL(CREATE_TABLE);
	}
	
	
	/**
	 * 插入
	 * @param values
	 */
	public void insert(ContentValues values){
		mDB = getWritableDatabase();
		mDB.beginTransaction();//开始事务
		try{			
			mDB.insert(TABLE_NAME, null, values);	
			mDB.setTransactionSuccessful(); //执行到endTransaction()提交当前事务,如不调用此方法会回滚事务 .	
		}catch (Exception e) {
			//Log.e(TAG, e.getMessage());
		}finally{
			mDB.endTransaction();//由事务的标志决定是提交事务，还是回滚事务.
			mDB.close();
		}
	}
	
	/**
	 * 更新
	 * @param values
	 * @param id
	 */
	public void update(ContentValues values,int id){
		mDB = getWritableDatabase();
		mDB.beginTransaction();//开始事务
		try{			
			mDB.update(TABLE_NAME, values, "_id = '"+id+"'", null);
			mDB.setTransactionSuccessful(); //执行到endTransaction()提交当前事务,如不调用此方法会回滚事务 .	
		}catch (Exception e) {
			//Log.e(TAG, e.getMessage());
		}finally{
			mDB.endTransaction();//由事务的标志决定是提交事务，还是回滚事务.
			close();
		}
	}
	
	/**
	 * 删除
	 * @param id
	 */
	public void delete(int id){
		try {
			if (mDB == null)
				mDB = getWritableDatabase();
			mDB.delete(TABLE_NAME, "_id=?", new String[] { String.valueOf(id) });
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			close();
		}
		
	}
	
	/**
	 * 查询
	 */
	public Cursor query(){
		Cursor c = null;
		try {
			SQLiteDatabase db = getWritableDatabase();
			c = db.query(TABLE_NAME, null, null, null, null, null, "_score desc",null);
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			close();
		}
		
		return c;
	}
	
	/**
	 * 查看分数排名
	 * @param score
	 * @return
	 */
	public String queryrank(String score){
		String rank = null;
		try {
			SQLiteDatabase db = getWritableDatabase();
			Cursor c = db.query(TABLE_NAME, null, " _score >= '"+score+"'" , null, null, null, null, null);
			rank = String.valueOf(c.getCount());
			
			Cursor cc = db.query(TABLE_NAME, null, " _score < '"+score+"'" , null, null, null, null, null);
			if(cc.getCount() > 0){
				int mName = cc.getColumnIndex("_name");
				int mScore = cc.getColumnIndex("_score");
				int mRank = cc.getColumnIndex("_rank");
				//String[][] tmpUpdate=new String[cc.getCount()][cc.getColumnCount()];
				String aa ="";
				String bb ="";
				String ee ="";
				for(cc.moveToFirst();!(cc.isAfterLast());cc.moveToNext()){
					if(cc.getString(mName)!=null){
					aa=aa+","+ cc.getString(mName);
					bb =bb+ ","+String.valueOf(cc.getInt(mScore));
					ee =ee+","+String.valueOf(cc.getInt(mRank));
					}
				}
				String[] aaa=aa.split(",");
				String[] bbb=bb.split(",");
				String[] ddd=ee.split(",");
				for(int i=0;i<aaa.length;i++){
					if(aaa[i]!=null && aaa[i]!="" && aaa[i].length()>0){
					Log.v("001",aaa[i]+":"+bbb[i]+":"+ddd[i]);
					ContentValues values=new ContentValues();
					values.put("_name", aaa[i]);
					values.put("_score", Integer.parseInt(bbb[i]));
					values.put("_rank", Integer.parseInt(ddd[i])+1);
					db.update(TABLE_NAME, values, "_score = '"+bbb[i]+"'", null);
					}
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			close();
		}
		
		
		return rank;
	}
	
	/**
	 * 判断用户名是否存在
	 * @param nameString
	 * @return
	 */
	public boolean isNameExist(String nameString){
		boolean flag = false;
		mDB = getReadableDatabase();	
		Cursor cursor = mDB.query(TABLE_NAME, null, "_name='"+nameString+"'", null, null, null, null, null);	//查询表中数据
		int nameIndex = cursor.getColumnIndex("_name");
		for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
			if(cursor.getString(nameIndex).equals("") ||cursor.getString(nameIndex) == null){
				flag = false;//不存在
			}else {
				flag = true;//已经存在
			}
		}
		cursor.close();	
		mDB.close();			
		return flag;
	}
	
	/**
	 * 关闭数据库
	 */
	public void close() {
		if (mDB != null)
			mDB.close();
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists " + TABLE_NAME);
		onCreate(db);
	}
	
	
	/**
	 * 根据不同的model 查询数据
	 * @param model 查询的类型
	 * @param start 查询开始的记录
	 * @param end 查询结束的记录  例如：0-15，16-20.....
	 * @return 返回的一个Cursor集，按照分数的降序排列
	 */
	public Cursor getListViewCursorByModel(int model,String start,String end) {
		Cursor cursor = null;
		try {
			mDB = getWritableDatabase();
			cursor = mDB.query(TABLE_NAME, null, "_model='"+model+"'", null, null, null, "_score desc","'"+start+"','"+end+"'");
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			mDB.close();
		}		
		return cursor;
	}
	

}
