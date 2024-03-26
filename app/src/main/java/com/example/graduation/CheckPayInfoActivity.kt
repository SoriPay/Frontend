package com.example.graduation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.graduation.databinding.ActivityCheckPayInfoBinding
import java.util.Locale

class CheckPayInfoActivity : AppCompatActivity() {
    lateinit var mtts:TextToSpeech
    private lateinit var binding: ActivityCheckPayInfoBinding
    private var productDatas = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckPayInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 결제할 품목 더미데이터
        productDatas.apply {
            add(Product(1, "스타벅스",  4500))
            add(Product(2, "이디야",4000))
            add(Product(3, "도미노 피자", 20000))
            add(Product(4, "포라임",12000))
            add(Product(5, "두끼 떡볶이", 12000))
            add(Product(6, "베스킨라빈스", 4500))
            add(Product(7, "쉑쉑버거", 15000))
        }

        //결제정보 나타내기
        val product = productDatas[0]
        binding.productPlaceTv.text = product.storeName
        binding.productPriceTv.text = "${product.price.toString()} 원"


        mtts = TextToSpeech(this) { //모든 글자를 소리로 읽어주는 tts
            mtts.language = Locale.KOREAN //언어:한국어
        }

        mtts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // 초기화가 성공한 경우
                val storeName = productDatas[0].storeName
                val productPrice = productDatas[0].price

                // 화면 정보 읽어주기
                val textToSpeak = "$storeName 에서 $productPrice 원을 결제하려고 합니다."+ binding.explainTv.text
                onSpeech(textToSpeak)
            } else {
                // 초기화가 실패한 경우
                Log.e("TTS", "TextToSpeech 초기화 실패")
            }
        }

        // SharedPreferences에서 소리 on/off 상태 불러오기
        val sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)
        val soundState = sharedPreferences.getBoolean("soundState", false)


        binding.prevBtn.setOnClickListener{
            if (soundState) {
                onSpeech(binding.prevBtn.text)
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.nextBtn.setOnClickListener{
            if (soundState) {
                onSpeech(binding.nextBtn.text)
            }

            val intent = Intent(this, ChoosePayMethodActivity::class.java)

            //상품 정보 담기
            val product = productDatas[0]

            //SharedPrefrence에 데이터 저장하기
            val sharedPreferences = getSharedPreferences("sp2", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("storeName", product.storeName)
            editor.putInt("price", product.price)
            editor.apply()

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
