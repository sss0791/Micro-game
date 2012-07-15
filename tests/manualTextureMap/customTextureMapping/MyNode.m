//
//  MyNode.m
//  customTextureMapping
//
//  Created by Min Kwon on 3/29/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "cocos2d.h"
#import "MyNode.h"

@implementation MyNode

- (id) init {
    self = [super init];

    // Load the texture
    texture = [[CCTextureCache sharedTextureCache] addImage:@"Kirby.png"];        

    GLfloat size = [texture pixelsWide]/2;

    // Vertices for the square
    vertices[0] = Vertex2DMake(-size, size);
    vertices[1] = Vertex2DMake(size, size);
    vertices[2] = Vertex2DMake(-size, -size);
    vertices[3] = Vertex2DMake(size, -size);
    
    // Because the texture is flipped about its x-axis,
    // we flip the y coordinates to correct this.
    textCoords[0] = Vertex2DMake(0, 0);
    textCoords[1] = Vertex2DMake(1, 0);
    textCoords[2] = Vertex2DMake(0, 1);
    textCoords[3] = Vertex2DMake(1, 1);

    // If you use the mapping below, then the image will
    // be flipped. To correc this, use the coordinates
    // above, or save the image in a flipped state so it
    // will be unflipped when mapped!
    /*
    textCoords[0] = Vertex2DMake(0, 1);
    textCoords[1] = Vertex2DMake(1, 1);
    textCoords[2] = Vertex2DMake(0, 0);
    textCoords[3] = Vertex2DMake(1, 0);
     */
    
    return self;
}

- (void) draw {
    // Following two states must be enabled for texture mapping
    glEnable(GL_TEXTURE_2D);
    glEnableClientState(GL_TEXTURE_COORD_ARRAY);

    glDisableClientState(GL_COLOR_ARRAY);
    
    // Bind the OpenGL texture
    glBindTexture(GL_TEXTURE_2D, [texture name]);

    // The vertices for the polygon
    glVertexPointer(2, GL_FLOAT, 0, vertices);
    // The vertices for the texture
    glTexCoordPointer(2, GL_FLOAT, 0, textCoords);
    // Send it off to OpenGL
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

    // Re-enable states
    glEnableClientState(GL_COLOR_ARRAY);

}


@end

