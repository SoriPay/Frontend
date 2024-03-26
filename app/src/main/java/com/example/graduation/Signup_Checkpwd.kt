package com.example.graduation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.example.graduation.LoginUser.Companion.email
import com.example.graduation.databinding.ActivitySignupCheckpwdBinding
import com.example.graduation.model.User
import com.example.graduation.retrofit.RetrofitService
import com.example.graduation.retrofit.UserApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.util.logging.Level
import java.util.logging.Logger

class Signup_Checkpwd : AppCompatActivity(), SignupDialogInterface {

    private lateinit var binding: ActivitySignupCheckpwdBinding
    lateinit var mtts: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupCheckpwdBinding.inflate(layoutInflater)
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
        if (soundState) {
            onSpeech("회원가입 이메일 입력 화면입니다.")
        }

        //화면 정보 읽기
        if (soundState) {
            onSpeech("회원가입 이메일 입력 화면입니다.")
        }

        //TODO:0325 해당 회원의 이메일과 이름 가져오기
        val sharedPreferences2 = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val id = sharedPreferences2.getString("id", "")
        val name = sharedPreferences2.getString("name", "")
        binding.emailTv.text=id //해당 회원의 이메일을 가져와서 이메일 텍스트뷰에 반영
        binding.nameTv.text=name //해당 회원의 이름을 가져와서 이메일 텍스트뷰에 반영


        binding.enterButton.setOnClickListener {


            val password = intent.getStringExtra("password").toString()
            val checkpwd = binding.signupInputCheckpwd.text.toString().trim()

            val user = User()
            user.id = id
            user.password = password
            user.name = name

            if (isPwdIdentified(checkpwd, password)) {
                //비밀번호 일치하면 계정 생성 후 로그인 화면으로 이동

                userApi.save(user)
                    .enqueue(object : Callback<User> {
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            Log.d("Signup_Checkpwd", "User saved successfully: ${response.body()}")
                            Toast.makeText(this@Signup_Checkpwd, "ok!!!", Toast.LENGTH_SHORT).show()

                        }


                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Toast.makeText(this@Signup_Checkpwd, "fail!!!", Toast.LENGTH_SHORT).show()
                            Logger.getLogger(Signup_Checkpwd::class.java.name).log(Level.SEVERE, "에러")
                        }
                    })

                val intent = Intent(this, Login::class.java)
                startActivity(intent)


            } else {
                //비밀번호 불일치 시
                run {
                    val dialog = SignupDialog(this, "비밀번호가 일치하지않습니다.")
                    dialog.isCancelable = false
                    dialog.show(this.supportFragmentManager, "SignupDialog")
                    if (soundState) {
                        onSpeech("비밀번호가 일치하지않습니다.")
                    }
                }
            }
        }
    }

    //비밀번호 일치 검사
    private fun isPwdIdentified(checkpwd: String, pwd: String) : Boolean {
        return pwd == checkpwd
    }

    //계정 생성
    private fun makeUser(name: String, email: String, pwd: String): Boolean {
        return TODO("서버에 입력받은 email, pwd로 사용자 계정 생성")
    }

    override fun onDialogButtonClick() {
        finish()
    }

    //음성 안내
    private fun onSpeech(text: CharSequence) {
        mtts.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
    }

}