package com.example.readingmessages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.readingmessages.OTPReceiverInterface;
import com.example.readingmessages.R;
import com.example.readingmessages.SMSReceiver;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        OTPReceiverInterface, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient myGoogleClient;
    EditText enterPhoneNumber;
    EditText enterOTP;
    Button getOTPBtn, verifyOtpBtn;
    int HINT_RESOLVE = 2;

    SMSReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getOTPBtn = findViewById(R.id.otpButton);
        enterOTP = findViewById(R.id.enterOtp);
        enterPhoneNumber = findViewById(R.id.enterMobile);
        verifyOtpBtn = findViewById(R.id.buttonVerify);

        smsReceiver = new SMSReceiver();

        myGoogleClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        smsReceiver.setOnOtpListeners(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getApplicationContext().registerReceiver(smsReceiver, intentFilter);

        getHintPhoneNumber();

        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        appSignatureHelper.getAppSignatures();

        getOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSMSListener();
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onOtpReceived(String otp) {
        enterOTP.setText(otp);
    }

    @Override
    public void onOtpTimeout() {
        Toast.makeText(this, "Time Out!!!", Toast.LENGTH_SHORT).show();
    }

    public void getHintPhoneNumber() {
        HintRequest hintRequest = new HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build();
        PendingIntent mIntent = Auth.CredentialsApi.getHintPickerIntent(myGoogleClient, hintRequest);
        try {
            startIntentSenderForResult(mIntent.getIntentSender(), HINT_RESOLVE, null, 0, 0, 0);
        }
        catch(IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(this);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {
                enterOTP.setVisibility(View.VISIBLE);
                verifyOtpBtn.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result if we want hint number
        if(requestCode == HINT_RESOLVE) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                enterPhoneNumber.setText(credential.getId());
            }
        }
    }



}
