package cordova.plugin.UdeskPlugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;


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
            String obj[] = new String[11];
            String sdktoken,NICK_NAME,CELLPHONE,DESCRIPTION,CustomerUrl,Title,SubTitle,ThumbHttpUrl,CommodityUrl,agentId,groupId;
            if(args!=null){
                for (int i=0;i<args.length();i++){
                    if(args.get(i)!=null){
                        obj[i]=args.get(i).toString();
                    }else{
                        obj[i]=null;
                    }
                    
                }
                sdktoken=obj[0].toString();//token
                NICK_NAME=obj[1].toString();//师傅姓名
                CELLPHONE=obj[2].toString();//师傅电话
                DESCRIPTION=obj[3].toString();//描述信息
                CustomerUrl=obj[4].toString();//师傅头像地址
            }else{
                return false;
            }
            if(obj[5]!=null&&obj[5]!="null"){
                Title=obj[5].toString();//商品标题
                SubTitle=obj[6].toString();//红色字体，可以填写价格
                ThumbHttpUrl=obj[7].toString();//商品图片
                CommodityUrl=obj[8].toString();//商品链接
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
            /**
            客户信息初始化
            */
            UdeskSDKManager.getInstance().initApiKey(cordova.getActivity(),"1ziton.udesk.cn","b0b212dd796bb8d85a37a13dd95b63e4","71ad3a7b85ce98a3");
            Map<String,String> info=new HashMap<String,String>();
            info.put(UdeskConst.UdeskUserInfo.USER_SDK_TOKEN,sdktoken);
            info.put(UdeskConst.UdeskUserInfo.NICK_NAME,NICK_NAME);
            //  info.put(UdeskConst.UdeskUserInfo.EMAIL,"IT测试");
            info.put(UdeskConst.UdeskUserInfo.CELLPHONE,CELLPHONE);
            info.put(UdeskConst.UdeskUserInfo.DESCRIPTION,DESCRIPTION);
            UdeskSDKManager.getInstance().setUserInfo(cordova.getActivity(),sdktoken,info);
            //  设置用户头像
            UdeskSDKManager.getInstance().setCustomerUrl(CustomerUrl);

            /*
            * 指定客服ID
            * */
            if(obj[9]!=null){
                agentId=obj[9].toString();//客服ID
                UdeskSDKManager.getInstance().lanuchChatByAgentId(cordova.getActivity(),agentId);
            }

            /**
            指定客服组ID
            */
            if(obj[10]!=null){
                groupId=obj[10].toString();//客服组ID
                UdeskSDKManager.getInstance().lanuchChatByGroupId(cordova.getActivity(),groupId);
            }

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

            // 打开客服
            UdeskSDKManager.getInstance().entryChat(cordova.getActivity());

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
