package com.example.xposed_sms;
/**
 * @author Li Jiansong
 * @date:2015-7-15  ����6:28:23
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
 * @date:2015-5-8  ����3:41:57
 * @version :
 *
 *������
 *���Կ���������com.android.mms.transaction.SmsSingleRecipientSender��
 *SmsSingleRecipientSender.sendMessage()����
 *�����乹�췽���������в�ͨ
 *���в��ԣ����WorkingMessage.send()�����ļ��ܲ��ֽ��н���
 *
 *SmsSingleRecipientSender.java���̳���SmsMessageSender��
 *�����һ�������ˣ�����Frameworks��ӿڷ�����Ϣ������MmsӦ����˵�����Ƿ��Ͷ��ŵ����һվ��
 *�Ծ���˵����Ӧ����˵������Ѷ��ŷ��ͳ�ȥ��
 *
 *˼·2��
 *���Կ�������SmsMessageSender���mMessageText����
 *
 */

/**
 * @author Li Jiansong
 * @date:2015-5-9  ����8:39:33
 * @version :
 *
 */
public class ClientHooker implements IXposedHookLoadPackage{

	private static final String PNAME="com.android.mms";
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		// TODO Auto-generated method stub
		if(!lpparam.packageName.equals(PNAME)){
			//XposedBridge.log("�޷��ҵ�"+PNAME);
			return ;
		}
		XposedBridge.log("----------Ŀǰ�ڰ�com.android.mms.data��------------");
		final Class<?> clazz=XposedHelpers.findClass(
				"com.android.mms.data.WorkingMessage", lpparam.classLoader);
		XposedHelpers.findAndHookMethod(clazz, "send", String.class,
				new XC_MethodHook(){
			@Override
			protected void beforeHookedMethod(MethodHookParam param)
					throws Throwable {
				// TODO Auto-generated method stub
				XposedBridge.log("----��ʼ����send����-------");
				Field f=XposedHelpers.findField(clazz, "mText");
				SpannableStringBuilder text=(SpannableStringBuilder) f.get(param.thisObject);
				String origMsg=text.toString();
				
				//�򵥼�������
				char array[]=origMsg.toCharArray();//��ȡ�ַ�����
				for(int i=0;i<array.length;i++){
					array[i]=(char) (array[i]^20000);//��ÿ������Ԫ�ؽ����������
				}
				String secretMsg=new String(array);
				//f.set(param.thisObject,"ԭʼ�������ݣ�"+origMsg+"\n"+"���ܺ����ݣ�"+secretMsg);
				f.set(param.thisObject, secretMsg);
				XposedBridge.log("------�ɹ�����send���������м���------");
			}
		});
	}
}