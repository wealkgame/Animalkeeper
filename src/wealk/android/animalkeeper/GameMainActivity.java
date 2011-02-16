package wealk.android.animalkeeper;

import java.io.IOException;
import java.util.Random;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.HUD;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.StrokeFont;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;


public class GameMainActivity extends BaseGameActivity {
	
	public static int CAMERA_WIDTH = 320;//屏幕的宽和高
	public static int CAMERA_HEIGHT;
	
	public Camera mCamera;
	
	public IUpdateHandler mIUpdateHandler,mainIUpdateHandler ;
	
	public Texture mTexture;
	public Texture mTextureTiled;
	
	public static Scene scene;
	public HUD hud;
	
	public TextureRegion mGameBackground;           //背景
	
	//==========
	///Animal
	//==========
	public TiledTextureRegion[] mAnimal;              //碰撞之前的Animal
	public TextureRegion[] mAnimalEat;                //Animal吃东西之前的动作
	public TextureRegion[] mAnimalEatTC;               //Animal吃东西之后的动作
	public TextureRegion[] mAnimalEatTW;               //Animal吃东西之后的反应
	
	public static AnimatedSprite[] mAnimatedMoveSprite; //碰撞之前的Animal动作
	public Sprite[] mAnimalEatSM;           //Animal吃的动作
	public Sprite[] mAnimalEatSC;           //吃之后Animal的动作正确的和错误的
	public Sprite[] mAnimalEatSW;           //
	
	public static int tmpAnimal;
	public static int mGameStart; //0表示游戏前 ；1表示进入游戏，初始化为2个动物；2表示游戏中新增一个动物；3表示再增一个动物；4表示游戏暂停；5表示游戏来电处理；9表示游戏结束
	public static int tmpGameStart;
	
	public int mAnimalState; //动物的状态 1表示原始状态 2表示张口吃的动作 3表示吃之后酝酿的动作4吃之后的反应
	public int drawMonkeyEatCount;//0表示初始化1表示原始状态2表示吃及其吃之后的反应	
	//cow-monkey-lion-pets
	public static boolean[] mAnimalMove = new boolean[]{false,false,false,false};
	public static float[] mAnimalPositionX = new float[]{80,155,230,0};
	public static float[] mAnimalPositionY = new float[]{380,380,380,380};
	public static long[] mAnimalCount = new long[]{300,300,300,300};//动物的停顿时间
	
	public static float tmpAnimalPositionX = 0;
	public static float tmpAnimalPositionY = 0;

	//=========
	//fruit
	//=========
	public TiledTextureRegion[] mFruit;   //
	public  Fruit[] mFruitSprite;
	public static Fruit tmpFruitSprite; //中间变量专门来最终决定是哪个水果在游戏中出现
	
	public static Random mRandrom ;  //用来随机产生水果
	public static final float mEight = 80.0f;
	public static float GAME_VELOCITY; //水果移动的速度
	public static float tmpGAME_VELOCITY;
	public static float GAME_VELOCITYAcceleration; //水果移动的加速度 更具不同的关数有不同的速度
	public static final int mFruitPositionXAdd = 15; //水果的横坐标相比宠物的横坐标要增加5个单位
	public static final float GAME_VELOCITYADD = 5.0f;//长按手机时候水果速度的增量
	
	public static int mFruitCount = 1;  // 1-2是草 3-5是菠萝6-7是肉8-9是宠物吃的食物
	
	public static int mFruitCountNum;  //记录水果种类的总数
	public static float TwoHundred = 200;          
	public static float mFruitX = TwoHundred;     //水果的横坐标
	
	public static boolean mRandromX;  //标识：在随机产生水果的时候随机产生其横坐标
	
	public static float mFruitMoveX;        //记录水果运动的X坐标
//	private static float mFruitMoveY;        //记录水果运动的Y坐标
	
	public static int mFruitCountSum;  //记录随机产生水果的个数
		
	//========
	//碰撞检测
	//========
	public static boolean mCollisionTrue = false;   //用来检测是否碰撞  碰撞之后水果消失  重新随机产生水果
	public Boolean[] mCollisionCorrect;
	public static int mCollisionWhichOne;  //1 is cow,2 is monkey 3 is lion ,4 is pets	
	
	//===============
	//关数、血量和分数
	//===============
			
	private Texture mStrokeFontTextureScore;  //绘制字体用
	private Texture mStrokeFontTextureLevel;
		
	private static final int FONT_SIZE = 20;	
	
