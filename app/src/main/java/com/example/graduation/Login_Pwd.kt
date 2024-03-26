package com.example.graduation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.graduation.databinding.ActivityLoginPwdBinding
import com.example.graduation.model.User
import com.example.graduation.retrofit.RetrofitService
import com.example.graduation.retrofit.UserApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.util.*

class Login_Pwd : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPwdBinding
    lateinit var mtts:TextToSpeech
    // SharedPreferences에서 소리 on/off 상태 불러오기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPwdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // SharedPreferences에서 소리 on/off 상태 불러오기
        val sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)
        val soundState = sharedPreferences.getBoolean("soundState", false)

        mtts = TextToSpeech(this) { //모든 글자를 소리로 읽어주는 tts
            mtts.language = Locale.KOREAN //언어:한국어
        }

        val retrofitService = RetrofitService()
        val userApi: UserApi = retrofitService.retrofit.create(UserApi::class.java)
        //화면 정보 읽기

        //화면 정보 읽기
        if (soundState) {
            onSpeech("로그인 비밀번호 입력 화면입니다.")
        }

        binding.enter.setOnClickListener {
            val password = binding.loginInputPwd.text.toString()
            val id = intent.getStringExtra("id")
            if (password == "") {
                //비밀번호 공백 입력 시
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                if (soundState) {
                    onSpeech("비밀번호를 입력해주세요.")
                }
            }
            else {

               // val userApi = retrofit.create(UserApi::class.java)
                //val call: Call<User> = userApi.getUser(id, password)
                userApi.getUser(id,password).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        val user: User? = response.body()
                        if (user != null) {
                            val intent = Intent(applicationContext, Login::class.java)
                           // intent.putExtra("user", user)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, "회원가입을 해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(applicationContext, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                })
                //입력한 비밀번호를 DB에 저장된 비밀번호와 비교
                /*val dbPassword = TODO("입력한 email에 해당하는 사용자의 DB 경로.get(password).toString()")
                if (pwd == dbPassword) {
                    TODO("로그인 로직")
                }
                else {
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()

                 */



                    if (soundState) {
                        onSpeech("비밀번호가 일치하지 않습니다.")
                    } }
            }
        }

        //비밀번호 찾기 화면 연결
        /* binding.resetPwd.setOnClickListener {
            val intent = Intent("비밀번호 찾기 화면"::class.java)
            startActivity(intent)
            if (soundState) {
                    onSpeech("비밀번호 찾기")
                }
        }*/

    //음성 안내
    private fun onSpeech(text: CharSequence) {
        mtts.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
    }
}
