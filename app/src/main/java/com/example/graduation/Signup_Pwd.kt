package com.example.graduation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.example.graduation.databinding.ActivitySignupPwdBinding
import com.example.graduation.model.User
import com.example.graduation.retrofit.RetrofitService
import com.example.graduation.retrofit.UserApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.util.logging.Level
import java.util.logging.Logger

class Signup_Pwd : AppCompatActivity(), SignupDialogInterface {

    private lateinit var binding: ActivitySignupPwdBinding
    lateinit var mtts:TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupPwdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // SharedPreferences에서 소리 on/off 상태 불러오기
        val sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)
        val soundState = sharedPreferences.getBoolean("soundState", false)

        mtts = TextToSpeech(this) { //모든 글자를 소리로 읽어주는 tts
            mtts.language = Locale.KOREAN //언어:한국어
        }

        //화면 정보 읽기
        if (soundState) {
            onSpeech("회원가입 비밀번호 입력 화면입니다. 숫자와 영문이 포함된 8~16자리를 입력해주세요.")
        }

        binding.enterButton.setOnClickListener {
            val password = binding.signupInputPwd.text.toString().trim()

            if (isPwdQualified(password)){
                //비밀번호 조건 충족 시 저장 후 다음 화면으로 이동
                val intent = Intent(this, Signup_Checkpwd::class.java)
                intent.putExtra("password", password) //비밀번호 값 전달
                startActivity(intent)
            }
            else {
                //비밀번호 조건 미충족 시
                run {
                    val dialog = SignupDialog(this, "숫자, 영문이 포함된\n8~16자리를 입력해주세요.")
                    dialog.isCancelable = false
                    dialog.show(this.supportFragmentManager, "SignupDialog")
                    if (soundState) {
                        onSpeech("숫자와 영문이 포함된 8~16자리를 입력해주세요.")
                    }
                }
            }
        }
    }

    override fun onDialogButtonClick() {
        finish()
    }

    //비밀번호 조건 검사 (숫자, 영문 조합 8~16자)
    private fun isPwdQualified(pwd: String): Boolean {
        var hasLetter = false
        var hasDigit = false
        if (pwd.length < 8 || pwd.length > 16) { //8~16자 조건
            return false
        }
        else{
            pwd.forEach{//숫자, 영문 조합 조건
                if (it.isDigit())
                {
                    hasDigit = true
                }
                if (it.isLetter())
                {
                    hasLetter = true
                }
            }
        }
        return hasLetter && hasDigit
    }

    //음성 안내
    private fun onSpeech(text: CharSequence) {
        mtts.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
    }
}