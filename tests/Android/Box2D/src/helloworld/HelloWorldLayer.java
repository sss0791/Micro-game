package helloworld;

import java.util.Iterator;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.actions.CCTimer;
import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteSheet;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class HelloWorldLayer extends CCLayer{ //implements OnTouchListener{
	private String tag = "dfg";
	public static final float PTM_RATIO = 32.0f;
	public static final int kTagTileMap = 1;
	public static final int kTagBatchNode = 1;
	public static final int kTagAnimation1 = 1;
	public MyNode node;
	private static Random random = new Random();
	private World _world;
	private MyNode node1;
	protected static CCSprite _ball;
	
    static int generateRandom(int n) {
        return Math.abs(random.nextInt()) % n;
    }
	
	public static CCScene scene() {
		// 'scene' is an autorelease object.
		CCScene scene = CCScene.node();

		// 'layer' is an autorelease object.
		HelloWorldLayer layer = new HelloWorldLayer();

		// add layer as a child to scene
		scene.addChild(layer);

		// return the scene
		return scene;
	}

	public HelloWorldLayer()
	{
		super();
		
		this.setIsAccelerometerEnabled(true);
//		
//		// Get window size
//		CGSize s = CCDirector.sharedDirector().winSize();
//
//		// Use scaled width and height so that our boundaries always match
//		// the current screen
//		float scaledWidth = s.width / PTM_RATIO;
//		float scaledHeight = s.height / PTM_RATIO;
//
//		// Create sprite and add it to the layer
//		_ball = CCSprite.sprite("Ball.jpg");
//		_ball.setPosition(CGPoint.make(100, 100));
//		this.addChild(_ball);
//
//		// Create a world
//		Vector2 gravity = new Vector2(0.0f, -30.0f);
//		boolean doSleep = true;
//		_world = new World(gravity, doSleep);
//
//		// Create edges around the entire screen
//		// Define the ground body.
//		BodyDef bxGroundBodyDef = new BodyDef();
//		bxGroundBodyDef.position.set(0.0f, 0.0f);
//
//		// The body is also added to the world.
//		Body groundBody = _world.createBody(bxGroundBodyDef);
//
//		// Define the ground box shape.
//		PolygonShape groundBox = new PolygonShape();
//		Vector2 bottomLeft = new Vector2(0f, 0f);
//		Vector2 topLeft = new Vector2(0f, scaledHeight);
//		Vector2 topRight = new Vector2(scaledWidth, scaledHeight);
//		Vector2 bottomRight = new Vector2(scaledWidth, 0f);
//		// bottom
//		groundBox.setAsEdge(bottomLeft, bottomRight);
//		groundBody.createFixture(groundBox, 0);
//		// top
//		groundBox.setAsEdge(topLeft, topRight);
//		groundBody.createFixture(groundBox, 0);
//		// left
//		groundBox.setAsEdge(topLeft, bottomLeft);
//		groundBody.createFixture(groundBox, 0);
//		// right
//		groundBox.setAsEdge(topRight, bottomRight);
//		groundBody.createFixture(groundBox, 0);
//
//		// Create ball body and shape
//		BodyDef ballBodyDef = new BodyDef();
//		ballBodyDef.type = BodyType.DynamicBody;
//		ballBodyDef.position.set(100 / PTM_RATIO, 100 / PTM_RATIO);
//		Body ballBody = _world.createBody(ballBodyDef);
//		ballBody.setUserData(_ball);
//		CircleShape circle = new CircleShape();
//		circle.setRadius(26.0f / PTM_RATIO);
//		FixtureDef ballShapeDef = new FixtureDef();
//		ballShapeDef.shape = circle;
//		ballShapeDef.density = 1.0f;
//		ballShapeDef.friction = 0.8f;
//		ballShapeDef.restitution = 1.0f;
//		ballBody.createFixture(ballShapeDef);
		
		
//		this.isTouchEnabled_ = true;
//		this.isAccelerometerEnabled_ = true;

		CGSize screenSize = CCDirector.sharedDirector().winSize();
		Log.d(tag , "Screen width screen height "+ screenSize.width + " , width" + screenSize.height);

		Vector2 gravity = new Vector2();
		gravity.set(0.0f, -10.0f);

		boolean doSleep = true;

		// Construct a world object, which will hold and simulate the rigid bodies.
		_world = new World(gravity, doSleep);

		_world.setContinuousPhysics(true);
		
		// Debug Draw functions
		//GLESDebugDraw m_debugDraw = new GLESDebugDraw( PTM_RATIO );
		//world.setDebugDraw(m_debugDraw);
		
//		int flags = 0;
//		flags += b2DebugDraw::e_shapeBit;
//		flags += b2DebugDraw::e_jointBit;
//		flags += b2DebugDraw::e_aabbBit;
//		flags += b2DebugDraw::e_pairBit;
//		flags += b2DebugDraw::e_centerOfMassBit;
//		m_debugDraw->SetFlags(flags);		


		// Define the ground body.
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(0, 0); // bottom-left corner

		// Call the body factory which allocates memory for the ground body
		// from a pool and creates the ground box shape (also from a pool).
		// The body is also added to the world.
		Body groundBody = _world.createBody(groundBodyDef);

		// Define the ground box shape.
		PolygonShape groundBox = new PolygonShape();		

					// bottom
		groundBox.setAsEdge(new Vector2(0,0), new Vector2(screenSize.width/PTM_RATIO,0));
			groundBody.createFixture(groundBox,0);

		// top
		groundBox.setAsEdge(new Vector2(0,screenSize.height/PTM_RATIO), new Vector2(screenSize.width/PTM_RATIO,screenSize.height/PTM_RATIO));
			groundBody.createFixture(groundBox,0);

		// left
		groundBox.setAsEdge(new Vector2(0,screenSize.height/PTM_RATIO), new Vector2(0,0));
			groundBody.createFixture(groundBox,0);

		// right
		groundBox.setAsEdge(new Vector2(screenSize.width/PTM_RATIO,screenSize.height/PTM_RATIO), new Vector2(screenSize.width/PTM_RATIO,0));
		groundBody.createFixture(groundBox,0);

		node = new MyNode();
	    node.setPosition(CGPoint.ccp(340, 260));
        node.createPhysicsObject(_world);
        this.addChild(node);
        
		//node1 = new MyNode();
	    //node1.setPosition(CGPoint.ccp(0, 0));
        //node1.createPhysicsObject(_world);
        //this.addChild(node1);
		
		schedule(tickCallback);
	}
	



	void addNewSpriteWithCoords(CGPoint p)
	{
		//CCLOG(@"Add sprite %0.2f x %02.f",p.x,p.y);
		CCSpriteSheet batch = (CCSpriteSheet)this.getChild(kTagBatchNode);

		//We have a 64x64 sprite sheet with 4 different 32x32 images.  The following code is
		//just randomly picking one of the images
		int idx = (generateRandom(2) > 0.5 ? 0:1);
		int idy = (generateRandom(2) > 0.5 ? 0:1);
		CCSprite sprite = new CCSprite(batch, CGRect.make(32 * idx,32 * idy,32,32));
		batch.addChild(sprite); 

		sprite.setPosition(CGPoint.make(p.x, p.y));

		// Define the dynamic body.
		//Set up a 1m squared box in the physics world
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;

		bodyDef.position.set(p.x/PTM_RATIO, p.y/PTM_RATIO);
		//bodyDef.userData.set(sprite);
		Body body = _world.createBody(bodyDef);

		// Define another box shape for our dynamic body.
		PolygonShape dynamicBox = new PolygonShape();
		dynamicBox.setAsBox(.5f, .5f);//These are mid points for our 1m box

		// Define the dynamic body fixture.
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = dynamicBox;	
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.3f;
		body.createFixture(fixtureDef);
		
	}
	
	private UpdateCallback tickCallback = new UpdateCallback() {
		@Override
		public void update(float d) {
			tick(d);
		}
	};
	
	public void tick(float dt)
	{
		//It is recommended that a fixed time step is used with Box2D for stability
		//of the simulation, however, we are using a variable time step here.
		//You need to make an informed choice, the following URL is useful
		//http://gafferongames.com/game-physics/fix-your-timestep/

		int velocityIterations = 8;
		int positionIterations = 1;

		// Instruct the world to perform a single step of simulation. It is
		// generally best to keep the time step and iterations fixed.
		_world.step(dt, velocityIterations, positionIterations);
		Iterator<Body> it = _world.getBodies();
		while (it.hasNext()) {

			Body b = it.next();
			Object userData = b.getUserData();

			if (userData != null && userData instanceof CCSprite) {

				// Synchronize the Sprites position and rotation with the
				// corresponding body
				CCSprite sprite = (CCSprite) userData;
				sprite.setPosition(b.getPosition().x * PTM_RATIO,
						b.getPosition().y * PTM_RATIO);
				sprite.setRotation(-1.0f
						* ccMacros.CC_RADIANS_TO_DEGREES(b.getAngle()));

			}

		}

	}

	   @Override
	    public void ccAccelerometerChanged(float accelX, float accelY, float accelZ) {
	        // Landscape values
	        Vector2 gravity =  new Vector2(accelY * 15, -accelX * 15);
	        _world.setGravity(gravity);
	    }



}
