package cordova.plugin.UdeskPlugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import udesk.UdeskConst;
import udesk.UdeskSDKManager;
import udesk.model.UdeskCommodityItem;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import udesk.activity.UdeskChatActivity;
import udesk.messagemanager.UdeskMessageManager;
import udesk.model.MsgNotice;
import yzt.jzt.R;
/**
 * This class echoes a string called from JavaScript.
 */
public class UdeskPlugin extends CordovaPlugin {
    //未读消息
    private String msg="";
    //未读消息数
    private int num=0;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("uDeskMethod")) {
            if(args!=null){
                JSONObject object=args.getJSONObject(0);
                String appId,appKey,domain,sdktoken,NICK_NAME,CELLPHONE,CustomerUrl,Title,SubTitle,ThumbHttpUrl,CommodityUrl;
                appId=object.getString("appId");//appId
                appKey=object.getString("appKey");//appKey
                domain=object.getString("domain");//domain
                sdktoken=object.getString("sdktoken");//token
                NICK_NAME=object.getString("nickName");//师傅姓名
                CELLPHONE=object.getString("sdktoken");//师傅电话
                CustomerUrl=object.getString("customerUrl");//师傅头像地址
                /**
                 客户信息初始化
                 */
                UdeskSDKManager.getInstance().initApiKey(cordova.getActivity(),domain,appKey,appId);
                Map<String,String> info=new HashMap<String,String>();
                info.put(UdeskConst.UdeskUserInfo.USER_SDK_TOKEN,sdktoken);
                info.put(UdeskConst.UdeskUserInfo.NICK_NAME,NICK_NAME);
                info.put(UdeskConst.UdeskUserInfo.CELLPHONE,CELLPHONE);
                UdeskSDKManager.getInstance().setUserInfo(cordova.getActivity(),sdktoken,info);
                //  设置用户头像
                UdeskSDKManager.getInstance().setCustomerUrl(CustomerUrl);
                UdeskMessageManager.getInstance().event_OnNewMsgNotice.bind(this,"OnNewMsgNotice");
                if (object.getString("messageFlag").equals("getUnreadMsg")){
                    /*
                    * 获取未读消息数
                    * */
                    num=UdeskSDKManager.getInstance().getCurrentConnectUnReadMsgCount();
                    JSONObject obj=new JSONObject();
                    obj.put("messageCount",num);
                    obj.put("messageStr",msg);
                    //获取未读消息
                    callbackContext.success(obj);
                    return true;
                }
                // 打开客服
                UdeskSDKManager.getInstance().entryChat(cordova.getActivity());
                if (!object.isNull("title")){
                    Title=object.getString("title");//商品标题
                    SubTitle=object.getString("ubTitle");//红色字体，可以填写价格
                    ThumbHttpUrl=object.getString("thumbHttpUrl");//商品图片
                    CommodityUrl=object.getString("commodityUrl");//商品链接
                    /*
                    * 商品信息
                    * */
                    UdeskCommodityItem item=new UdeskCommodityItem();
                    item.setTitle(Title);
                    item.setSubTitle(SubTitle);
                    item.setThumbHttpUrl(ThumbHttpUrl);
                    item.setCommodityUrl(CommodityUrl);
                    UdeskSDKManager.getInstance().setCommodity(item);
                }else{
                    UdeskSDKManager.getInstance().setCommodity(null);
                }
            }else{
                return false;
            }
            return true;
        }
        return false;
    }

    public void OnNewMsgNotice(MsgNotice msgNotice) {
        if (msgNotice==null){
            return;
        }
        String notify_service=cordova.getActivity().NOTIFICATION_SERVICE;
        NotificationManager notificationManager= (NotificationManager) cordova.getActivity().getSystemService(notify_service);
        int icon = R.mipmap.icon;
        CharSequence tickerText = "您有新消息了";
        long when = System.currentTimeMillis();
        CharSequence contentTitle = "客服消息";
        CharSequence contentText = msgNotice.getContent();
        Intent notificationIntent = null;
        notificationIntent = new Intent(cordova.getActivity(), UdeskChatActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(cordova.getActivity(), 0,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(cordova.getActivity());
        Notification noti = builder.setSmallIcon(icon)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setTicker(tickerText)
            .setContentIntent(contentIntent)
            .setWhen(when).build();
        noti.flags = Notification.FLAG_AUTO_CANCEL;
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        noti.defaults |= Notification.DEFAULT_LIGHTS;
        noti.defaults = Notification.DEFAULT_SOUND;
        msg= (String) contentText;

        notificationManager.notify(1, noti);
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
