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

    return self;
}

// The number of outer circles (more, the smoother the circle)
#define NUM_SEGMENTS 12

- (void) createPhysicsObject:(b2World *)world {
    // Center is the position of circle that is in the center
    b2Vec2 center = b2Vec2(240/PTM_RATIO, 260/PTM_RATIO);
    
    b2CircleShape circleShape;
    circleShape.m_radius = 0.25f;
    
    b2FixtureDef fixtureDef;
    fixtureDef.shape = &circleShape;
    fixtureDef.density = 0.1;
    fixtureDef.restitution = 0.05;
    fixtureDef.friction = 1.0;
    
    // The greater the number, the more springy
    float springiness = 4.0;
    
    // Delta angle to step by
    float deltaAngle = (2.f * M_PI) / NUM_SEGMENTS;
    
    // Radius of the wheel
    float radius = 50;

    // Need to store the bodies so that we can refer back
    // to it when we connect the joints
    NSMutableArray *bodies = [NSMutableArray array];
    
    // For each segment...
    for (int i = 0; i < NUM_SEGMENTS; i++) {
        // Current angle
        float theta = deltaAngle*i;
        
        // Calcualte the x and y based on theta 
        float x = radius*cosf(theta);
        float y = radius*sinf(theta);
        
        // Remember to divide by PTM_RATIO to convert to Box2d coordinates
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
    
    // Circle at the center
    b2BodyDef innerCircleBodyDef;
    innerCircleBodyDef.type = b2_dynamicBody;    
    // Position is at the center
    innerCircleBodyDef.position = center;    
    b2Body *innerCircleBody = world->CreateBody(&innerCircleBodyDef);
    innerCircleBody->CreateFixture(&fixtureDef);
    
    // Connect the joints    
    b2DistanceJointDef jointDef;
    for (int i = 0; i < NUM_SEGMENTS; i++) {
        // The neighbor.
        int neighborIndex = (i + 1) % NUM_SEGMENTS;
        
        // Get the current body and the neighbor
        b2Body *currentBody = (b2Body*)[[bodies objectAtIndex:i] pointerValue];
        b2Body *neighborBody = (b2Body*)[[bodies objectAtIndex:neighborIndex] pointerValue];

        // Connect the outer circles to each other
        jointDef.Initialize(currentBody, neighborBody,
                            currentBody->GetWorldCenter(), 
                            neighborBody->GetWorldCenter() );
        jointDef.collideConnected = true;
        jointDef.frequencyHz = springiness;
        jointDef.dampingRatio = 0.5f;
        
        world->CreateJoint(&jointDef);
        
        // Connect the center circle with other circles        
        jointDef.Initialize(currentBody, innerCircleBody, currentBody->GetWorldCenter(), center);
        jointDef.collideConnected = true;
        jointDef.frequencyHz = springiness;
        jointDef.dampingRatio = 0.5;
        
        world->CreateJoint(&jointDef);
    }

}


@end
