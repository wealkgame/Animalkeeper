package wealk.android.animalkeeper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		TelephonyManager tm = 
		    (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
		GameMainActivity.tmpTelephonyManager = tm;
		// TODO Auto-generated method stub
		if( intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
			Log.v("0001", "new outgoing number"+this.getResultData());
		}else{
			   if( tm.getCallState() == TelephonyManager.CALL_STATE_RINGING){
				   GameMainActivity.tmpGameStart = GameMainActivity.mGameStart;
				   GameMainActivity.mGameStart = 7;
			   }else{

			   }

		}
	}	

}
