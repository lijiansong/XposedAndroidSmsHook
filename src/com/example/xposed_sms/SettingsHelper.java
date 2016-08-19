package com.example.xposed_sms;
/**
 * @author Li Jiansong
 * @date:2015-7-15  ÏÂÎç6:32:38
 * @version :
 *
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import de.robv.android.xposed.XSharedPreferences;


public class SettingsHelper {
	private SharedPreferences mPreferences=null;
	private XSharedPreferences mXPreferences=null;
	
	public SettingsHelper(){
		mXPreferences=new XSharedPreferences("com.example.xposed_sms");
		mXPreferences.makeWorldReadable();
		this.reload();
	}
	
	public void reload() {
		// TODO Auto-generated method stub
		if(mXPreferences!=null){
			mXPreferences.reload();//Reload the settings from file if they have changed.
		}
	}

	public SettingsHelper(Context context){
		this.mPreferences=context.getSharedPreferences("preferences", 1);
	}//makeWorldReadable
	
	public String getString(String key, String defValue){
		if(mPreferences!=null){
			return mPreferences.getString(key, defValue);
		}
		else if(mXPreferences!=null){
			return mXPreferences.getString(key, defValue);
		}
		return defValue;
	}
	
	public int getInt(String key,int defValue){
		if(mPreferences!=null){
			return mPreferences.getInt(key, defValue);
		}else if(mXPreferences!=null){
			return mXPreferences.getInt(key, defValue);
		}
		return defValue;
	}
	
	public boolean getBoolean(String key,boolean defValue){
		if(mPreferences!=null){
			return mPreferences.getBoolean(key, defValue);
		}
		else if(mXPreferences!=null){
			return mXPreferences.getBoolean(key, defValue);
		}
		return defValue;
	}
	
	public void setString(String key, String value){
		Editor editor=null;
		if(mPreferences!=null){
			editor=mPreferences.edit();
		}else if(mXPreferences!=null){
			editor=mXPreferences.edit();
		}
		if(editor!=null){
			editor.putString(key, value);
			editor.commit();
		}
	}
	
	public void setBoolean(String key, boolean value) {
        Editor editor = null;
        if (mPreferences != null) {
            editor = mPreferences.edit();
        } else if (mXPreferences != null) {
            editor = mXPreferences.edit();
        }

        if (editor != null) {
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    public void setInt(String key, int value) {
        Editor editor = null;
        if (mPreferences != null) {
            editor = mPreferences.edit();
        } else if (mXPreferences != null) {
            editor = mXPreferences.edit();
        }

        if (editor != null) {
            editor.putInt(key, value);
            editor.commit();
        }
    }
}
