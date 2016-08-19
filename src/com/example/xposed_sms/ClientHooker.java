package com.example.xposed_sms;
/**
 * @author Li Jiansong
 * @date:2015-7-15  下午6:28:23
 * @version :
 *
 */
import java.lang.reflect.Field;

import android.text.SpannableStringBuilder;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * @author Li Jiansong
 * @date:2015-5-8  下午3:41:57
 * @version :
 *
 *分析：
 *可以考虑拦截类com.android.mms.transaction.SmsSingleRecipientSender的
 *SmsSingleRecipientSender.sendMessage()方法
 *或者其构造方法经测试行不通
 *进行测试，针对WorkingMessage.send()方法的加密部分进行解密
 *
 *SmsSingleRecipientSender.java—继承自SmsMessageSender，
 *它针对一个收信人，调用Frameworks层接口发送信息，对于Mms应用来说，这是发送短信的最后一站，
 *对就是说对于应用来说，它会把短信发送出去。
 *
 *思路2：
 *可以考虑拦截SmsMessageSender类的mMessageText变量
 *
 */

/**
 * @author Li Jiansong
 * @date:2015-5-9  下午8:39:33
 * @version :
 *
 */
public class ClientHooker implements IXposedHookLoadPackage{

	private static final String PNAME="com.android.mms";
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		// TODO Auto-generated method stub
		if(!lpparam.packageName.equals(PNAME)){
			//XposedBridge.log("无法找到"+PNAME);
			return ;
		}
		XposedBridge.log("----------目前在包com.android.mms.data中------------");
		final Class<?> clazz=XposedHelpers.findClass(
				"com.android.mms.data.WorkingMessage", lpparam.classLoader);
		XposedHelpers.findAndHookMethod(clazz, "send", String.class,
				new XC_MethodHook(){
			@Override
			protected void beforeHookedMethod(MethodHookParam param)
					throws Throwable {
				// TODO Auto-generated method stub
				XposedBridge.log("----开始拦截send方法-------");
				Field f=XposedHelpers.findField(clazz, "mText");
				SpannableStringBuilder text=(SpannableStringBuilder) f.get(param.thisObject);
				String origMsg=text.toString();
				
				//简单加密运算
				char array[]=origMsg.toCharArray();//获取字符数组
				for(int i=0;i<array.length;i++){
					array[i]=(char) (array[i]^20000);//对每个数组元素进行异或运算
				}
				String secretMsg=new String(array);
				//f.set(param.thisObject,"原始短信内容："+origMsg+"\n"+"加密后内容："+secretMsg);
				f.set(param.thisObject, secretMsg);
				XposedBridge.log("------成功拦截send方法并进行加密------");
			}
		});
	}
}