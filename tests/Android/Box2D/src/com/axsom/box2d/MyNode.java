package com.axsom.box2d;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.cocos2d.nodes.CCNode;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.World;

public class MyNode extends CCNode {

	public static final int NUM_SEGMENTS = 40;
	public static final float PTM_RATIO = 32;
	
	static Vertex2D Vertex2DMake(float inX, float inY) {
	    Vertex2D ret = new Vertex2D();
	    ret.x = inX;
	    ret.y = inY;
	    return ret;
	}
	
	public Vertex2D[] vertices;

    public List<Body> bodies;
    
    public Body innerCircleBody;
    public Vertex2D[] triangleFanPos;
    
    public MyNode()
    {
    	vertices = new Vertex2D[4];
    	triangleFanPos = new Vertex2D[NUM_SEGMENTS+2];
    	
    	float size = 100/2;
        vertices[0] = Vertex2DMake(-size, size);
        vertices[1] = Vertex2DMake(size, size);
        vertices[2] = Vertex2DMake(-size, -size);
        vertices[3] = Vertex2DMake(size, -size);
    }
	
    public void CreatePhysicsObject(World world)
    {
        // Center is the position of circle that is in the center
        Vector2 center = new Vector2(240/PTM_RATIO, 160/PTM_RATIO);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.35f);// small circle
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 0.01f;
        fixtureDef.restitution = 1.00f;
        fixtureDef.friction = 1.0f;
        
        // Delta angle to step by
        float deltaAngle = (2.0f * (float)Math.PI) / NUM_SEGMENTS;
        
        // Radius of the wheel
        float radius = 50;
        
        // Need to store the bodies so that we can refer
        // back to it when we connect the joints
        bodies = new ArrayList<Body>();

        // For each segment...
        for (int i = 0; i < NUM_SEGMENTS; i++) {
            double theta = deltaAngle*i;
            
            // Calculate x and y based on theta
            double x = radius*Math.cos(theta);
            double y = radius*Math.sin(theta);
            
            // Remember to divide by PTM_RATIO to convert to Box2d coordinate
            Vector2 circlePosition = new Vector2((float)x/PTM_RATIO, (float)y/PTM_RATIO);
            
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyType.DynamicBody;
            // Position should be relative to the center
            bodyDef.position.set(center.x + circlePosition.x, center.y + circlePosition.y);
            
            // Create the body and fixture
            Body body;
            body = world.createBody(bodyDef);
            body.createFixture(fixtureDef);
            
            // Add the body to the array so that we can connect joints
            // to it later. b2Body is a C++ object, so we must wrap it
            // in NSValue when inserting into a NSMutableArray
            bodies.add(body);
        }
        
        // Inner circle (circle at center)
        BodyDef innerCircleBodyDef = new BodyDef();
        innerCircleBodyDef.type = BodyType.DynamicBody;
        // Position is at center
        innerCircleBodyDef.position.set(center);    
        innerCircleBody = world.createBody(innerCircleBodyDef);
        innerCircleBody.createFixture(fixtureDef);
        
        // Connect the joints
        DistanceJointDef jointDef = new DistanceJointDef();
        for (int i = 0; i < NUM_SEGMENTS; i++) {
            // The neighbor
            int neighborIndex = (i + 1) % NUM_SEGMENTS;
            
            // Get the current body and the neighbor
            Body currentBody = bodies.get(i);
            Body neighborBody = bodies.get(neighborIndex);
            
            // Connect the outer circles to each other.
            // Note that we do not set any springiness (it's rigid).
            // This is to maintain structual integrity when we apply
            // force to it. Try testing it with the frequencyHz set to a
            // higher value, and you'll see that the wheel shape will 
            // be irreversibily deformed once we apply an impulse force to it.
            jointDef.initialize(currentBody, neighborBody,
                                currentBody.getWorldCenter(), 
                                neighborBody.getWorldCenter() );
            jointDef.collideConnected = true;
            jointDef.frequencyHz = 0.0f;
            jointDef.dampingRatio = 0.5f;
            
            world.createJoint(jointDef);
            
            // Connect outer circles to the inner circle
            jointDef.initialize(currentBody, innerCircleBody, currentBody.getWorldCenter(), center);
            jointDef.collideConnected = true;
            jointDef.frequencyHz = 5.0f;
            jointDef.dampingRatio = 0.5f;
            
            world.createJoint(jointDef);
        }
    }
    
    @Override
    public void draw(GL10 gl) {
        // Using the wheel defined by the box2d objects, we'll be mapping a triangle on
        // top of it using a triangle fan. First, we calculate the center. The center
        // needs to be mulitplied by the PTM_RATIO (to get the pixel coordinate from box2d coordinate)
        // and also must be offset by the current position (remember, in HelloWorldLayer, we set
        // the position to the center of the screen (myNode.position = ccp(240, 160).
        triangleFanPos[0] = Vertex2DMake(innerCircleBody.getPosition().x * PTM_RATIO - this.position_.x, 
                                         innerCircleBody.getPosition().y * PTM_RATIO - this.position_.y);
        

        // Use each box2d body as a vertex and calculate coordinate for the triangle fan
        for (int i = 0; i < NUM_SEGMENTS; i++) {
            Body currentBody = bodies.get(i);
            Vertex2D pos = Vertex2DMake(currentBody.getPosition().x * PTM_RATIO - this.position_.x, 
                                        currentBody.getPosition().y * PTM_RATIO - this.position_.y);
            triangleFanPos[i+1] = Vertex2DMake(pos.x, pos.y);
        }
        
        // Loop back to close off the triangle fan
        triangleFanPos[NUM_SEGMENTS+1] = triangleFanPos[1];

        // Disable the states. We're not texturing mapping just yet.
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        // Set the color to red
        gl.glColor4f(1.f, 0.f, 0.f, 1.f);

        
        
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, createFloatBuffer(triangleFanPos));
        // Number of vertices is NUM_SEGMENTS+2 because we have the origin
        // plus the loop back to close off the triangle fan
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, NUM_SEGMENTS+2);

        // Re-enable states
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
    }

    protected FloatBuffer createFloatBuffer(Vertex2D[] array){  
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(array.length << 2);  
        byteBuffer.order(ByteOrder.nativeOrder());  
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();  
        for (Vertex2D vertex2d : array) {
        	float[] buf = new float[2];
        	buf[0] = vertex2d.x;
        	buf[1] = vertex2d.y;
        	floatBuffer.put(buf);
		}
          
        floatBuffer.position(0);  
        return floatBuffer;  
    }
    
    public void bounce() {
        Vector2 impulse = new Vector2(innerCircleBody.getMass() * 0, innerCircleBody.getMass() * 150);
        Vector2 impulsePoint = innerCircleBody.getPosition();
        innerCircleBody.applyLinearImpulse(impulse, impulsePoint);	        
    }
}
