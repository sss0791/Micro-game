//
//  MyNode.m
//  customTextureMapping
//
//  Created by Min Kwon on 3/29/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "cocos2d.h"
#import "MyNode.h"

#define PTM_RATIO 32.f

@implementation MyNode

- (id) init {
    self = [super init];

    texture = [[CCTextureCache sharedTextureCache] addImage:@"Ball.png"];        

    return self;
}

- (void) createPhysicsObject:(b2World *)world {
    // Center is the position of the circle that is in the center (inner circle)
    b2Vec2 center = b2Vec2(240/PTM_RATIO, 160/PTM_RATIO);
    b2CircleShape circleShape;
    circleShape.m_radius = 0.1f;
    
    b2FixtureDef fixtureDef;
    fixtureDef.shape = &circleShape;
    fixtureDef.density = 0.1;
    fixtureDef.restitution = 0.05;
    fixtureDef.friction = 1.0;
    
    // Delta angle to step by
    deltaAngle = (2.f * M_PI) / NUM_SEGMENTS;
    
    // Radius of the wheel
    float radius = 50;

    // Need to store the bodies so that we can refer back
    // to it when we connect the joints
    bodies = [[NSMutableArray alloc] init];
    
    for (int i = 0; i < NUM_SEGMENTS; i++) {
        // Current angle
        float theta = deltaAngle*i;
        
        // Calculate x and y based on theta
        float x = radius*cosf(theta);
        float y = radius*sinf(theta);
        // Remember to divide by PTM_RATIO to convert to Box2d coordinate
        b2Vec2 circlePosition = b2Vec2(x/PTM_RATIO, y/PTM_RATIO);
        
        b2BodyDef bodyDef;
        bodyDef.type = b2_dynamicBody;
        // Position should be relative to the center
        bodyDef.position = (center + circlePosition);
        
        // Create the body and fixture
        b2Body *body;
        body = world->CreateBody(&bodyDef);
        body->CreateFixture(&fixtureDef);
        
        // Add the body to the array to connect joints to it
        // later. b2Body is a C++ object, so must wrap it
        // in NSValue when inserting into it NSMutableArray
        [bodies addObject:[NSValue valueWithPointer:body]];
    }
    
    // Circle at the center (inner circle)
    b2BodyDef innerCircleBodyDef;
    // Make the inner circle larger
    circleShape.m_radius = 0.8f;
    innerCircleBodyDef.type = b2_dynamicBody;

    // Position is at the center
    innerCircleBodyDef.position = center;
    innerCircleBody = world->CreateBody(&innerCircleBodyDef);
    innerCircleBody->CreateFixture(&fixtureDef);
    
    // Connect the joints
    b2DistanceJointDef jointDef;
    for (int i = 0; i < NUM_SEGMENTS; i++) {
        // The neighbor
        const int neighborIndex = (i + 1) % NUM_SEGMENTS;
        
        // Get current body and neighbor
        b2Body *currentBody = (b2Body*)[[bodies objectAtIndex:i] pointerValue];
        b2Body *neighborBody = (b2Body*)[[bodies objectAtIndex:neighborIndex] pointerValue];
        
        // Connect the outer circles to each other
        jointDef.Initialize(currentBody, neighborBody,
                            currentBody->GetWorldCenter(), 
                            neighborBody->GetWorldCenter() );
        // Specifies whether the two connected bodies should collide with each other
        jointDef.collideConnected = true;
        jointDef.frequencyHz = 0.0f;
        jointDef.dampingRatio = 0.0f;
        
        world->CreateJoint(&jointDef);
        
        // Connect the center circle with other circles        
        jointDef.Initialize(currentBody, innerCircleBody, currentBody->GetWorldCenter(), center);
        jointDef.collideConnected = true;
        jointDef.frequencyHz = 8.0;
        jointDef.dampingRatio = 0.5;
        
        world->CreateJoint(&jointDef);
    }
}



- (void) draw {
    // Using the wheel defined by the box2d objects, we'll be mapping a triangle on
    // top of it using a triangle fan. First, we calculate the center. The center
    // needs to be mulitplied by the PTM_RATIO (to get the pixel coordinate from box2d coordinate)
    // and also must be offset by the current position (remember, in HelloWorldLayer, we set
    // the position to the center of the screen (myNode.position = ccp(240, 160).
    triangleFanPos[0] = Vertex2DMake(innerCircleBody->GetPosition().x * PTM_RATIO - self.position.x, 
                                     innerCircleBody->GetPosition().y * PTM_RATIO - self.position.y);
    // Use each box2d body as a vertex and calculate coordinate for the triangle fan
    for (int i = 0; i < NUM_SEGMENTS; i++) {
        b2Body *currentBody = (b2Body*)[[bodies objectAtIndex:i] pointerValue];
        Vertex2D pos = Vertex2DMake(currentBody->GetPosition().x * PTM_RATIO - self.position.x, 
                                    currentBody->GetPosition().y * PTM_RATIO - self.position.y);
        triangleFanPos[i+1] = Vertex2DMake(pos.x, pos.y);
    }
    // Loop back to close off the triangle fan
    triangleFanPos[NUM_SEGMENTS+1] = triangleFanPos[1];

    // The first vertex is the center of the triangle fan.
    // So the first coordinate of the texture map should
    // map to the center of the triangle fan. The texture
    // map coordinates are normalized between 0 and 1, so
    // the center is (0.5. 0.5)
    textCoords[0] = Vertex2DMake(0.5f, 0.5f);
    for (int i = 0; i < NUM_SEGMENTS; i++) {
        
        GLfloat theta = M_PI + (deltaAngle * i);
        
        // Calculate the X and Y coordinates for texture mapping.
        // Need to normalize the cosine and sine functions so that the
        // values are between 0 and 1. Cosine and sine functions have
        // values from -1 to 1, so we divide by 2 and add 0.5 to it to
        // normalize the values between 0 and 1.
        textCoords[i+1] = Vertex2DMake(0.5+cosf(theta)*0.5, 
                                       0.5+sinf(theta)*0.5);

    }
    // Close it off.
    textCoords[NUM_SEGMENTS+1] = textCoords[1];
    
    // Enable texture mapping stuff
    glEnable(GL_TEXTURE_2D);
    glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    glDisableClientState(GL_COLOR_ARRAY);
    
    // Bind the OpenGL texture
    glBindTexture(GL_TEXTURE_2D, [texture name]);

    // Send the texture coordinates to OpenGL
    glTexCoordPointer(2, GL_FLOAT, 0, textCoords);
    // Send the polygon coordinates to OpenGL
    glVertexPointer(2, GL_FLOAT, 0, triangleFanPos);
    // Draw it 
    glDrawArrays(GL_TRIANGLE_FAN, 0, NUM_SEGMENTS+2);
    
    glEnableClientState(GL_COLOR_ARRAY);
}

- (void) bounce {
    b2Vec2 impulse = b2Vec2(innerCircleBody->GetMass() * 0, innerCircleBody->GetMass() * 40);
    b2Vec2 impulsePoint = innerCircleBody->GetPosition();
    innerCircleBody->ApplyLinearImpulse(impulse, impulsePoint);	          
}

@end
