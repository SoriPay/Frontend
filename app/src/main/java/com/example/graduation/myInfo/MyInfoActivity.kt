package com.example.graduation.myInfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.graduation.MainActivity
import com.example.graduation.R
import com.example.graduation.TermsActivity
import com.example.graduation.databinding.ActivityMyInfoBinding
import java.util.Locale


class MyInfoActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMyInfoBinding
    lateinit var mtts: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyInfoBinding.inflate(layoutInflater)
        mtts = TextToSpeech(this) { //모든 글자를 소리로 읽어주는 tts
            mtts.language = Locale.KOREAN //언어:한국어
        }

        // SharedPreferences에서 소리 on/off 상태 불러오기
        val sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)
        val soundState = sharedPreferences.getBoolean("soundState", false)

        Log.d("ppa", soundState.toString())


        binding.prevBtn.setOnClickListener{
            if (soundState) {
                onSpeech(binding.prevBtn.text)
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }



        //비밀번호 변경 버튼 이벤트 처리
        binding.changePwdBtn.setOnClickListener{
            if (soundState) {
                onSpeech(binding.changePwdBtn.text)
            }
            val intent = Intent(this, ChangePwdActivity::class.java)
            startActivity(intent)
        }

        //이용약관 버튼 이벤트 처리
        binding.termsBtn.setOnClickListener{
            if (soundState) {
                onSpeech(binding.termsBtn.text)
            }
            val intent = Intent(this, TermsActivity::class.java)
            startActivity(intent)
        }

        //계정탈퇴 이벤트처리
        binding.deleteAccountBtn.setOnClickListener{
            if (soundState) {
                onSpeech(binding.deleteAccountBtn.text)
            }

            val fragment = DeleteAccountConfirmationFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fl_basic, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        setContentView(binding.root)

    }


    private fun onSpeech(text: CharSequence) {
        mtts.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
    }


}