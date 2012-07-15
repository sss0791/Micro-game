//
//  MyNode.m
//  customTextureMapping
//
//  Created by Min Kwon on 3/29/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "cocos2d.h"
#import "MyNode.h"

#define PTM_RATIO 42.f

@implementation MyNode

- (id) init {
    self = [super init];

    GLfloat size = 100/2;
    vertices[0] = Vertex2DMake(-size, size);
    vertices[1] = Vertex2DMake(size, size);
    vertices[2] = Vertex2DMake(-size, -size);
    vertices[3] = Vertex2DMake(size, -size);
    
    return self;
}

- (void) createPhysicsObject:(b2World *)world {
    // Center is the position of circle that is in the center
    b2Vec2 center = b2Vec2(240/PTM_RATIO, 160/PTM_RATIO);
    b2CircleShape circleShape;
    circleShape.m_radius = 0.55f;// small circle
    
    b2FixtureDef fixtureDef;
    fixtureDef.shape = &circleShape;
    fixtureDef.density = 0.01;
    fixtureDef.restitution = 1.00;
    fixtureDef.friction = 1.0;
    
    // Delta angle to step by
    float deltaAngle = (2.f * M_PI) / NUM_SEGMENTS;
    
    // Radius of the wheel
    float radius = 50;
    
    // Need to store the bodies so that we can refer
    // back to it when we connect the joints
    bodies = [[NSMutableArray alloc] init];

    // For each segment...
    for (int i = 0; i < NUM_SEGMENTS; i++) {
        // Calculate current angle
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
        
        // Add the body to the array so that we can connect joints
        // to it later. b2Body is a C++ object, so we must wrap it
        // in NSValue when inserting into a NSMutableArray
        [bodies addObject:[NSValue valueWithPointer:body]];
    }
    
    // Inner circle (circle at center)
    b2BodyDef innerCircleBodyDef;
    innerCircleBodyDef.type = b2_dynamicBody;
    // Position is at center
    innerCircleBodyDef.position = center;    
    innerCircleBody = world->CreateBody(&innerCircleBodyDef);
    innerCircleBody->CreateFixture(&fixtureDef);
    
    // Connect the joints
    b2DistanceJointDef jointDef;
    for (int i = 0; i < NUM_SEGMENTS; i++) {
        // The neighbor
        const int neighborIndex = (i + 1) % NUM_SEGMENTS;
        
        // Get the current body and the neighbor
        b2Body *currentBody = (b2Body*)[[bodies objectAtIndex:i] pointerValue];
        b2Body *neighborBody = (b2Body*)[[bodies objectAtIndex:neighborIndex] pointerValue];
        
        // Connect the outer circles to each other.
        // Note that we do not set any springiness (it's rigid).
        // This is to maintain structual integrity when we apply
        // force to it. Try testing it with the frequencyHz set to a
        // higher value, and you'll see that the wheel shape will 
        // be irreversibily deformed once we apply an impulse force to it.
        jointDef.Initialize(currentBody, neighborBody,
                            currentBody->GetWorldCenter(), 
                            neighborBody->GetWorldCenter() );
        jointDef.collideConnected = true;
        jointDef.frequencyHz = 0.0f;
        jointDef.dampingRatio = 0.5f;
        
        world->CreateJoint(&jointDef);
        
        // Connect outer circles to the inner circle
        jointDef.Initialize(currentBody, innerCircleBody, currentBody->GetWorldCenter(), center);
        jointDef.collideConnected = true;
        jointDef.frequencyHz = 5.0;
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

    // Disable the states. We're not texturing mapping just yet.
    glDisable(GL_TEXTURE_2D);
    glDisableClientState(GL_TEXTURE_COORD_ARRAY);
    glDisableClientState(GL_COLOR_ARRAY);

    // Set the color to red
    glColor4f(1.f, 0.f, 0.f, 1.f);

    glVertexPointer(2, GL_FLOAT, 0, triangleFanPos);
    // Number of vertices is NUM_SEGMENTS+2 because we have the origin
    // plus the loop back to close off the triangle fan
    glDrawArrays(GL_TRIANGLE_FAN, 0, NUM_SEGMENTS+2);

    // Re-enable states
    glEnableClientState(GL_COLOR_ARRAY);
    glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    glEnable(GL_TEXTURE_2D);
}

- (void) bounce {
    b2Vec2 impulse = b2Vec2(innerCircleBody->GetMass() * 0, innerCircleBody->GetMass() * 150);
    b2Vec2 impulsePoint = innerCircleBody->GetPosition();
    innerCircleBody->ApplyLinearImpulse(impulse, impulsePoint);	        
}

@end
