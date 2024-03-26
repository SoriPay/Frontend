package com.example.graduation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.graduation.databinding.ActivityLoginBinding
import java.util.Locale
import java.util.concurrent.Executor


class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    lateinit var mtts:TextToSpeech
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // SharedPreferences에서 소리 on/off 상태 불러오기
        val sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)
        val soundState = sharedPreferences.getBoolean("soundState", false)

        mtts = TextToSpeech(this) { //모든 글자를 소리로 읽어주는 tts
            mtts.language = Locale.KOREAN //언어:한국어
        }

        //화면 정보 읽기
        if (soundState) {
            onSpeech("로그인 화면입니다.")
        }

        //지문 인증
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "지문 로그인 에러", Toast.LENGTH_SHORT).show()
                    if (soundState) {
                        onSpeech("로그인 에러")
                    }

                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext, "로그인 성공", Toast.LENGTH_SHORT).show()
                    if (soundState) {
                        onSpeech("로그인 성공")
                    }
                    val intent=Intent(this@Login,MainActivity::class.java)
                    startActivity(intent)

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                    if (soundState) {
                        onSpeech("지문 로그인 실패")
                    }
                   /* val intent=Intent(this@Login,MainActivity::class.java)
                    startActivity(intent)*/
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("지문 인증")
            .setSubtitle("기기에 등록된 지문을 이용하여 지문을 인증해주세요.")
            .setNegativeButtonText("취소")
            .build()

        //지문 로그인 버튼 클릭 시 인식
        binding.FingerLogin.setOnClickListener {
            if (soundState) {
                onSpeech("지문 로그인입니다. 기기에 등록된 지문을 이용하여 인증해주세요.")
            }
            biometricPrompt.authenticate(promptInfo)
        }

        //비밀번호 로그인 화면 연결
        binding.PwdLogin.setOnClickListener {
            val intent = Intent(this, Login_Email::class.java)
            startActivity(intent)
            if (soundState) {
                onSpeech("비밀번호 로그인")
            }
        }

        //회원가입 화면 연결
        binding.SignUp.setOnClickListener {
            val intent = Intent(this, Signup_Name::class.java)
            startActivity(intent)
            if (soundState) {
                onSpeech("회원가입")
            }
        }
    }

    //음성 안내
    private fun onSpeech(text: CharSequence) {
        mtts.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
    }
}