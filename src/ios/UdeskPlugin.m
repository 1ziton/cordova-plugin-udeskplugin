/********* UdeskPlugin.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import "UdeskSDKManager.h"

@interface UdeskPlugin : CDVPlugin {
    // Member variables go here.
}

- (void)uDeskMethod:(CDVInvokedUrlCommand*)command;
@end

@implementation UdeskPlugin

- (void)uDeskMethod:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSDictionary * taskInfo = (NSDictionary *)[command.arguments objectAtIndex:0];
    if (taskInfo) {
        [self invokeUdeskSDK:taskInfo];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"success"];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

// 初始化SDK
- (void)invokeUdeskSDK:(NSDictionary *)taskInfo {
    //初始化公司（appKey、appID、domain都是必传字段）
    NSString * appId = [taskInfo objectForKey:@"appId"];
    NSString * appKey = [taskInfo objectForKey:@"appKey"];
    NSString * domain = [taskInfo objectForKey:@"domain"];
    
    UdeskOrganization *organization = [[UdeskOrganization alloc] initWithDomain:domain appKey:appKey appId:appId];
    
    //注意sdktoken 是客户的唯一标识，用来识别身份,是你们生成传入给我们的。
    UdeskCustomer *customer = [UdeskCustomer new];
    NSString * sdk_token = [taskInfo objectForKey:@"sdktoken"];
    customer.sdkToken = sdk_token;
    customer.cellphone = sdk_token;
    customer.nickName = [taskInfo objectForKey:@"nickName"];
    //初始化sdk
    [UdeskManager initWithOrganization:organization customer:customer];
    
    if ([taskInfo allKeys].count > 6) {
        [self invokeUdeskSDKWithTaskMessage:taskInfo];
    } else {
        [self invokeUdeskSDKWithChatPage:taskInfo];
    }
    
}

// 跳转聊天界面
- (void)invokeUdeskSDKWithChatPage: (NSDictionary *) taskInfo {
    UdeskSDKManager *manager = [[UdeskSDKManager alloc] initWithSDKStyle:[UdeskSDKStyle blueStyle]];
    UINavigationController * nav = [self getCurrentVC];
    [manager pushUdeskInViewController:nav completion:nil];
}

// 跳转聊天界面并传递订单信息
- (void)invokeUdeskSDKWithTaskMessage: (NSDictionary *)taskInfo {
    NSString * title = [taskInfo objectForKey:@"title"];
    NSString * moneyStr = [taskInfo objectForKey:@"ubTitle"];
    NSString * thumbHttpUrl = [taskInfo objectForKey:@"thumbHttpUrl"];
    NSString * commodityUrl = [taskInfo objectForKey:@"commodityUrl"];
    
    UdeskSDKManager *manager = [[UdeskSDKManager alloc] initWithSDKStyle:[UdeskSDKStyle defaultStyle]];
    
    NSDictionary *dict = @{@"productImageUrl": thumbHttpUrl,@"productTitle": title, @"productDetail":moneyStr, @"productURL":commodityUrl};
    [manager setProductMessage:dict];
    
    //        UINavigationController *nav = (UINavigationController *)[UIApplication sharedApplication].delegate.window.rootViewController;
    UINavigationController * nav = [self getCurrentVC];
    [manager pushUdeskInViewController:nav completion:nil];
    //        [manager pushUdeskInViewController:nav udeskType:UdeskIM completion:nil];
}
//获取当前屏幕显示的viewcontroller
- (UINavigationController *) getCurrentVC {
    UIWindow * window = [[UIApplication sharedApplication] keyWindow];
    if (window.windowLevel != UIWindowLevelNormal){
        NSArray *windows = [[UIApplication sharedApplication] windows];
        for(UIWindow * tmpWin in windows) {
            if (tmpWin.windowLevel == UIWindowLevelNormal) {
                window = tmpWin;
                break;
            }
        }
    }
    
    UIViewController *result = window.rootViewController;
    while (result.presentedViewController) {
        result = result.presentedViewController;
    }
    
    return (UINavigationController *)result;
}

@end

