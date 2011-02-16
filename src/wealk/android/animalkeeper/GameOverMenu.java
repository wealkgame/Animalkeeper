package wealk.android.animalkeeper;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class GameOverMenu extends BaseGameActivity{

	public static int CAMERA_WIDTH = 320;//屏幕的宽和高
	public static int CAMERA_HEIGHT ;
	private Camera mCamera;	
	private Texture mTexture;
	private final static Scene scene = new Scene(1);
	private TextureRegion mGameBackground;
	private TextureRegion mGameAgain;
	private TextureRegion mGameSubmit;
	private TextureRegion mGameExit;
	private Sprite mGameBackgroundSprite;
	private Sprite mGameAgainSprite;
	private Sprite mGameSubmitSprite;
	private Sprite mGameExitSprite;
	public int mScore;//分数
	
	@Override
	public Engine onLoadEngine() {
		WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
//        CAMERA_WIDTH = display.getWidth();
        CAMERA_HEIGHT = display.getHeight();
        if(CAMERA_HEIGHT > 480){
        	CAMERA_HEIGHT = 535;
        }
        
        Log.v("003", String.valueOf(CAMERA_WIDTH)+":"+String.valueOf(CAMERA_HEIGHT));
		
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT, 
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		// TODO Auto-generated method stub
		//获取分数
		Bundle bundle = this.getIntent().getExtras();
		mScore = bundle.getInt("score");
		
		TextureRegionFactory.setAssetBasePath("image/");
		//TextureRegion
		this.mTexture = new Texture(512, 1024, TextureOptions.DEFAULT);
		this.mGameBackground = TextureRegionFactory.createFromAsset(this.mTexture, 
				this, "bg.png", 0, 0);
		this.mGameAgain = TextureRegionFactory.createFromAsset(this.mTexture, 
				this, "again.png", 320, 0);
		this.mGameSubmit = TextureRegionFactory.createFromAsset(this.mTexture, 
				this, "submit.png", 320, 40);
		this.mGameExit = TextureRegionFactory.createFromAsset(this.mTexture, 
				this, "menuExit.png", 320, 80);
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
	}

	@Override
	public Scene onLoadScene() {
		mGameBackgroundSprite=new Sprite(0, 0, mGameBackground);
		scene.getTopLayer().addEntity(mGameBackgroundSprite);
		
		mGameAgainSprite = new Sprite(100, 85, mGameAgain){

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
					//再来一次
					startActivity(new Intent(GameOverMenu.this, GameMainActivity.class));
					GameOverMenu.this.finish();
				}
				return true;
			}
			
		};
		
		mGameSubmitSprite = new Sprite(100,150,mGameSubmit){

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
					//提交分数
					try {
						showDialog(1);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				return true;
			}
			
		};
		
		mGameExitSprite = new Sprite(100, 215, mGameExit){

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
					// 点击退出菜单之后的动作，退出游戏
					android.os.Process.killProcess(android.os.Process.myPid());
				}
				return true;
			}
			
		};
		
		scene.getTopLayer().addEntity(mGameAgainSprite);
		scene.registerTouchArea(mGameAgainSprite);
		scene.getTopLayer().addEntity(mGameSubmitSprite);
		scene.registerTouchArea(mGameSubmitSprite);
		scene.getTopLayer().addEntity(mGameExitSprite);
		scene.registerTouchArea(mGameExitSprite);
		
		return scene;
	}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1://显示提交分数页面
			
			//获取姓名
			 LayoutInflater layoutInflater = LayoutInflater.from(this);
		     View submitTextView = layoutInflater.inflate(R.layout.submit, null);
			 final EditText mNameEditText = (EditText)submitTextView.findViewById(R.id.edittext_score);
			 mNameEditText.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {				
					mNameEditText.setHint(null);
				}
			});	
			
			return new AlertDialog.Builder(GameOverMenu.this)
	        .setTitle("GameOver")
	        .setMessage("游戏得分： " + String.valueOf(mScore))
	        .setView(submitTextView)
	        .setPositiveButton("提  交", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	    				SQLiteHelper helper = new SQLiteHelper(getApplicationContext());    
	    				final String name = mNameEditText.getText().toString();
	    				final String rank = helper.queryrank(String.valueOf(mScore));
	    				if(name.equals("") || name.trim().equals("")){
	    					Toast.makeText(getApplicationContext(), "名字不能为空！", Toast.LENGTH_LONG).show();
	    					showDialog(1);
	    				}else {
	    					ContentValues values = new ContentValues();
	    					values.put("_name", name);
	    					values.put("_score", mScore);
	    					values.put("_rank", Integer.parseInt(rank)+1);
	    					
	        			    helper.insert(values);//插入排行榜数据库	
	        			    dialog.cancel();
	        			    
	        			    startActivity(new Intent(GameOverMenu.this, Top.class));
	        			    finish();
	    				}
	            }
	        })
	        .setNeutralButton("取  消", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            }
	        })
	        .create();

		default:
			break;
		}
		return null;
	}
	

}