	//关数
	private static StrokeFont mStrokeFontLevel;
	public static int mLevel;
	public static int mLevelAddCondition;
	private static int mTenLevel;
	private static int mBitsLevel;
	private static Text textStrokeLevel;
	private static boolean mShowLevel;//true为第一次显示，否则不是第一次显示 

	//血量
	private TextureRegion mBloodRed;
	private TextureRegion mBloodGray;
	
	private Sprite[] mBloodRedSprite;
	private Sprite[] mBloodGraySprite;
	private static int mBloodCount;
	
	//分数
	private static int mScore;
	private StrokeFont mStrokeFontScore;
	private static int mThousandScore;
	private static int mHundredScore;
	private static int mTenScore;
	private static int mBitsScore;
	private static int mAddScore; //没进行一次合理的碰撞增加的分数
	private static boolean mShowScore;//true为第一次显示，否则不是第一次显示 
	private static Text textStrokeScore;
	
	//==============
	//音乐
	//==============
	private Music mMusic;
	private Sound mGoodMusic;
	private Sound mBadMusic;
	
	
	//pause
	private TextureRegion mPausedTextureRegion;
	private CameraScene mPauseScene;
	GamePauseThread gamePauseRun;
	public static TelephonyManager tmpTelephonyManager;
	
	//TimeCount
	public int mTimeCount;
	
	
	//
	public Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
        	switch(msg.what){
        	case 1:
        		GamePause1();
        		break;
        	case 2:
        		GamePause2();
        		break;
        	default:
        			break;
        	}
		}
	};
	
	
	@Override
	public Engine onLoadEngine() {
		WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
//        CAMERA_WIDTH = display.getWidth();
        CAMERA_HEIGHT = display.getHeight();
        if(CAMERA_HEIGHT > 480){
        	CAMERA_HEIGHT = 535;
        }
        
        Log.v("002", String.valueOf(CAMERA_WIDTH)+":"+String.valueOf(CAMERA_HEIGHT));
		
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT, 
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera).setNeedsMusic(true).setNeedsSound(true));
	}

	@Override
	public void onLoadResources() {
		//资源初始化
		initResourses();
		//
		TextureRegionFactory.setAssetBasePath("image/");		
		// 
		mTexture = new Texture(1024, 1024, TextureOptions.DEFAULT);		
		this.mGameBackground = TextureRegionFactory.createFromAsset(this.mTexture, 
				this, "gamebg.png", 0, 0);
		for(int i = 0;i < mAnimalEat.length;i++){
			mAnimalEat[i] = TextureRegionFactory.createFromAsset(this.mTexture, 
					this, "animaleat"+i+".png", 320 + i*80,0 );
		}
		for(int i = 0;i < mAnimalEatTC.length;i++){
			mAnimalEatTC[i] = TextureRegionFactory.createFromAsset(this.mTexture, 
					this, "animaleatTC"+i+".png", 320+i*80, 100);	
		}
		for(int i = 0;i < mAnimalEatTW.length;i++){
			mAnimalEatTW[i] = TextureRegionFactory.createFromAsset(this.mTexture, 
					this, "animaleatTR"+i+".png", 320+i*80, 180);
		}
		this.mBloodRed = TextureRegionFactory.createFromAsset(this.mTexture, 
				this, "red.png", 640, 0);
		this.mBloodGray = TextureRegionFactory.createFromAsset(this.mTexture, 
				this, "gray.png", 670, 0);
		
		this.mPausedTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture,
				this, "pausebg.png", 0, 535);
		
		//
		mTextureTiled = new Texture(1024, 1024, TextureOptions.DEFAULT);
		for(int i = 0;i < mAnimal.length ; i ++){
			mAnimal[i] = TextureRegionFactory.createTiledFromAsset(this.mTextureTiled, 
					this, "animal"+i+".png", 0,i*80,2,1);//动物的高为80
		}
		for(int i = 0; i<mFruit.length;i++){
			mFruit[i] = TextureRegionFactory.createTiledFromAsset(this.mTextureTiled, 
					this, "fruit"+i+".png", 160 + i*50,0,1,1);//水果的宽为50
		}
		//Font
		this.mStrokeFontTextureScore = new Texture(256, 256, TextureOptions.BILINEAR);
		this.mStrokeFontScore = new StrokeFont(this.mStrokeFontTextureScore, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 
				FONT_SIZE, true, Color.BLACK, 2, Color.YELLOW,true);
		this.mStrokeFontTextureLevel = new Texture(128, 128, TextureOptions.BILINEAR);
		GameMainActivity.mStrokeFontLevel = new StrokeFont(this.mStrokeFontTextureLevel, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 
				FONT_SIZE, true, Color.BLACK, 2, Color.YELLOW,true);
		//music
		MusicFactory.setAssetBasePath("sound/");
		try {
			this.mMusic = MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), this, "musicbg.ogg");
			this.mMusic.setLooping(true);
			this.mMusic.setVolume(10);
		} catch (final IOException e) {
			Debug.e("Error", e);
		}
		
		SoundFactory.setAssetBasePath("sound/");
		try {
			this.mGoodMusic = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "musicgood.ogg");
			this.mGoodMusic.setVolume(10);
		} catch (final IOException e) {
			Debug.e("mGoodMusic Error", e);
		}
		try {
			this.mBadMusic = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "musicbad.ogg");
			this.mBadMusic.setVolume(10);
		} catch (final IOException e) {
			Debug.e("mBadMusic Error", e);
		}
		
		this.mEngine.getTextureManager().loadTextures(this.mTexture,this.mTextureTiled,
				this.mStrokeFontTextureScore,this.mStrokeFontTextureLevel);
		this.mEngine.getFontManager().loadFonts(this.mStrokeFontScore,GameMainActivity.mStrokeFontLevel);
		
	}
	
	/**
	 * initResourses
	 */
	private void initResourses(){
		scene = new Scene(1);
		hud = new HUD();
		
		//
		drawMonkeyEatCount = 0;
		
		//Animal
		mAnimal = new TiledTextureRegion[4];
		mAnimalEat = new TextureRegion[4];
		mAnimalEatTC = new TextureRegion[4];
		mAnimalEatTW = new TextureRegion[4];
		
		
		mAnimatedMoveSprite = new AnimatedSprite[4];
		mAnimalEatSM = new Sprite[4];
		mAnimalEatSC = new Sprite[4];
		mAnimalEatSW = new Sprite[4];
		
		mAnimalState = 1;
		tmpAnimal = -1;
		
		//fruit
		mFruit = new TiledTextureRegion[9];
		mFruitSprite = new Fruit[9];
		GAME_VELOCITY = mEight;
		mRandromX = false;
		mFruitCountSum = 0;
		mFruitCountNum = 9;
		
		
		//collision
		mCollisionCorrect = new Boolean[]{false,false,false,false};
		mCollisionWhichOne = -1; 
		
		mRandrom = new Random();
		mTimeCount = 1;
		
		
		//level
		mLevel = 0;
		mLevelAddCondition = 5;
		mTenLevel = 0;
		mBitsLevel = 0;
		mShowLevel = true;//true为第一次显示，否则不是第一次显示 
		
		//blood
		mBloodRedSprite = new Sprite[5];
		mBloodGraySprite = new Sprite[5];
		mBloodCount = 5;
		
		//分数
		mScore = 0;
		mThousandScore = 0 ;
		mHundredScore = 0;
	    mTenScore = 0;
		mBitsScore = 0;
		mAddScore = 10; //没进行一次合理的碰撞增加的分数
		mShowScore = true;//true为第一次显示，否则不是第一次显示
		
		//
		gamePauseRun = new GamePauseThread();
		tmpTelephonyManager = null;
		
	}
	

	@Override
	public Scene onLoadScene() {
		
		this.mPauseScene = new CameraScene(1, this.mCamera);
		/* Make the 'PAUSED'-label centered on the camera. */
		final int x = CAMERA_WIDTH / 2 - this.mPausedTextureRegion.getWidth() / 2;
		final int y = CAMERA_HEIGHT / 2 - this.mPausedTextureRegion.getHeight() / 2;
		final Sprite pausedSprite = new Sprite(x, y, this.mPausedTextureRegion);
		this.mPauseScene.getTopLayer().addEntity(pausedSprite);
		/* Makes the paused Game look through. */
		this.mPauseScene.setBackgroundEnabled(false);
		
		//background
		final Sprite mBackground=new Sprite(0, 0, mGameBackground);
		scene.getTopLayer().addEntity(mBackground);
		
		//music
		GameMainActivity.this.mMusic.play();
		
		for(int i = 0;i < mBloodRedSprite.length;i++){
			mBloodRedSprite[i] = new Sprite(78 + i*22, 28, mBloodRed);
			mBloodGraySprite[i] = new Sprite(78 + i*22, 26,mBloodGray);
		}
		drawBlood(mBloodCount);
		
		//分数
		drawScore(mScore);
		
		for(int i = 0;i<mAnimatedMoveSprite.length ; i++){//加载四个动物的四种状态
			final int tmpAnimal = i;
			
			mAnimatedMoveSprite[i] = new AnimatedSprite(mAnimalPositionX[i], mAnimalPositionY[i], mAnimal[i]){

				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
//					Debug(tmpAnimal);
					switch (tmpAnimal) {//鼠标在屏幕上的三个动作，第一个是开始，后两个是结束的标致
					case 0:
						onTouchAnimal(pSceneTouchEvent,tmpAnimal);
						break;
					case 1:
						onTouchAnimal(pSceneTouchEvent,tmpAnimal);
						break;
					case 2:
						onTouchAnimal(pSceneTouchEvent,tmpAnimal);
						break;
					case 3:
						onTouchAnimal(pSceneTouchEvent,tmpAnimal);
						break;

					default:
						break;
					}
					return true;
				}
				
			};
			
			mAnimatedMoveSprite[i].animate(mAnimalCount[i]);
			
			mAnimalEatSM[i] = new Sprite(mAnimalPositionX[i], mAnimalPositionY[i],mAnimalEat[i]);
			mAnimalEatSC[i] = new Sprite(mAnimalPositionX[i], mAnimalPositionY[i],mAnimalEatTC[i]);
			mAnimalEatSW[i] = new Sprite(mAnimalPositionX[i], mAnimalPositionY[i],mAnimalEatTW[i]);
			
		}
		
		
		
		for(int i = 0;i < mFruit.length;i++){
			mFruitSprite[i] = new Fruit(0, -50, mFruit[i]);
		}
		
		
		//画出水果
		drawFruit();
		
		//加载那些动物
		this.mCamera.setHUD(hud);
		
		//initIUpdateHandler
		initIUpdateHandler();
		
		/**
		 *  The actual collision-checking. 
		 *  碰撞检测的过程也要游戏中的元素进行不同的动作
		 *  分别是碰撞前、碰撞中和碰撞后
		 */
		try {
			scene.registerUpdateHandler(mIUpdateHandler);
		} catch (Exception e) {
			gameOverToWhere();
		}
		
		return scene;
	}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub
		
	}
	
	
	/**
	 * initIUpdateHandler
	 */
	private void initIUpdateHandler(){
		mIUpdateHandler = new IUpdateHandler() {

			@Override
			public void onUpdate(float pSecondsElapsed) {//控制游戏的线程
				// TODO Auto-generated method stub				
				switch (mGameStart) {
				case 7://游戏暂停
					gamePauseRun.run();
					break;
				case 9://游戏结束
					mGameStart = 0;					
					gameOverToWhere();
					break;
				default:
					break;
				}
				
				if(tmpFruitSprite.collidesWith(mAnimatedMoveSprite[0])){
					
					if(mFruitCount == 1 || mFruitCount == 2){
						mCollisionCorrect[0] = true;
					}					
					mCollisionWhichOne = 0;						
					mCollisionTrue = true;
					updateAnimalEatBeforeState();
					updateFruit();
				}else{
					if(tmpFruitSprite.collidesWith(mAnimatedMoveSprite[1])){
						if(mFruitCount == 3 || mFruitCount == 4|| mFruitCount == 5){
							mCollisionCorrect[1] = true;
						}
						mCollisionWhichOne = 1;						
						mCollisionTrue = true;
						updateAnimalEatBeforeState();
						updateFruit();
					}else{
						if(tmpFruitSprite.collidesWith(mAnimatedMoveSprite[2])){
							if(mFruitCount == 6 || mFruitCount == 7){
								mCollisionCorrect[2] = true;
							}
							mCollisionWhichOne = 2;						
							mCollisionTrue = true;
							updateAnimalEatBeforeState();
							updateFruit();
						}else{
							if(tmpFruitSprite.collidesWith(mAnimatedMoveSprite[3])){
								if(mFruitCount == 8 || mFruitCount == 9){
									mCollisionCorrect[3] = true;
								}
								mCollisionWhichOne = 3;						
								mCollisionTrue = true;
								updateAnimalEatBeforeState();
								updateFruit();
							}else{
								mAnimalState();
							}
						}
					}
				}
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	
	/**
	 * touch the animal
	 * @param pSceneTouchEvent
	 * @param tmpAnimal
	 */
	private void onTouchAnimal(TouchEvent pSceneTouchEvent,int tmpAnimal){
		
		GameMainActivity.tmpAnimal = tmpAnimal;
		
		if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN) {
			//点击猴子之后的动作
			mAnimalMove[tmpAnimal] = true;
		}
		if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP){
			mAnimalMove[tmpAnimal] = false;
		}
		if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_MOVE){
			mAnimalMove[tmpAnimal] = false;
		}
	}
	
	
	private void updateAnimalEatBeforeState(){
		mAnimalState ++ ;
		drawMonkeyEatAfter(mAnimalState);
		mAnimalState ++;
	}
	
	/**
	 * 1表示原始状态， 2-3表示张口吃的动作， 4-7吃之后的反应，
	 */
	private void mAnimalState(){		
		if(mAnimalState == 1){
			drawMonkeyEatBefore();
		}else{
			if(mAnimalState == 5){
				drawMonkeyEatAfter(mAnimalState);
				mAnimalState ++;
			}else{
				if(mAnimalState == 10){
					mAnimalState = 1;
				}else{
					mAnimalState ++;
				}
			}
		}
	}
	
	
	/**
	 * change animal position
	 * @param front
	 * @param end
	 */
	private void changeAnimalPosition(int front,int end){		
		hud.getTopLayer().removeEntity(mAnimatedMoveSprite[end]);
		hud.unregisterTouchArea(mAnimatedMoveSprite[end]);
		hud.getTopLayer().removeEntity(mAnimatedMoveSprite[front]);
		hud.unregisterTouchArea(mAnimatedMoveSprite[front]);
		
		tmpAnimalPositionX = mAnimatedMoveSprite[front].getX();
		tmpAnimalPositionY = mAnimatedMoveSprite[front].getY();
		mAnimatedMoveSprite[front].setPosition(mAnimatedMoveSprite[end].getX(), mAnimatedMoveSprite[end].getY());
		mAnimatedMoveSprite[end].setPosition(tmpAnimalPositionX, tmpAnimalPositionY);
		
		hud.getTopLayer().addEntity(mAnimatedMoveSprite[end]);
		hud.registerTouchArea(mAnimatedMoveSprite[end]);
		hud.getTopLayer().addEntity(mAnimatedMoveSprite[front]);
		hud.registerTouchArea(mAnimatedMoveSprite[front]);
		
		//change other Animal position
		tmpAnimalPositionX = mAnimalEatSM[front].getX();
		tmpAnimalPositionY = mAnimalEatSM[front].getY();
		mAnimalEatSM[front].setPosition(mAnimalEatSM[end].getX(), mAnimalEatSM[end].getY());
		mAnimalEatSM[end].setPosition(tmpAnimalPositionX, tmpAnimalPositionY);
		
		tmpAnimalPositionX = mAnimalEatSC[front].getX();
		tmpAnimalPositionY = mAnimalEatSC[front].getY();
		mAnimalEatSC[front].setPosition(mAnimalEatSC[end].getX(), mAnimalEatSC[end].getY());
		mAnimalEatSC[end].setPosition(tmpAnimalPositionX, tmpAnimalPositionY);
		
		tmpAnimalPositionX = mAnimalEatSW[front].getX();
		tmpAnimalPositionY = mAnimalEatSW[front].getY();
		mAnimalEatSW[front].setPosition(mAnimalEatSW[end].getX(), mAnimalEatSW[end].getY());
		mAnimalEatSW[end].setPosition(tmpAnimalPositionX, tmpAnimalPositionY);
		
		
	}
	
	/**
	 * 画出动物在等待水果落下的等待状态
	 */
	private void drawMonkeyEatBefore(){
		if(drawMonkeyEatCount == 0){
			
			switch (mGameStart) {
			case 0:
				addAnimal(0);
				addAnimal(1);
				mGameStart ++; //进入游戏中
				break;
			case 1:
				if(mLevel == 2){
					addAnimal(2);
					mGameStart ++;
				}
				break;
			case 2:
				if(mLevel == 4){
					addAnimal(3);
					mGameStart ++;
				}
				break;
			default:
				break;
			}
			
			if(mCollisionWhichOne >= 0){
				addAnimal(mCollisionWhichOne);
				
				if(mCollisionCorrect[mCollisionWhichOne]){
					scene.getTopLayer().removeEntity(mAnimalEatSC[mCollisionWhichOne]);
					mCollisionCorrect[mCollisionWhichOne] = false;
				}else{
					scene.getTopLayer().removeEntity(mAnimalEatSW[mCollisionWhichOne]);
				}
				
				if(mTimeCount % 3 == 0){
					int front = mRandrom.nextInt(4);
					int end = mRandrom.nextInt(4);
					while(front == end){
						end = mRandrom.nextInt(4);
					}
					changeAnimalPosition(front, end);
				}
				
			}
			
			mCollisionWhichOne = -1;
			drawMonkeyEatCount ++;
		}
	}
	
	
	
	
	private void drawMonkeyEatAfter(int state){
		if(drawMonkeyEatCount == 1){
			switch (state) {
			case 2:
//				Log.v("001", String.valueOf(mCollisionWhichOne));
				scene.getTopLayer().addEntity(mAnimalEatSM[mCollisionWhichOne]);
				removeAnimal(mCollisionWhichOne);
				break;
			case 5:
				if(mCollisionCorrect[mCollisionWhichOne]){
					scene.getTopLayer().addEntity(mAnimalEatSC[mCollisionWhichOne]);				
					//播放碰撞的声音
					GameMainActivity.this.mGoodMusic.play();
					updateScore();
					
				}else{
					scene.getTopLayer().addEntity(mAnimalEatSW[mCollisionWhichOne]);
					//播放碰撞的声音
					GameMainActivity.this.mBadMusic.play();
					updateBlood();
				}
				scene.getTopLayer().removeEntity(mAnimalEatSM[mCollisionWhichOne]);
				drawMonkeyEatCount = 0;
				break;

			default:
				break;
			}
		}
	}
	
	
	/**
	 * add animal
	 * @param id
	 */
	private void addAnimal(int id){
		hud.getTopLayer().addEntity(mAnimatedMoveSprite[id]);
		hud.registerTouchArea(mAnimatedMoveSprite[id]);
	}
		
		
	
 
	/**
	 * 
	 * @param id
	 */
	private void removeAnimal(int id){
		hud.getTopLayer().removeEntity(mAnimatedMoveSprite[id]);
		hud.unregisterTouchArea(mAnimatedMoveSprite[id]);
	}
	
	
	private void updateFruit(){
		removeFruit();
		GAME_VELOCITY = mEight;//初始化水果下落的速度
		drawFruit();
	}
	
	
	/**
	 * 绘制游戏中随机产生的水果
	 */
	private void drawFruit(){
		
		if(mFruitCountSum % mLevelAddCondition == 0){//当水果每增加5个 过一关
			updateLevel();
		}
		
		
		if(mLevel > 3){
			mFruitCount = mRandrom.nextInt(mFruitCountNum) + 1;
			
			
			mTimeCount++;
			if(mTimeCount>10){
				mTimeCount = 0;
			}			
			
		}else{
			if(mLevel < 2){
				mFruitCount = mRandrom.nextInt(5) + 1;
			}else{
				mFruitCount = mRandrom.nextInt(7) + 1;
			}
		}
		
		tmpFruitSprite = mFruitSprite[mFruitCount - 1];
				
		
		//初始化产生水果的属性
		tmpGAME_VELOCITY = mRandrom.nextInt((int)GAME_VELOCITYAcceleration);
		tmpFruitSprite.setVelocityY(GAME_VELOCITY+(float)tmpGAME_VELOCITY);
		scene.getTopLayer().addEntity(tmpFruitSprite);
		
		mFruitCountSum++;
		
//		Debug(mFruitCountSum);
		mRandromX = true;
		
	}
	
	/**
	 * 移除游戏中下落的水果
	 */
	private static void removeFruit(){		
		scene.getTopLayer().removeEntity(tmpFruitSprite);
	}
	
	/**
	 * 更新关数
	 */
	private static void updateLevel(){
		GAME_VELOCITYAcceleration += 5.0f;
		mLevel++;
//		Log.v("mLevel", String.valueOf(mLevel));
		drawLevel(mLevel);
	}
	
	/**
	 * 更新血量
	 */
	private void updateBlood(){
		mBloodCount --;
		drawBlood(mBloodCount);
	}
	
	
	/**
	 * 更新分数
	 */
	private void updateScore(){
		mScore += mAddScore;
		drawScore(mScore);
	}
	
	/**
	 * 画游戏中的关数
	 * @param level
	 */
	private static void drawLevel(int level){
		mTenLevel = level / 10;
		mBitsLevel = (level - mTenLevel * 10) % 10;
		
		if(mShowLevel){//如果是第一次加载关数则直接显示
			textStrokeLevel = new Text(22, 28, mStrokeFontLevel, String.valueOf(mTenLevel)+" "+String.valueOf(mBitsLevel));
			scene.getTopLayer().addEntity(textStrokeLevel);			
			mShowLevel = false;
		}else{//不是第一次加载需要先将之前的关数移除
			scene.getTopLayer().removeEntity(textStrokeLevel);
			textStrokeLevel = new Text(22, 28, mStrokeFontLevel, String.valueOf(mTenLevel)+" "+String.valueOf(mBitsLevel));
			scene.getTopLayer().addEntity(textStrokeLevel);
		}
	}
	
	
	/**
	 * 画游戏中设置的血量
	 * @param mBloodCount
	 */
	private void drawBlood(int mBloodCount){		
		switch (mBloodCount) {
		case 5:
			scene.getTopLayer().addEntity(mBloodRedSprite[0]);
			scene.getTopLayer().addEntity(mBloodRedSprite[1]);
			scene.getTopLayer().addEntity(mBloodRedSprite[2]);
			scene.getTopLayer().addEntity(mBloodRedSprite[3]);
			scene.getTopLayer().addEntity(mBloodRedSprite[4]);
			break;
		case 4:
			scene.getTopLayer().removeEntity(mBloodRedSprite[4]);
			scene.getTopLayer().addEntity(mBloodGraySprite[4]);
			break;
		case 3:
			scene.getTopLayer().removeEntity(mBloodRedSprite[3]);
			scene.getTopLayer().addEntity(mBloodGraySprite[3]);
			break;
		case 2:
			scene.getTopLayer().removeEntity(mBloodRedSprite[2]);
			scene.getTopLayer().addEntity(mBloodGraySprite[2]);
			break;
		case 1:
			scene.getTopLayer().removeEntity(mBloodRedSprite[1]);
			scene.getTopLayer().addEntity(mBloodGraySprite[1]);
			break;
		case 0:
			//游戏结束
			scene.getTopLayer().removeEntity(mBloodRedSprite[0]);
			scene.getTopLayer().addEntity(mBloodGraySprite[0]);
			mGameStart = 9;
			break;

		default:
			break;
		}
		
	}
	
	/**
	 * 游戏暂停
	 */
	public void GamePause(){
		if(mEngine.isRunning()) {
			scene.setChildScene(this.mPauseScene, false, true, true);
			this.mEngine.stop();
			this.mMusic.pause();		
		} else {
			scene.clearChildScene();
			this.mEngine.start();
			this.mMusic.play();	
		}
	}
	
	public void GamePause1(){
		this.mEngine.onPause();
		this.mMusic.pause();		
	}
	
	public void GamePause2(){
		gamePauseRun.interrupt();
		this.mEngine.start();
		this.mMusic.play();
	}
	
	
	
	
	/**
	 * 画游戏中的分数
	 * @param mScore
	 */
	private void drawScore(int mScore){
		//分数暂时最高定位百位
		mThousandScore = mScore / 1000;
		mHundredScore = (mScore - mThousandScore * 1000) / 100;
		mTenScore = (mScore - mThousandScore * 1000 - mHundredScore * 100) / 10;
		mBitsScore = (mScore - mThousandScore * 1000 - mHundredScore * 100 - mTenScore * 10) % 10;
		if(mThousandScore == 0){
			if(mHundredScore == 0){//如果百位为0 不显示百位的数字
				if(mTenScore == 0){//如果十位为0 不显示十位的数字
					if(mShowScore){//判断是不是第一次显示，第一次显示直接加载
						textStrokeScore = new Text(250, 28, this.mStrokeFontScore, String.valueOf(mTenScore)+" "+String.valueOf(mBitsScore));
						scene.getTopLayer().addEntity(textStrokeScore);					
						mShowScore = false;
					}else{//不是第一次加载需要先移除之前的分数，再加载新的分数
						scene.getTopLayer().removeEntity(textStrokeScore);
						textStrokeScore = new Text(250, 28, this.mStrokeFontScore, String.valueOf(mTenScore)+" "+String.valueOf(mBitsScore));
						scene.getTopLayer().addEntity(textStrokeScore);
					}
				}else{//如果十位不是0 显示出来
					scene.getTopLayer().removeEntity(textStrokeScore);
					textStrokeScore = new Text(250, 28, this.mStrokeFontScore, String.valueOf(mTenScore)+" "+String.valueOf(mBitsScore));
					scene.getTopLayer().addEntity(textStrokeScore);
				}
			}else{//如果百位不是0 显示出来
				scene.getTopLayer().removeEntity(textStrokeScore);
				textStrokeScore = new Text(230, 28, this.mStrokeFontScore,String.valueOf(mHundredScore) + " " + String.valueOf(mTenScore)+" "+String.valueOf(mBitsScore));
				scene.getTopLayer().addEntity(textStrokeScore);
			}
		}else{
			scene.getTopLayer().removeEntity(textStrokeScore);
			textStrokeScore = new Text(230, 28, this.mStrokeFontScore,String.valueOf(mThousandScore) + " " + String.valueOf(mHundredScore) + " " + String.valueOf(mTenScore)+" "+String.valueOf(mBitsScore));
			scene.getTopLayer().addEntity(textStrokeScore);
		}
	}
	
	
	/**
	 * 游戏结束的去向
	 */
	private void gameOverToWhere(){
		
		//转到游戏结束界面
		Intent intent = new Intent();
		intent.setClass(GameMainActivity.this, GameOverMenu.class);	
		Bundle bundle = new Bundle();
		bundle.putInt("score", mScore);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
		
		mScore = 0;
		GameMainActivity.this.finish();
		
		gameOver();
	}
	
	/**
	 * 游戏结束需要对这些数据还原
	 */
	public void gameOver(){
		scene.unregisterUpdateHandler(mIUpdateHandler);//注销注册事件
		removeFruit();//移除水果
		GameMainActivity.this.mMusic.resume();
		initResourses();
		
		System.exit(0);
	}
	
	
	/**
	 * Log
	 * @param bug
	 */
	public void Debug(String bug){
		org.anddev.andengine.util.Debug.v(bug);
	}
	
	public void Debug(int bug){
		org.anddev.andengine.util.Debug.v(String.valueOf(bug));
	}
	
	
	/**
	 * 按键事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){//返回上一界面，并且将所有数据初始化
			try {
				mGameStart = 9;//游戏结束
			} catch (Exception e) {
			}
			
			return false;
			
		}
		
		if(keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
			GamePause();

			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	
	/**
	 * 该类为AnimatedSprite增加一个不断更新该精灵位置的一个方法
	 * @author w e a l k
	 *
	 */
	private static class Fruit extends AnimatedSprite{

		

		public Fruit(float pX, float pY, TiledTextureRegion pTiledTextureRegion) {
			super(pX, pY, pTiledTextureRegion);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onManagedUpdate(float pSecondsElapsed) {
			// TODO Auto-generated method stub
			if(mRandromX){
				if(mLevel > 3){
					int tmp = GameMainActivity.mRandrom.nextInt(4);
					this.mX = GameMainActivity.mAnimatedMoveSprite[tmp].getX() + GameMainActivity.mFruitPositionXAdd;
					
				}else{
					if(mLevel < 2){
						int tmp = GameMainActivity.mRandrom.nextInt(2);
						this.mX = GameMainActivity.mAnimatedMoveSprite[tmp].getX() + GameMainActivity.mFruitPositionXAdd;
					}else{
						int tmp = GameMainActivity.mRandrom.nextInt(3);
						this.mX = GameMainActivity.mAnimatedMoveSprite[tmp].getX() + GameMainActivity.mFruitPositionXAdd;
					}
				}
				mRandromX = false;
			}
			
			if(GameMainActivity.tmpAnimal >= 0){
				if(GameMainActivity.mAnimalMove[GameMainActivity.tmpAnimal]){
					this.mX = GameMainActivity.mAnimatedMoveSprite[GameMainActivity.tmpAnimal].getX() + GameMainActivity.mFruitPositionXAdd;
					GAME_VELOCITY += GAME_VELOCITYADD;
					tmpFruitSprite.setVelocityY(GAME_VELOCITY+(float)tmpGAME_VELOCITY);
				}
				GameMainActivity.tmpAnimal = -1;
			}
			

			
			
			//最后要删除
			if(this.mY < 0) {
				this.setVelocityY(GAME_VELOCITY);
			} else if(this.mY + this.getHeight() > CAMERA_HEIGHT) {
				this.mY = 0;
			}
			
			
			
			if(mCollisionTrue){
				this.mY = 0;
				mCollisionTrue = false;
			}
			
			super.onManagedUpdate(pSecondsElapsed);
		}
				
		
	}
	
	
	class GamePauseThread extends Thread implements Runnable{		
		@Override
		public void run() {	
			boolean start = true;
			boolean tmp = true;
			while(start){
				try {
					switch (tmpTelephonyManager.getCallState()) {
					case TelephonyManager.CALL_STATE_RINGING:
						if(tmp){
							GamePause1();
							tmp = false;
						}
						break;
					case TelephonyManager.CALL_STATE_IDLE:
						if(!tmp){
							GamePause2();
							tmp = true;
							start = false;
							mGameStart = tmpGameStart;
						}
						break;
					case TelephonyManager.CALL_STATE_OFFHOOK:
						if(!tmp){
							GamePause2();
							tmp = true;
							start = false;
							mGameStart = tmpGameStart;
						}
						break;
					default:
						break;
					}
					
					
					Thread.sleep(80);//游戏休息80ms
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			
		}
		
		
		
		
	}

}