package com.example.xposed_sms;
/**
 * @author Li Jiansong
 * @date:2015-7-15  ����6:29:34
 * @version :
 *
 */
import java.lang.reflect.Field;

import android.util.Log;
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
public class ServerHooker implements IXposedHookLoadPackage{
	
	private static final String PACKAGE_MMS="com.android.mms";
	
	private static final String PACKAGE_TRANS="com.android.mms.transaction";
	private static final String CLASSNAME_SEND="com.android.mms.data.WorkingMessage";
	
	private static SettingsHelper mSettings = new SettingsHelper();
	
	private final String TAG="mms";
	
	public void log(String s){
		Log.d(TAG, s);
		XposedBridge.log(s);	
	}
	//private  static Myshare share=new Myshare();
  
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		// TODO Auto-generated method stub
		XposedBridge.log("Loaded app: "+lpparam.packageName);
		if(!lpparam.packageName.equals(PACKAGE_MMS))
			return ;
		XposedBridge.log("------------����hook com.android.mms");
		
		try {
//			final Class<?> sendClazz=XposedHelpers.findClass(CLASSNAME_SEND,
//					lpparam.classLoader);
			
			final Class<?> serverClazz=XposedHelpers.findClass(PACKAGE_TRANS+".SmsMessageSender",
					lpparam.classLoader);
			XposedHelpers.findAndHookMethod(serverClazz, "sendMessage", 
					long.class,
					new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(MethodHookParam param)
						throws Throwable {
					// TODO Auto-generated method stub
					//super.beforeHookedMethod(param);
					
					Field field=XposedHelpers.findField(serverClazz, "mMessageText");
					String messageText=(String) field.get(param.thisObject);
					String msgTxt=messageText.toString();
					
					mSettings.reload();
					XposedBridge.log("------------------flag:"+mSettings.getString("flag", "0"));
					if(mSettings.getString("flag", "0").equals("1")){
						//��������
						char []array1=msgTxt.toCharArray();//��ȡ�ַ�����
						for(int i=0;i<array1.length;i++){
							array1[i]=(char) (array1[i]^20000);//�ٴζ�ÿ������Ԫ�ؽ�������������
						}
						String msgText=new String(array1);
						field.set(param.thisObject, msgText);
						XposedBridge.log("------------------flag:"+mSettings.getString("flag", "0"));
						XposedBridge.log("---------����ʱ��Server�˽��ܳɹ�-----------");
					}
//					Field field=XposedHelpers.findField(sendClazz, "mText");
//					SpannableStringBuilder text=(SpannableStringBuilder) field.get(param.thisObject);
//					field.set(param.thisObject,msgText+"\n"+"test");
					
				}
				@Override
						protected void afterHookedMethod(MethodHookParam param)
								throws Throwable {
							// TODO Auto-generated method stub
							super.afterHookedMethod(param);
						}
				
			});
		} catch (Throwable t) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			XposedBridge.log(t);
		}
	}

}
