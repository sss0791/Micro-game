#import "GyroscopeViewController.h"

@implementation GyroscopeViewController

@synthesize image;

-(IBAction)toggleUpdates:(id)sender {
	if ([sender isOn]) {
		[motionManager startGyroUpdates];
		timer = [NSTimer scheduledTimerWithTimeInterval:1/30.0
												 target:self 
											   selector:@selector(doGyroUpdate)
											   userInfo:nil 
												repeats:YES];
	} else {
		[motionManager stopGyroUpdates];
		[timer invalidate];
	}
}

-(void)doGyroUpdate {
	float rate = motionManager.gyroData.rotationRate.z;
	if (fabs(rate) > .2) {
		float direction = rate > 0 ? 1 : -1;
		rotation += direction * M_PI/90.0;
		self.image.transform = CGAffineTransformMakeRotation(rotation);
	}
}

- (void)viewDidLoad {
    [super viewDidLoad];
	motionManager = [[CMMotionManager alloc] init];
}


/*
// The designated initializer. Override to perform setup that is required before the view is loaded.
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    if ((self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil])) {
        // Custom initialization
    }
    return self;
}
*/

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView {
}
*/


/*
// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
    [super viewDidLoad];
}
*/


/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}


- (void)dealloc {
	[motionManager release];
	self.image = nil;
    [super dealloc];
}

@end
