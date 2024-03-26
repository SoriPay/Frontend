package com.example.graduation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.graduation.databinding.ActivityMainBinding
import com.example.graduation.databinding.ActivityPayConfirmationBinding
import java.util.Locale

class PayConfirmationActivity : AppCompatActivity() {

    lateinit var mtts:TextToSpeech
    private lateinit var binding: ActivityPayConfirmationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityPayConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences에서 소리 on/off 상태 불러오기
        val sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)
        val soundState = sharedPreferences.getBoolean("soundState", false)

        //결제해야할 상품 정보 가져와서 텍스트뷰에 반영
        val sharedPreferences2 = getSharedPreferences("sp2", Context.MODE_PRIVATE)
        val storeName = sharedPreferences2.getString("storeName", "") ?: ""
 /*       val productName = sharedPreferences2.getString("productName", "") ?: ""*/
        val price = sharedPreferences2.getInt("price", 0)
        binding.productPlaceTv.text = storeName
        binding.productPriceTv.text = price.toString()

        //선택된 은행계좌와 계좌번호 가져와서 텍스트뷰에 반영
        val sharedPreferences3 = getSharedPreferences("sp3", Context.MODE_PRIVATE)
        val selectedAccountName = sharedPreferences3.getString("selectedAccountName", "")
        val selectedAccountNumber = sharedPreferences3.getString("selectedAccountNumber", "")
        binding.chosenPaymentNameText.text =selectedAccountName
        binding.chosenPaymentNumberText.text=selectedAccountNumber

        //소리 설정
        mtts = TextToSpeech(this) { //모든 글자를 소리로 읽어주는 tts
            mtts.language = Locale.KOREAN //언어:한국어
        }

        mtts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // 화면 정보 읽어주기
                val textToSpeak ="결제 승인하시겠습니까?"+binding.productPlaceTv.text+"에서"+price+"원을 결제합니다. 출금계좌는"+binding.chosenPaymentNameText.text+"의"+binding.chosenPaymentNumberText.text+"입니다."
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

            val intent = Intent(this,PayPasswordActivity::class.java)
            startActivity(intent)
        }

        //TODO:결제승인 과정에서 오류시 else 처리
        //결제승인 버튼 이벤트 처리
        binding.approveBtn.setOnClickListener {

            if (soundState) {
                onSpeech(binding.approveBtn.text)
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fl_basic, PayCompletedFragment())
                .commit()


        }

        //결제 승인했으니 결제 내역을 담은 PayHistoryActivity로 데이터를 보내야함
        val spPay = getSharedPreferences("PayInfo", Context.MODE_PRIVATE)
        val editor = spPay.edit()
        editor.putString("storeName", storeName)
        editor.putInt("price", price)
        editor.apply()
    }
    private fun onSpeech(text: CharSequence) {
        mtts.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
    }
    override fun onDestroy() {
        super.onDestroy()
        mtts.shutdown()

    }
}