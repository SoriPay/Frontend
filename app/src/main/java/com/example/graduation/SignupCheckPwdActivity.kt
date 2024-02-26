package com.example.graduation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.example.graduation.databinding.ActivityMainBinding
import com.example.graduation.databinding.ActivitySignupCheckpwdBinding

class SignupCheckPwdActivity : AppCompatActivity(), SignupDialogInterface {

    lateinit var mtts:TextToSpeech
    private lateinit var binding: ActivitySignupCheckpwdBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupCheckpwdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clickViewEvents()}

    private fun clickViewEvents() {
        binding.enterButton.setOnClickListener {
            //비밀번호 일치하지 않으면 (추후 작성)
            run {
                val dialog = SignupDialog(this, "비밀번호가 일치하지않습니다.")
                dialog.isCancelable = false
                dialog.show(this.supportFragmentManager, "SignupDialog")
            }
            //비밀번호 일치하면 저장 후 다음 화면으로 이동(추후 작성)
        }
    }

    override fun onDialogButtonClick() {
        finish()
    }
    private fun onSpeech(text: CharSequence) {
        mtts.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
    }

}