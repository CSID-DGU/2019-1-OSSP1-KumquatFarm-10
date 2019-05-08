package com.kakao.sdk.newtone.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.kakao.sdk.newtoneapi.SpeechRecognizeListener;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.impl.util.PermissionUtils;
import java.util.ArrayList;

/**
 * 본 sample의 main activity.
 *
 * 직접 음성인식 기능을 제어하는 API를 호출하는 버튼과 기본으로 제공되는 UI를 통해 음성인식을 수행하는
 * 두가지 형태를 제공한다.
 *
 * 음성인식 API의 callback을 받기 위해 {@link com.kakao.sdk.newtoneapi.SpeechRecognizeListener} interface를 구현하였다.
 *
 * @author Daum Communications Corp.
 * @since 2013
 */
public class SpeechSampleActivity extends Activity implements View.OnClickListener, SpeechRecognizeListener {
    private SpeechRecognizerClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SpeechRecognizerManager.getInstance().initializeLibrary(this);
        findViewById(R.id.speechbutton).setOnClickListener(this);
        setButtonsStatus(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // API를 더이상 사용하지 않을 때 finalizeLibrary()를 호출한다.
        SpeechRecognizerManager.getInstance().finalizeLibrary();
    }

    private void setButtonsStatus(boolean enabled) {
        findViewById(R.id.speechbutton).setEnabled(enabled);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String serviceType = SpeechRecognizerClient.SERVICE_TYPE_WEB;
        Log.i("SpeechSampleActivity", "serviceType : " + serviceType);
        // 음성인식 버튼 listener
        if (id == R.id.speechbutton) {
            if(PermissionUtils.checkAudioRecordPermission(this)) {
                SpeechRecognizerClient.Builder builder = new SpeechRecognizerClient.Builder().
                        setServiceType(serviceType);
                client = builder.build();
                client.setSpeechRecognizeListener(this);
                client.startRecording(true);
                setButtonsStatus(false);
            }
        }
    }

    @Override
    public void onReady() {
        //TODO implement interface DaumSpeechRecognizeListener method
    }
    @Override
    public void onBeginningOfSpeech() {
        //TODO implement interface DaumSpeechRecognizeListener method
    }
    @Override
    public void onEndOfSpeech() {
        //TODO implement interface DaumSpeechRecognizeListener method
    }
    @Override
    public void onError(int errorCode, String errorMsg) {
        //TODO implement interface DaumSpeechRecognizeListener method
		Log.e("SpeechSampleActivity", "onError");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setButtonsStatus(true);
            }
        });
        client = null;
    }

    @Override
    public void onPartialResult(String text) {
        //TODO implement interface DaumSpeechRecognizeListener method
    }
    @Override
    public void onResults(Bundle results) {
        final StringBuilder builder = new StringBuilder();
		Log.i("SpeechSampleActivity", "onResults");
        ArrayList<String> texts = results.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);
        ArrayList<Integer> confs = results.getIntegerArrayList(SpeechRecognizerClient.KEY_CONFIDENCE_VALUES);
        builder.append(texts.get(0));
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // finishing일때는 처리하지 않는다.
                if (activity.isFinishing()) return;
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity).
                        setMessage(builder.toString()).
                        setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();
                setButtonsStatus(true);
            }
        });
        client = null;
    }

    @Override
    public void onAudioLevel(float v) {
        //TODO implement interface DaumSpeechRecognizeListener method
    }

	@Override
	public void onFinished() {
		Log.i("SpeechSampleActivity", "onFinished");
	}
}
