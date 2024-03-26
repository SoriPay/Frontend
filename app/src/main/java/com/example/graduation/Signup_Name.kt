package com.example.graduation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.TextUtils.isEmpty
import android.widget.Toast
import com.example.graduation.databinding.ActivitySignupNameBinding
import com.example.graduation.model.User
import com.example.graduation.retrofit.RetrofitService
import com.example.graduation.retrofit.UserApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.util.logging.Level
import java.util.logging.Logger

class Signup_Name : AppCompatActivity(), SignupDialogInterface {
    private lateinit var binding: ActivitySignupNameBinding
    lateinit var mtts: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // SharedPreferences에서 소리 on/off 상태 불러오기
        val sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)
        val soundState = sharedPreferences.getBoolean("soundState", false)

        mtts = TextToSpeech(this) { //모든 글자를 소리로 읽어주는 tts
            mtts.language = Locale.KOREAN //언어:한국어
        }

        //화면 정보 읽기
        if (soundState) {
            onSpeech("회원가입 이름 입력 화면입니다.")
        }

        val retrofitService = RetrofitService()
        val userApi: UserApi = retrofitService.retrofit.create(UserApi::class.java)

        binding.enterButton.setOnClickListener {
            val name = binding.signupInputName.text.toString().trim()
            if (isEmpty(name)) {
                //이름 공백 시 다이얼로그 생성
                val dialog = SignupDialog(this, "이름을 입력해주세요.")
                dialog.isCancelable = false
                dialog.show(this.supportFragmentManager, "SignupDialog")
                if (soundState) {
                    onSpeech("이름을 입력해주세요.")
                }
            } else {
                //TODO:0325 이름을 checkpwd로 보내버리기
                val sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("name", name)
                editor.apply()

                val intent1 = Intent(this, Signup_Email::class.java)
                val intent2 = Intent(this, Signup_Checkpwd::class.java)
                intent2.putExtra("name", name) //이름 값 전달
                startActivity(intent1)
            }
        }
    }

    override fun onDialogButtonClick() {
        finish()
    }

    //음성 안내
    private fun onSpeech(text: CharSequence) {
        mtts.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
    }
}