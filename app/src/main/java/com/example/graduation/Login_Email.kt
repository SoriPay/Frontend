package com.example.graduation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.graduation.LoginUser.Companion.email
import com.example.graduation.databinding.ActivityLoginEmailBinding
import com.example.graduation.model.User
import com.example.graduation.retrofit.RetrofitService
import com.example.graduation.retrofit.UserApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.Locale

class Login_Email : AppCompatActivity() {
    private lateinit var binding: ActivityLoginEmailBinding
    lateinit var mtts: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // SharedPreferences에서 소리 on/off 상태 불러오기
        val sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)
        val soundState = sharedPreferences.getBoolean("soundState", false)

        mtts = TextToSpeech(this) { //모든 글자를 소리로 읽어주는 tts
            mtts.language = Locale.KOREAN //언어:한국어
        }

        //화면 정보 읽기
        if (soundState) {
            onSpeech("로그인 이메일 입력 화면입니다.")
        }

        //  val retrofitService = RetrofitService()
        // val userApi = retrofitService.retrofit.create(UserApi::class.java)

        binding.enter.setOnClickListener {
            val id = binding.loginInputEmail.text.toString()

             if (isEmailValid(id)) {
                /*val retrofitService = RetrofitService()
                val userApi = retrofitService.retrofit.create(UserApi::class.java)
                userApi.checkEmailRegistration(id).enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.isSuccessful) {
                            val isRegistered = response.body() ?: false
                            if (isRegistered) {
                                val intent = Intent(this@Login_Email, Login_Pwd::class.java)
                                intent.putExtra("id", id) // 이메일 값 넘겨주기
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@Login_Email,
                                    "가입되지 않은 이메일입니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (soundState) {
                                    onSpeech("가입되지 않은 이메일입니다.")
                                }
                            }
                        }
                    }



                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        // 요청 실패 시 처리
                        Toast.makeText(this@Login_Email, "서버와의 통신에 실패하였습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                })


                 */

            } else {
                // 이메일 형식 오류일 경우
                Toast.makeText(this, "이메일을 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                if (soundState) {
                    onSpeech("이메일을 다시 확인해주세요.")
                }
            }
        }
    }

         /*
            if (isEmailValid(id)) {
                if (isEmailRegistered(id)) {
                    val intent = Intent(this, Login_Pwd::class.java)
                    intent.putExtra("id", id) //이메일 값 넘겨주기
                    startActivity(intent)
                } else {
                    //등록되지 않은 이메일인 경우
                    Toast.makeText(this, "가입되지 않은 이메일입니다.", Toast.LENGTH_SHORT).show()
                    if (soundState) {
                        onSpeech("가입되지 않은 이메일입니다.")
                    }
                }
                val user = User()
                user.setId(email)
                userApi.save(user).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@Login_Email, "로그인 성공", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(this@Login_Email, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                //이메일 형식 오류일 경우
                Toast.makeText(this, "이메일을 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                if (soundState) {
                    onSpeech("이메일을 다시 확인해주세요.")
                }
            }

        }
    }


          */

    //이메일 형식 검사
    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    //이메일 등록 여부 검사
    private fun isEmailRegistered(id: String): Boolean {
        // JDBC 연결
        val url = "jdbc:mysql://192.168.228.8:3306/userlog"
        val id = "parang"
        val password = "backend"
        // DB 연결
        //Class.forName("com.mysql.jdbc.Driver")
        var connection: Connection? = null
        var isRegistered = false
        try {
           // connection = DriverManager.getConnection("jdbc:mysql://192.168.219.102:3306/userlog","parang","backend")

             connection = DriverManager.getConnection(url, id, password)
            // SQL 쿼리를 이용해서 이메일이 DB에 존재하는지 확인
            val sql = "SELECT * FROM User WHERE id=?"
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, id)
            val resultSet = preparedStatement.executeQuery()
            //존재하지 않으면 isNotRegistered 값을 true로 설정
            if (resultSet.next()) {
                val count = resultSet.getInt(1)
                isRegistered = count > 0 //0보다 크면 등록 여부값 true
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            // 연결 닫기
            try {
                connection?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return isRegistered
    }



    //음성 안내
    private fun onSpeech(text: CharSequence) {
        mtts.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
    }

}


