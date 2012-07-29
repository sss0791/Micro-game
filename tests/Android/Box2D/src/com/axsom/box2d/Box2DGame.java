package com.axsom.box2d;

import helloworld.HelloWorldLayer;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;


public class Box2DGame extends Activity implements OnTouchListener{
	
	static {
        System.loadLibrary("gdx");
	}
	
	public HelloWorldLayer layer;
	
    private CCGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mGLSurfaceView = new CCGLSurfaceView(this);
		CCDirector director = CCDirector.sharedDirector();
		director.attachInView(mGLSurfaceView);
		director.setDeviceOrientation(CCDirector.kCCDeviceOrientationPortrait);
		
		setContentView(R.layout.main);
        
		
		// show FPS
        CCDirector.sharedDirector().setDisplayFPS(true);

        // frames per second
        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60.0f);
        
        // Create starting scene, add first layer as child
        //CCScene scene = Box2DScene.node();
        //scene.addChild(new Box2DLayer());
        
        // Lookup R.layout.main
        FrameLayout layout = (FrameLayout)findViewById(R.id.layout_main);
        layout.addView(mGLSurfaceView);
        mGLSurfaceView.setOnTouchListener(this);
        
        CCScene scene = CCScene.node();

		// 'layer' is an autorelease object.
		layer = new HelloWorldLayer();

		// add layer as a child to scene
		scene.addChild(layer);
        
        CCDirector.sharedDirector().runWithScene(scene);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();

        CCDirector.sharedDirector().onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        CCDirector.sharedDirector().onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CCDirector.sharedDirector().end();
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
        switch (event.getAction())
        {
        case MotionEvent.ACTION_DOWN:
    		layer.node.bounce();
    		Log.d("dfg", "Ontouch");
            break;
        case MotionEvent.ACTION_MOVE:
            break;
        case MotionEvent.ACTION_UP:
            break;
        }

        return false;
	}
}
