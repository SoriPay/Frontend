package com.example.graduation

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.graduation.databinding.ActivityDeletePayBinding
import com.example.graduation.databinding.ActivityMainBinding
import com.example.graduation.databinding.ActivityPayConfirmationBinding

class DeletePayActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var mtts:TextToSpeech
    private lateinit var binding: ActivityDeletePayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityDeletePayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.deletePayBtn.setOnClickListener {
            // Dialog만들기
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.delete_pay_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("결제수단 삭제")

            mBuilder.show()


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