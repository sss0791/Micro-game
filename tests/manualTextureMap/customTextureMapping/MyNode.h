//
//  MyNode.h
//  customTextureMapping
//
//  Created by Min Kwon on 3/29/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "CCNode.h"

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

@interface MyNode : CCNode {
    Vertex2D vertices[4];   // For the polygon
    Vertex2D textCoords[4]; // For the texture

    CCTexture2D *texture;
}
@end
