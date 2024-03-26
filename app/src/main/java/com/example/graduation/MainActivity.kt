package com.example.graduation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.example.graduation.myInfo.MyInfoActivity
import com.example.graduation.databinding.ActivityMainBinding
import com.example.graduation.managePay.EditPayActivity
import com.example.graduation.transfer.TransferChooseBankActivity
import com.example.graduation.transfer.TransferHistoryActivity

import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var mtts:TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences에서 소리 on/off 상태 불러오기
        val sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)
        val soundState = sharedPreferences.getBoolean("soundState", false)

        mtts = TextToSpeech(this) { //모든 글자를 소리로 읽어주는 tts
            mtts.language = Locale.KOREAN //언어:한국어
        }


        //화면 정보 읽기
        if (soundState) {
            onSpeech("소리페이 메인 화면입니다.")
        }

        //TODO:0325 해당 회원의 이메일과 이름 가져오기
        val sharedPreferences2 = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val name = sharedPreferences2.getString("name", "")
        binding.nameTv.text=name //해당 회원의 이름을 가져와서 이메일 텍스트뷰에 반영

        //결제하기 버튼
        binding.payBtn.setOnClickListener{
            if (soundState) {
                onSpeech(binding.payBtn.text)
            }
            val intent = Intent(this, CheckPayInfoActivity::class.java)
            startActivity(intent)
        }

        //송금 버튼
        binding.transferBtn.setOnClickListener{
            if (soundState) {
                onSpeech(binding.transferBtn.text)
            }
            val intent = Intent(this, TransferChooseBankActivity::class.java)
            startActivity(intent)

        }


        //결제수단 관리 버튼
        binding.editPayBtn.setOnClickListener{
            if (soundState) {
                onSpeech(binding.editPayBtn.text)
            }
            val intent = Intent(this, EditPayActivity::class.java)
            startActivity(intent)
        }

        //정보수정 버튼
        binding.editInfoBtn.setOnClickListener{
            onSpeech(binding.editInfoBtn.text)
            val intent = Intent(this, MyInfoActivity::class.java)
            startActivity(intent)
        }

        //결제 내역 확인 버튼 이벤트 처리
        binding.paymentHistoryBtn.setOnClickListener{
            if (soundState) {
                onSpeech(binding.paymentHistoryBtn.text)
            }
            val intent = Intent(this, PayHistoryActivity::class.java)
            startActivity(intent)
        }

        //송금 내역 확인 버튼 이벤트 처리
        binding.transferHistoryBtn.setOnClickListener{
            if (soundState) {
                onSpeech(binding.transferHistoryBtn.text)
            }
            val intent = Intent(this, TransferHistoryActivity::class.java)
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
