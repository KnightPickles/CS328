package edu.cs328;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

// Add Assets folder to working directory
public class Game extends ApplicationAdapter {
	// The basics
	private Screen screen;
	private SpriteBatch batch;
	private AssetManager assetManager;
	private GameObjectManager gameObjectManager;

	float PPM; // pixels per meter
	private ResolutionFileResolver.Resolution[] resolutions;

	// HUD Camera and variables
	private OrthographicCamera HUDCam;
	private int virtWidthHUD;
	private int virtHeightHUD;
	private float aspectRatioHUD;
	private Rectangle viewportHUD;
	private Vector3 touchPointHUD;

	// Game Camera and variables
	private OrthographicCamera GAMECam;
	private int virtWidthGAME;
	private int virtHeightGAME;
	private float aspectRatioGAME;
	private Rectangle viewportGAME;
	private Vector3 touchPointGAME;

	@Override
	public void create () {
		batch = new SpriteBatch();
		assetManager = new AssetManager();
		gameObjectManager = new GameObjectManager();
		HUDCam = new OrthographicCamera(virtWidthHUD, virtHeightHUD);
		HUDCam.setToOrtho(false, virtWidthHUD, virtHeightHUD);

		GAMECam = new OrthographicCamera(virtWidthGAME, virtHeightGAME);
		GAMECam.setToOrtho(false, virtWidthGAME, virtHeightGAME);

		PPM = virtWidthHUD / virtWidthGAME;

		touchPointHUD = new Vector3();
		touchPointGAME = new Vector3();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(screen != null) screen.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void resize(int width, int height) {
		float aspectRatio = (float)width / (float)height;
		float scale = 1f;
		float w, h;
		Vector2 crop = new Vector2(0f, 0f);

		// New viewport for HUD Cam
		if(aspectRatio > aspectRatioHUD) {
			scale = (float)height/(float)virtHeightHUD;
			crop.x = (width - virtWidthHUD * scale) / 2f;
		}
		else if(aspectRatio < aspectRatioHUD) {
			scale = (float)width/(float)virtWidthHUD;
			crop.y = (height - virtHeightHUD * scale) / 2f;
		}
		else {
			scale = (float)width/(float)virtWidthHUD;
		}

		w = (float)virtWidthHUD * scale;
		h = (float)virtHeightHUD * scale;
		viewportHUD = new Rectangle(crop.x, crop.y, w, h);

		// New viewport for GAME Cam
		crop = new Vector2(0f, 0f);
		if(aspectRatio > aspectRatioGAME) {
			scale = (float)height/(float)virtHeightGAME;
			crop.x = (width - virtWidthGAME * scale) / 2f;
		}
		else if(aspectRatio < aspectRatioGAME) {
			scale = (float)width/(float)virtWidthGAME;
			crop.y = (height - virtHeightGAME * scale) / 2f;
		}
		else {
			scale = (float)width/(float)virtWidthGAME;
		}

		w = (float)virtWidthGAME * scale;
		h = (float)virtHeightGAME * scale;
		viewportGAME = new Rectangle(crop.x, crop.y, w, h);

		Gdx.app.log("height: " + h, "PPM: " + PPM);

		if (screen != null) screen.resize(width, height);
	}

	@Override
	public void pause() {
		if(screen != null) screen.pause();
	}

	@Override
	public void resume() {
		if(screen != null) screen.resume();
	}

	// Use on any library classes that may be using underlying C++ code that won't be collected by Java's garbage collector.
	@Override
	public void dispose() {
		if(screen != null) screen.hide();
	}

	// --- CAMERA UTILITIES --- //
	public void updateGAMECam() {
		GAMECam.update();
		if (viewportGAME != null) Gdx.gl.glViewport((int) viewportGAME.x, (int) viewportGAME.y, (int) viewportGAME.width, (int) viewportGAME.height);
		batch.setProjectionMatrix(GAMECam.combined);
	}

	public void updateHUDCam() {
		HUDCam.update();
		if (viewportHUD != null) Gdx.gl.glViewport((int) viewportHUD.x, (int) viewportHUD.y, (int) viewportHUD.width, (int) viewportHUD.height);
		batch.setProjectionMatrix(HUDCam.combined);
	}

	// GAME click/touch world-screen translation
	public Vector3 unprojectGame(int xPos, int yPos) {
		GAMECam.unproject(touchPointGAME.set(xPos, yPos, 0), viewportGAME.x, viewportGAME.y, viewportGAME.width, viewportGAME.height);
		return touchPointGAME;
	}

	// HUD click/touch world-screen translation
	public Vector3 unprojectHUD(int xPos, int yPos) {
		HUDCam.unproject(touchPointHUD.set(xPos, yPos, 0), viewportHUD.x, viewportHUD.y, viewportHUD.width, viewportHUD.height);
		return touchPointHUD;
	}

	// --- GET/SET UTILITIES --- //
	public void setScreen (Screen screen) {
		if (this.screen != null) {
			this.screen.hide();
		}

		this.screen = screen;
		if (this.screen != null) {
			this.screen.show();
			this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}

	public void setHUDCam(int width, int height) {
		virtWidthHUD = width;
		virtHeightHUD = height;
		aspectRatioHUD = (float)virtWidthHUD / (float)virtHeightHUD;
	}

	public void setGAMECam(int width, int height) {
		virtWidthGAME = width;
		virtHeightGAME = height;
		aspectRatioGAME = (float)virtWidthGAME / (float)virtHeightGAME;
	}

	public SpriteBatch 			getSpritebatch() 		{ return batch; }
	public Screen 				getScreen() 			{ return screen; }
	public AssetManager 		getAssetManager() 		{ return assetManager; }
	public GameObjectManager 	getGameObjectManager() 	{ return gameObjectManager; }

	public OrthographicCamera 	getHUDCamera() 			{ return HUDCam; }
	public OrthographicCamera 	getGAMECamera() 		{ return GAMECam; }
	public Rectangle 			getViewportGAME() 		{ return viewportGAME; }
	public Rectangle 			getViewportHUD() 		{ return viewportHUD; }
	public int 					getVirtualWidthHUD() 	{ return virtWidthHUD; }
	public int 					getVirtualHeightHUD() 	{ return virtHeightHUD; }
	public int 					getVirtualWidthGAME() 	{ return virtWidthGAME; }
	public int 					getVirtualHeightGAME() 	{ return virtHeightGAME; }

	public float 				getPPM() 					 { return PPM; }
	public float 				metersToPixels(float meters) { return (float)meters * PPM; }
	public float 				pixelsToMeters(float pixels) { return (float)pixels / PPM; }

	public void setResolutions(ResolutionFileResolver.Resolution[] resolutions) { this.resolutions = resolutions; }
	public ResolutionFileResolver.Resolution[] getResolutions () { return resolutions; }

	public void setCameraZoom(float amount) {
		HUDCam.zoom = HUDCam.zoom + amount;
		GAMECam.zoom = GAMECam.zoom + amount;
	}
}
