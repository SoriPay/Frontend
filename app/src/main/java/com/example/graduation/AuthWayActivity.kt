package com.example.graduation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.graduation.databinding.ActivityAuthWayBinding
import java.util.Locale

class AuthWayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthWayBinding
    lateinit var mtts:TextToSpeech



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthWayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences에서 소리 on/off 상태 불러오기
        val sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)
        val soundState = sharedPreferences.getBoolean("soundState", false)

        mtts = TextToSpeech(this) { //모든 글자를 소리로 읽어주는 tts
            mtts.language = Locale.KOREAN //언어:한국어
        }

        //화면 정보 읽기
        mtts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // 화면 정보 읽어주기
                val textToSpeak ="인증 방법 선택 화면입니다. 지문 인증과 비밀번호 입력 중 하나를 선택해주세요. 화면 최상단에는 이전 화면으로 되돌아가기 버튼이, 그 밑에는 지문 인증 버튼과 비밀번호 입력 버튼이 있습니다."
                onSpeech(textToSpeak)
            } else {
                // 초기화가 실패한 경우
                Log.e("TTS", "TextToSpeech 초기화 실패")
            }
        }

        binding.prevBtn.setOnClickListener {
            if (soundState) {
                onSpeech(binding.prevBtn.text)
            }
            val intent = Intent(this,ChoosePayMethodActivity::class.java)
            startActivity(intent)
        }

        //지문 인식 버튼 이벤트 처리
        binding.fingerprintBtn.setOnClickListener {
            if (soundState) {
                onSpeech(binding.fingerprintBtn.text)
            }
            val intent = Intent(this,PayFingerActivity::class.java)
            startActivity(intent)
        }


        //비밀번호 입력 버튼 이벤트 처리
        binding.pwdBtn.setOnClickListener {
            if (soundState) {
                onSpeech(binding.pwdBtn.text)
            }
            val intent = Intent(this,PayPasswordActivity::class.java)
            startActivity(intent)
        }

    }
    private fun onSpeech(text: CharSequence) {
        mtts.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
    }
    override fun onDestroy() {
        super.onDestroy()
        mtts.shutdown()

    }

}