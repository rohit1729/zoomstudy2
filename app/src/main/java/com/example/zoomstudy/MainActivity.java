package com.example.zoomstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zoomstudy.initsdk.InitAuthSDKCallback;
import com.example.zoomstudy.initsdk.InitAuthSDKHelper;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingOptions;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;

public class MainActivity extends AppCompatActivity implements InitAuthSDKCallback, MeetingServiceListener {

    private final static String TAG = "ZoomStudyRoom";
    private static StartMeetingOptions meetingOptions = new StartMeetingOptions();
    private EditText numberEdit;
    private EditText nameEdit;
    private EditText passwordEdit;
    private ZoomSDK mZoomSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numberEdit = findViewById(R.id.edit_join_number);
        nameEdit = findViewById(R.id.edit_join_name);
        passwordEdit = findViewById(R.id.edit_join_password);
        InitAuthSDKHelper.getInstance().initSDK(this, this);
        mZoomSDK = ZoomSDK.getInstance();
        if (mZoomSDK.isLoggedIn()) {
            finish();
            return;
        }
    }

    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
        Log.i(TAG, "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);

        if (errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
            Toast.makeText(this, "Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode=" + internalErrorCode, Toast.LENGTH_LONG).show();
        } else {
            ZoomSDK.getInstance().getZoomUIService().enableMinimizeMeeting(false);
            ZoomSDK.getInstance().getMeetingSettingsHelper().enable720p(false);
            ZoomSDK.getInstance().getMeetingSettingsHelper().enableShowMyMeetingElapseTime(true);
            ZoomSDK.getInstance().getMeetingService().addListener(this);
            Toast.makeText(this, "Initialize Zoom SDK successfully.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onZoomAuthIdentityExpired() {
        Log.e(TAG,"onZoomAuthIdentityExpired");
    }

    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode, int internalErrorCode) {
        Log.d(TAG,"onMeetingStatusChanged "+meetingStatus+":"+errorCode+":"+internalErrorCode);

    }

    public void onClickJoin(View view) {
        if(!mZoomSDK.isInitialized())
        {
            Toast.makeText(this,"Init SDK First",Toast.LENGTH_SHORT).show();
            InitAuthSDKHelper.getInstance().initSDK(this, this);
            return;
        }
        String number = numberEdit.getText().toString();
        String name = nameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        if (password.isEmpty() || name.isEmpty() || number.isEmpty()){
            Toast.makeText(this, "Please enter the required fields", Toast.LENGTH_SHORT).show();
        }

        if (ZoomSDK.getInstance().getMeetingSettingsHelper().isCustomizedMeetingUIEnabled()) {
            ZoomSDK.getInstance().getSmsService().enableZoomAuthRealNameMeetingUIShown(false);
        } else {
            ZoomSDK.getInstance().getSmsService().enableZoomAuthRealNameMeetingUIShown(true);
        }

        JoinMeetingParams params = new JoinMeetingParams();
        params.meetingNo = number;
        params.displayName = name;
        params.password = password;
        JoinMeetingOptions options=new JoinMeetingOptions();
        ZoomSDK.getInstance().getMeetingService().joinMeetingWithParams(this, params,getJoinMeetingOptions());
    }

    public static JoinMeetingOptions getJoinMeetingOptions() {
        JoinMeetingOptions opts = new JoinMeetingOptions();
        fillMeetingOption(opts);
        opts.no_audio = meetingOptions.no_audio;
        return opts;
    }

    private static MeetingOptions fillMeetingOption(MeetingOptions opts)
    {
        opts.no_driving_mode = meetingOptions.no_driving_mode;
        opts.no_invite = meetingOptions.no_invite;
        opts.no_meeting_end_message = meetingOptions.no_meeting_end_message;
        opts.no_titlebar = meetingOptions.no_titlebar;
        opts.no_bottom_toolbar = meetingOptions.no_bottom_toolbar;
        opts.no_dial_in_via_phone = meetingOptions.no_dial_in_via_phone;
        opts.no_dial_out_to_phone = meetingOptions.no_dial_out_to_phone;
        opts.no_disconnect_audio = meetingOptions.no_disconnect_audio;
        opts.no_share = meetingOptions.no_share;
        opts.invite_options = meetingOptions.invite_options;
        opts.no_video = meetingOptions.no_video;
        opts.meeting_views_options = meetingOptions.meeting_views_options;
        opts.no_meeting_error_message = meetingOptions.no_meeting_error_message;
        opts.participant_id = meetingOptions.participant_id;
        return opts;
    }

}