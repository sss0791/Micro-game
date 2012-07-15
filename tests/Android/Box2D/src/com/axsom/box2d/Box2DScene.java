package com.axsom.box2d;

import java.util.Iterator;

import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Box2DScene extends CCScene {

	static class Box2DLayer extends CCLayer {
	    protected static final float PTM_RATIO = 32.0f;
	   
		protected final World _world;
	    protected static Body _body = null;
	    protected static CCSprite _ball = null;

		public Box2DLayer() {
	    	super();
	    	
	    	// Enable accelerometer
	    	this.setIsAccelerometerEnabled(true);
	    	
	    	// Get window size
	    	CGSize s = CCDirector.sharedDirector().winSize();
	    	
	    	// Use scaled width and height so that our edges always match the current screen
	    	float scaledWidth = s.width/PTM_RATIO;
	        float scaledHeight = s.height/PTM_RATIO;
	    	
	    	// Create sprite and add it to the layer
	    	_ball = CCSprite.sprite("Ball.jpg");
	    	_ball.setPosition(CGPoint.make(100, 100));
	    	this.addChild(_ball);
	    
	    	// Create a world
	    	Vector2 gravity = new Vector2(0.0f, -30.0f);
	    	boolean doSleep = true;
	    	_world = new World(gravity, doSleep);
	    	 
	    	// Create edges around the entire screen
	    	// Define the ground body.
	        BodyDef bxGroundBodyDef = new BodyDef();
	        bxGroundBodyDef.position.set(0.0f, 0.0f);
			
			// The body is also added to the world.
	        Body groundBody = _world.createBody(bxGroundBodyDef);
	
	        // Define the ground box shape.
	        PolygonShape groundBox = new PolygonShape();
	
	        Vector2 bottomLeft = new Vector2(0f,0f);
	        Vector2 topLeft = new Vector2(0f,scaledHeight);
	        Vector2 topRight = new Vector2(scaledWidth,scaledHeight);
	        Vector2 bottomRight = new Vector2(scaledWidth,0f);
	        
			// bottom
			groundBox.setAsEdge(bottomLeft, bottomRight);
			groundBody.createFixture(groundBox,0);
			
			// top
			groundBox.setAsEdge(topLeft, topRight);
			groundBody.createFixture(groundBox,0);
			
			// left
			groundBox.setAsEdge(topLeft, bottomLeft);
			groundBody.createFixture(groundBox,0);
			
			// right
			groundBox.setAsEdge(topRight, bottomRight);
			groundBody.createFixture(groundBox,0);
	    	 
	    	// Create ball body and shape	    	
	    	BodyDef ballBodyDef = new BodyDef();
	    	ballBodyDef.type = BodyType.DynamicBody;
	    	ballBodyDef.position.set(100/PTM_RATIO, 100/PTM_RATIO);
	    	Body ballBody = _world.createBody(ballBodyDef);
	    	ballBody.setUserData(_ball);
	
	    	CircleShape circle = new CircleShape();
	    	circle.setRadius(26.0f/PTM_RATIO);
	  	
	    	FixtureDef ballShapeDef = new FixtureDef();
	    	ballShapeDef.shape = circle;
	    	ballShapeDef.density = 1.0f;
	    	ballShapeDef.friction = 0.2f; 
	    	ballShapeDef.restitution = 0.8f;	    	
	    	ballBody.createFixture(ballShapeDef); 
	    	
			schedule(tickCallback);

	    }
		
		private UpdateCallback tickCallback = new UpdateCallback() {
			
			@Override
			public void update(float d) {
				tick(d);
			}
		};
		
		public synchronized void tick(float delta) {
	    	synchronized (_world) {
	    		_world.step(delta, 8, 1);
	    	}
	
	    	// Iterate over the bodies in the physics world
	    	Iterator<Body> it = _world.getBodies();
	    	while(it.hasNext()) {
	    		Body b = it.next();
	    		Object userData = b.getUserData();
	    		
	    		if (userData != null && userData instanceof CCSprite) {
	    			
	    			//Synchronize the Sprites position and rotation with the corresponding body
	    			CCSprite sprite = (CCSprite)userData;
	    			sprite.setPosition(b.getPosition().x * PTM_RATIO, b.getPosition().y * PTM_RATIO);
	    			sprite.setRotation(-1.0f * ccMacros.CC_RADIANS_TO_DEGREES(b.getAngle()));

	    		}
	    	}
		}
		
		@Override
	    public void ccAccelerometerChanged(float accelX, float accelY, float accelZ) {

		    // Landscape values
	    	Vector2 gravity = new Vector2(accelY * 15, -accelX * 15);
	    	_world.setGravity(gravity);

		}
		
	}  
}

