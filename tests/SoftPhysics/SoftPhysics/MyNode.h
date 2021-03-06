//
//  MyNode.h
//  customTextureMapping
//
//  Created by Min Kwon on 3/29/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "CCNode.h"
#import "Box2D.h"

typedef struct {
    GLfloat x;
    GLfloat y;
} Vertex2D;

static inline Vertex2D Vertex2DMake(GLfloat inX, GLfloat inY) {
    Vertex2D ret;
    ret.x = inX;
    ret.y = inY;
    return ret;
}

#define NUM_SEGMENTS 20

@interface MyNode : CCNode {
    // Texture
    CCTexture2D *texture;
    
    // Physics bodies
    NSMutableArray *bodies;
    float deltaAngle;
    
    // Center circle
    b2Body *innerCircleBody;
    
    // Polygon vertices
    Vertex2D triangleFanPos[NUM_SEGMENTS+2];
    
    // Texture coordinate array
    Vertex2D textCoords[NUM_SEGMENTS+2];
}

- (void) createPhysicsObject:(b2World*)world;
- (void) bounce;

@end
