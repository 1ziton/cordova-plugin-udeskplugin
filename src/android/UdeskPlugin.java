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

import android.util.Log;
import android.widget.Toast;
/**
 * This class echoes a string called from JavaScript.
 */
public class UdeskPlugin extends CordovaPlugin {

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

            /*
            * 指定客服ID
            * */
//            if(obj[9]!=null){
//                agentId=obj[9].toString();//客服ID
//                UdeskSDKManager.getInstance().lanuchChatByAgentId(cordova.getActivity(),agentId);
//            }
            /*
            *指定客服组ID
            */
//            if(obj[10]!=null){
//                groupId=obj[10].toString();//客服组ID
//                UdeskSDKManager.getInstance().lanuchChatByGroupId(cordova.getActivity(),groupId);
//            }
            /*
            * 获取未读消息数
            * */
            int num=UdeskSDKManager.getInstance().getCurrentConnectUnReadMsgCount();
            /*
            * 删除客户聊天数据
            * */
            // UdeskSDKManager.getInstance().deleteMsg();
            /*
            * 断开与Udesk服务器连接
            * */
            // UdeskSDKManager.getInstance().disConnectXmpp();

            // PluginResult result=new PluginResult(PluginResult.Status.OK,num);
            // result.setKeepCallback(true);
            // callbackContext.sendPluginResult(result);
            // 帮助中心
            // UdeskSDKManager.getInstance().toLanuchHelperAcitivty(getBaseContext());

            callbackContext.success(num);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
