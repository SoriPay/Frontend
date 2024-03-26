package com.example.graduation

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import android.os.Build
import android.provider.Settings
import android.speech.tts.TextToSpeech
import androidx.biometric.BiometricPrompt
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.example.graduation.databinding.ActivityPayFingerBinding
import java.util.Locale
import java.util.concurrent.Executor


class PayFingerActivity : AppCompatActivity() {
    private var executor: Executor? = null
    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null
    lateinit var binding: ActivityPayFingerBinding
    lateinit var mtts:TextToSpeech
    lateinit var mediaPlayerSuccess: MediaPlayer
    lateinit var mediaPlayerFailure: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPayFingerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // SharedPreferences에서 소리 on/off 상태 불러오기
        val sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)
        val soundState = sharedPreferences.getBoolean("soundState", false)

        mtts = TextToSpeech(this) { //모든 글자를 소리로 읽어주는 tts
            mtts.language = Locale.KOREAN //언어:한국어
        }
        mtts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // 화면 정보 읽어주기
                val textToSpeak ="화면 하단의 버튼을 누르고 등록된 지문을 인식시켜주세요."
                onSpeech(textToSpeak)
            } else {
                // 초기화가 실패한 경우
                Log.e("TTS", "TextToSpeech 초기화 실패")
            }
        }

        // 효과음 초기화
        mediaPlayerSuccess = MediaPlayer.create(this, R.raw.success_sound)
        mediaPlayerFailure = MediaPlayer.create(this, R.raw.failure_sound)



        //이전 화면 버튼 클릭시
        binding.prevBtn.setOnClickListener {
            if (soundState) {
                onSpeech(binding.prevBtn.text)
            }

            val intent = Intent(this, ChoosePayMethodActivity::class.java)
            startActivity(intent)
        }


        //지문 관련 변수
        biometricPrompt = setBiometricPrompt()
        promptInfo = setPromptInfo()

        //지문 인증하기 호출 버튼 클릭 시
        binding.fingerAuthBtn.setOnClickListener {
            if (soundState) {
                onSpeech(binding.fingerAuthBtn.text)
            }

            authenticateToEncrypt()  //생체 인증 가능 여부확인
        }


    }

    private fun setPromptInfo(): BiometricPrompt.PromptInfo {

        val promptBuilder: BiometricPrompt.PromptInfo.Builder = BiometricPrompt.PromptInfo.Builder()

        promptBuilder.setTitle("지문 인증")
        promptBuilder.setSubtitle("터치 센서에 지문을 인식하고 결제하세요")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { //  안면인식 ap사용 android 11부터 지원
            promptBuilder.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        }

        promptInfo = promptBuilder.build()
        return promptInfo as PromptInfo
    }


    private fun setBiometricPrompt(): BiometricPrompt {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this@PayFingerActivity, executor!!, object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@PayFingerActivity, """"지문 인식 ERROR [ errorCode: $errorCode, errString: $errString ]""".trimIndent(), Toast.LENGTH_SHORT).show()
            }

            //지문 인식 성공시
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                playSuccessSound()
                Toast.makeText(this@PayFingerActivity, "지문 인증에 성공하였습니다.", Toast.LENGTH_SHORT).show()

                //결제승인 확인 화면으로 이동
                val intent = Intent(this@PayFingerActivity, PayConfirmationActivity::class.java)
                startActivity(intent)
            }

            //지문 인식 실패시
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                playFailureSound()
                Toast.makeText(this@PayFingerActivity, "지문 인증에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }

        } )
        return biometricPrompt as BiometricPrompt
    }


    /*
    * 생체 인식 인증을 사용할 수 있는지 확인
    * */
    fun authenticateToEncrypt() = with(binding) {

        Log.d("0222", "authenticateToEncrypt() ")

        var textStatus = ""
        val biometricManager = BiometricManager.from(this@PayFingerActivity)
//        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {

            //생체 인증 가능
            BiometricManager.BIOMETRIC_SUCCESS -> textStatus = "이 디바이스에서 생체 정보를 활용할 수 있습니다."

            //기기에서 생체 인증을 지원하지 않는 경우
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> textStatus = "이 디바이스에서는 생체 정보 활용이 불가합니다."

            //현재 생체 인증을 사용할 수 없는 경우
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> textStatus = "생체 정보 활용이 허용되지 않았습니다."

            //생체 인식 정보가 등록되어 있지 않은 경우
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                textStatus = "생체 인식 정보가 디바이스에 등록되어 있지 않습니다."

                val dialogBuilder = AlertDialog.Builder(this@PayFingerActivity)
                dialogBuilder //지문등록 필요함 안내 다이얼로그
                    .setTitle("SoriPay")
                    .setMessage("지문 등록이 필요합니다. 지문등록 설정화면으로 이동하시겠습니까?")
                    .setPositiveButton("확인") { dialog, which -> goBiometricSettings() }

                dialogBuilder.show()
            }

            //기타 실패
            else ->  textStatus = "생체 정보 인식 실패"

        }
        binding.statusTv.text = textStatus

        //인증 실행하기
        goAuthenticate()
    }


    /*
    * 생체 인식 인증 실행
    * */
    private fun goAuthenticate() {
        Log.d("0222", "goAuthenticate - promptInfo : $promptInfo")
        promptInfo?.let {
            biometricPrompt?.authenticate(it);  //인증 실행
        }
    }


    /*
    * 지문 등록 화면으로 이동
    * */
    fun goBiometricSettings() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        }

    }

    private fun onSpeech(text: CharSequence) {
        mtts.speak(text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun playSuccessSound() {
        mediaPlayerSuccess.start()
    }

    private fun playFailureSound() {
        mediaPlayerFailure.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mtts.shutdown()
        mediaPlayerSuccess.release()
        mediaPlayerFailure.release()
    }

}
