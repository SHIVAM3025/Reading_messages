package com.example.readingmessages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SMSReceiver extends BroadcastReceiver {


    private static final String TAG = "SmsBroadcastReceiver";
    OTPReceiverInterface otpReceiveInterface = null;

    public void setOnOtpListeners(OTPReceiverInterface otpReceiveInterface) {
        this.otpReceiveInterface = otpReceiveInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction()))
        {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents'
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    if (otpReceiveInterface != null) {
                        String otp = message.replace("<#> Your OTP is: ", "");
                        int i = 0;
                        String x = "";
                        while(otp.charAt(i) != ' ') x += otp.charAt(i++);
                        otpReceiveInterface.onOtpReceived(x);
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    if (otpReceiveInterface != null) {
                        otpReceiveInterface.onOtpTimeout();
                    }
                    break;
            }
        }
    }
}
