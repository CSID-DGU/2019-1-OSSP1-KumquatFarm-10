package com.kakao.sdk.newtone.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.kakao.sdk.newtoneapi.SpeechRecognizeListener;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.impl.util.PermissionUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class SpeechSampleActivity extends Activity implements View.OnClickListener, SpeechRecognizeListener {
    ListView m_ListView;
    CustomAdapter m_Adapter;
    private SpeechRecognizerClient client;
    private EditText mJsonText;
    private TextView mReceiveanswerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SpeechRecognizerManager.getInstance().initializeLibrary(this);
        findViewById(R.id.speechbutton).setOnClickListener(this);
        setButtonsStatus(true);
        m_Adapter = new CustomAdapter();
        m_ListView = (ListView) findViewById(R.id.listView1);
        m_ListView.setAdapter(m_Adapter);

        mJsonText = (EditText) findViewById(R.id.editText1);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = sdf.format(date);
        m_Adapter.add(getTime,2);

        m_Adapter.add("안녕하세요. 동국대 자동응답 챗봇입니다.",1);
        m_Adapter.add("음성인식 기능은 마이크 버튼을 터치하시면 사용 가능합니다.",1);
        m_Adapter.add("말을 안하시면 음성인식 기능이 자동 중단됩니다.",1);
        m_Adapter.add("챗봇을 사용해서 질문인식이 제대로 되지 않을 수 있으므로 정확한 단어로 질문해주세요.",1);
        m_Adapter.add("동국대에 궁금한 것을 물어보세요.",1);

        Button buttonsend = (Button) findViewById(R.id.button1);
        buttonsend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text1 = (EditText) findViewById(R.id.editText1);
                String s1 = text1.getText().toString();
                sendObject();
                m_Adapter.add(s1,0);
                m_Adapter.notifyDataSetChanged();
                ((EditText) findViewById(R.id.editText1)).setText(null);
                //Log.e("text",mReceiveanswerText.toString());
            }
        });
    }

    private void sendObject(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("question", mJsonText.getText().toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        receiveObject(jsonObject);
    }

    private void receiveObject(JSONObject data){
        try{
            mReceiveanswerText.setText(data.getString("answer"));
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
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
        Log.i("SpeechSampleActivity", "onResults");
        ArrayList<String> texts = results.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);
        ((EditText) findViewById(R.id.editText1)).setText(texts.get(0));
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (activity.isFinishing()) return;
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