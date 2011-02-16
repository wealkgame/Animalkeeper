package wealk.android.animalkeeper;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.HUD;
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

import android.content.Intent;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * 
 * @author w e a l k 
 *
 */
public class GameStartMenu extends BaseGameActivity{

	public int CAMERA_WIDTH = 320;//屏幕的宽和高
	public int CAMERA_HEIGHT ;
	private Camera mCamera;
	
	private Texture mTexture;
	
	private Scene scene;
	private HUD hud;
	
	public TextureRegion mMenuBackground; //background of menu 
	public TextureRegion mZooKeeper;
	public TextureRegion mMenuStart; //start Game
	public TextureRegion mMenuHelp;  //help
	public TextureRegion mMenuHelpAbout;  //help
	public TextureRegion mMenuScore; //score
	public TextureRegion mMenuExit;  //Exit
	
	public Sprite menuStart;
	public Sprite menuHelp;
	public Sprite menuScore;
	public Sprite menuExit;
	public Sprite menuHelpabout;
	public Sprite zookeeper;
	
	
	private int mGameState = 1; //1是菜单状态 2是帮助状态3是分数排行榜4是退出状态
	private int mCount = 0; //记录帮助说明被展开的次数
	
	@Override
	public Engine onLoadEngine() {
		WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
//        CAMERA_WIDTH = display.getWidth();
        CAMERA_HEIGHT = display.getHeight();
        if(CAMERA_HEIGHT > 480){
        	CAMERA_HEIGHT = 535;
        }
        
        Log.v("001", String.valueOf(CAMERA_WIDTH)+":"+String.valueOf(CAMERA_HEIGHT));
		
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT, 
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		scene = new Scene(1);
		hud = new HUD();
		mTexture = new Texture(1024, 1024, TextureOptions.DEFAULT);
		
		TextureRegionFactory.setAssetBasePath("image/");
		
		this.mMenuBackground = TextureRegionFactory.createFromAsset(this.mTexture, 
				this, "bg.png", 0, 0);
		
		this.mMenuStart = TextureRegionFactory.createFromAsset(this.mTexture, 
				this, "menuStart.png", 320, 0);
		
		this.mMenuHelp = TextureRegionFactory.createFromAsset(this.mTexture, 
				this, "menuHelp.png", 440, 0);
		
		this.mMenuScore = TextureRegionFactory.createFromAsset(this.mTexture, 
				this, "menuScore.png", 560, 0);
		
		this.mMenuExit = TextureRegionFactory.createFromAsset(this.mTexture, 
				this, "menuExit.png", 680, 0);
		
		this.mMenuHelpAbout = TextureRegionFactory.createFromAsset(this.mTexture, 
				this, "menuhelpabout.png", 800, 0);
		
		this.mZooKeeper = TextureRegionFactory.createFromAsset(this.mTexture, 
				this, "zookeeper.png", 0, 535);
		
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
	}

	@Override
	public Scene onLoadScene() {
		final Sprite mBackground = new Sprite(0, 0, this.mMenuBackground);

		scene.getTopLayer().addEntity(mBackground);
		
		menuStart = new Sprite(100, 185, this.mMenuStart){

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
					//进入游戏-开始
					startActivity(new Intent(GameStartMenu.this, GameMainActivity.class));
					finish();
				}
				return true;
			};
			
		};
		
		menuHelp = new Sprite(100, 230, this.mMenuHelp){

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// 点击帮助菜单之后的动作
				drawHelp();
				return true;
			}
			
		};
		
		menuScore =new Sprite(100, 275, this.mMenuScore){

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// 点击分数菜单之后的动作
				startActivity(new Intent(GameStartMenu.this, Top.class));
				finish();
				return true;
			}
			
		};
		
		menuExit = new Sprite(100, 340, this.mMenuExit){

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				// 点击退出菜单之后的动作
				System.exit(0);
				return true;
			}
			
		};
		
		zookeeper = new Sprite(45, 30, this.mZooKeeper);
		
		menuHelpabout = new Sprite(60, 60, this.mMenuHelpAbout);
		
		drawMenu();
				
		this.mCamera.setHUD(hud);
		
		return scene;
	}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub
		
	}
	
	
	 /**
     * 画菜单界面
     */
	private void drawMenu(){
		
		hud.getTopLayer().addEntity(menuStart);
		hud.getTopLayer().addEntity(menuHelp);
		hud.getTopLayer().addEntity(menuScore);
		hud.getTopLayer().addEntity(menuExit);

		hud.registerTouchArea(menuStart);
		hud.registerTouchArea(menuHelp);
		hud.registerTouchArea(menuScore);
		hud.registerTouchArea(menuExit);
		scene.getTopLayer().addEntity(zookeeper);
		if(mCount == 1){
			scene.getTopLayer().removeEntity(menuHelpabout);
		}	
		mGameState = 1;
		mCount = 1;
	}
	
	private void drawHelp(){
		hud.getTopLayer().removeEntity(menuStart);
		hud.getTopLayer().removeEntity(menuHelp);
		hud.getTopLayer().removeEntity(menuScore);
		hud.getTopLayer().removeEntity(menuExit);
		
		hud.unregisterTouchArea(menuStart);
		hud.unregisterTouchArea(menuHelp);
		hud.unregisterTouchArea(menuScore);
		hud.unregisterTouchArea(menuExit);
		
		
		scene.getTopLayer().removeEntity(zookeeper);
		scene.getTopLayer().addEntity(menuHelpabout);
		
		mGameState = 2;
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(mGameState == 1){
				System.exit(0);
			}else{
				if(mGameState == 2){
					drawMenu();
				}
			}
		}
		return false;
	}
	

}
