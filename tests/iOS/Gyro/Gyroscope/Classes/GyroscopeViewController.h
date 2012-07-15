#import <UIKit/UIKit.h>
#import <CoreMotion/CoreMotion.h>

@interface GyroscopeViewController : UIViewController {

	CMMotionManager *motionManager;
	NSTimer *timer;
	float rotation;
	
}

@property(nonatomic, retain) IBOutlet UIImageView *image;

-(IBAction)toggleUpdates:(id)sender;

@end

