package com.gap.pino.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.gap.pino.R;
import com.gap.pino.activity.message.ChatActivity;
import com.gap.pino.common.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordFragment extends DialogFragment {

    private Chronometer recordTimer;
    private MediaRecorder mediaRecorder;
    private ImageButton record_btn;

    private boolean isNotRecording = true;
    private String recordFile;

    private static final int MY_PERMISSIONS_REQUEST = 100;

    private String path_param = "";

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        recordTimer = view.findViewById(R.id.record_timer);
        record_btn = view.findViewById(R.id.record_btn);


        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission
                                    .WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST);
                }
            }

        }

        record_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale
                                (getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(
                                        new String[]{Manifest.permission
                                                .WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST);
                            }
                        }

                    } else {
                        if (isNotRecording && checkAudioPermission()) {
                            // Start rec
                            startRecording();
                            ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stop, null));
                            isNotRecording = false;
                        } else {
                            // Stop rec

                        }
                    }

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // stop your timer.
                    stopRecording();
                    ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.record_btn_start, null));
                    isNotRecording = true;

                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //@OnClick(R.id.record_btn)
    public void recordBtnClicked(View v) {

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission
                                    .WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST);
                }
            }

        } else {
            if (isNotRecording && checkAudioPermission()) {
                // Start rec
                startRecording();
                ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stop, null));
                isNotRecording = false;
            } else {
                // Stop rec
                stopRecording();
                ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.record_btn_start, null));
                isNotRecording = true;
            }
        }

    }

    private void startRecording() {
        recordTimer.setBase(SystemClock.elapsedRealtime());
        recordTimer.start();

        String path = Environment.getExternalStorageDirectory().toString() + "/BisInspection" + "/imagesss";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_mm_ss");
        Date now = new Date();

        recordFile = "Recording_" + formatter.format(now) + ".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(path + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        path_param = path + "/" + recordFile;


        System.out.println("path====" + path);
        System.out.println("recordFile====" + recordFile);
        System.out.println("///////====" + path + "/" + recordFile);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        mediaRecorder.start();
    }

    private void stopRecording() {
        recordTimer.stop();

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        ((ChatActivity)getActivity()).setDate(path_param);
        getDialog().dismiss();
    }

    private boolean checkAudioPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, Constants.RECORD_AUDIO_PERMISSION);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!isNotRecording)
            startRecording();
    }

}
