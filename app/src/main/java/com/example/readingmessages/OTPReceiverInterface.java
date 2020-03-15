package com.example.readingmessages;

public interface OTPReceiverInterface {

    void onOtpReceived(String otp);
    void onOtpTimeout();
}
