package com.kakao.sdk.newtone.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.kakao.sdk.newtoneapi.SpeechRecognizerActivity;
import java.util.ArrayList;
import java.util.List;

public class VoiceRecoActivity extends SpeechRecognizerActivity {
    public static String EXTRA_KEY_RESULT_ARRAY = "result_array"; // 결과값 목록
    public static String EXTRA_KEY_MARKED = "marked"; // 첫번째 값의 신뢰도가 현저하게 높은 경우 true. 아니면 false. Boolean
    public static String EXTRA_KEY_ERROR_CODE = "error_code"; // 에러가 발생했을 때 코드값. 코드값은 SpeechRecognizerClient를 참조. Integer
    public static String EXTRA_KEY_ERROR_MESSAGE = "error_msg"; // 에러 메시지. String

    protected void putStringFromId(RES_STRINGS key, int id) {
        putString(key, getResources().getString(id));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // activity가 표시될 때의 transition 효과 설정
        overridePendingTransition(R.anim.com_kakao_sdk_asr_grow_height_from_top, android.R.anim.fade_in);

        // isValidResourceMappings()을 호출하면 리소스 및 view id 설정이 안된 것이 있는지 체크할 수 있다.
        boolean resourcePassed = isValidResourceMappings();
        Log.i("VoiceRecoActivity", "resource pass : " + resourcePassed);

        if (!resourcePassed) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
    }

    @Override
    public void finish() {
        super.finish();

        // activity가 사라질 때 transition 효과 지정
        overridePendingTransition(android.R.anim.fade_out, R.anim.com_kakao_sdk_asr_shrink_height_from_bottom);
    }

    @Override
    protected void onRecognitionSuccess(List<String> result, boolean marked) {
        // result는 선택된 결과 목록이 담겨있다.
        // 첫번째 값의 신뢰도가 낮아 후보 단어를 선택하는 과정을 거쳤을 경우에는 그 때 선택된 값이 가장 처음으로 오게 된다.
        // 첫번째 값의 신뢰도가 현저하게 높았거나, 이용자가 선택을 했을 경우에는 marked 값은 true가 된다. 이 이외에는 false가 된다.
        Intent intent = new Intent().
                putStringArrayListExtra(EXTRA_KEY_RESULT_ARRAY, new ArrayList<String>(result)).
                putExtra(EXTRA_KEY_MARKED, marked);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onRecognitionFailed(int errorCode, String errorMsg) {
        Intent intent = new Intent().
                putExtra(EXTRA_KEY_ERROR_CODE, errorCode).
                putExtra(EXTRA_KEY_ERROR_MESSAGE, errorMsg);

        setResult(RESULT_CANCELED, intent);
        finish();
    }
}