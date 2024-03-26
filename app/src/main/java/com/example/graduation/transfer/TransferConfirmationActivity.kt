package com.example.graduation.transfer

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.graduation.R
import com.example.graduation.databinding.ActivityTransferConfirmationBinding
import java.util.Locale
import java.util.concurrent.Executor

class TransferConfirmationActivity : AppCompatActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    lateinit var mtts:TextToSpeech
    private lateinit var binding: ActivityTransferConfirmationBinding
    private var executor: Executor? = null
    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null
    lateinit var mediaPlayerSuccess: MediaPlayer
    lateinit var mediaPlayerFailure: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences에서 소리 on/off 상태 불러오기
        val sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)
        val soundState = sharedPreferences.getBoolean("soundState", false)

        mtts = TextToSpeech(this) { //모든 글자를 소리로 읽어주는 tts
            mtts.language = Locale.KOREAN //언어:한국어
        }

        //TODO:0326 가입한 사람의 이름이 받는사람에게 표시:000에 떠야함
        val spSender = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val name = spSender.getString("name", "")
        binding.senderNameTv.text=name //해당 회원의 이름을 가져와서 텍스트뷰에 반영


        //지문 관련 변수
        biometricPrompt = setBiometricPrompt()
        promptInfo = setPromptInfo()

        //화면 정보 읽기
        mtts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val titleText ="송금하기 전 마지막 확인 화면입니다"
                val explainText = "${binding.receiverNameTv.text}님에게 ${binding.receiverMoneyAmountTv.text}원을 이체하시겠습니까?"
                val senderNameText="받는 분에게 표시: ${binding.senderNameTv.text}"
                val buttonExplainText=binding.explainSenderNameTv
                val textToSpeak = "$titleText $explainText $senderNameText $buttonExplainText"
                onSpeech(textToSpeak)
            } else {
                // 초기화가 실패한 경우
                Log.e("TTS", "TextToSpeech 초기화 실패")
            }
        }


        //받을 사람 이름, 계좌번호, 금액 데이터 가져오기
        val sharedPreferences2 = getSharedPreferences("transferInfo", Context.MODE_PRIVATE)
        val recipientName = sharedPreferences2.getString("recipientName", "")
        val accountNumber = sharedPreferences2.getString("ReceiverAccountNumber", "")
        val sendMoneyAmount = sharedPreferences2.getString("moneyAmount", "")
        //어느 은행으로 보낼지
        val receiverBank = sharedPreferences.getString("selectedBank", "")

        // 불러온 데이터 사용하기
        if (recipientName != null && accountNumber != null&&sendMoneyAmount != null &&receiverBank != null) {
            // 이름과 계좌번호,금액 모두 존재할 때
            binding.receiverNameTv.text=recipientName
            binding.receiverAccountNumberTv.text=accountNumber
            binding.receiverMoneyAmountTv.text=sendMoneyAmount
            binding.receiverBankTv.text=receiverBank
        } else {
            Log.d("yk","받을 사람의 이름 또는 계좌번호가 존재하지 않음")
        }


        //소리 설정
        mtts = TextToSpeech(this) { //모든 글자를 소리로 읽어주는 tts
            mtts.language = Locale.KOREAN //언어:한국어
        }
        // 효과음 초기화
        mediaPlayerSuccess = MediaPlayer.create(this, R.raw.success_sound)
        mediaPlayerFailure = MediaPlayer.create(this, R.raw.failure_sound)

        binding.prevBtn.setOnClickListener {
            if (soundState) {
                onSpeech(binding.prevBtn.text)
            }

            val intent = Intent(this, TransferVoiceActivity::class.java)
            startActivity(intent)
        }

        // 권한 설정
        requestPermission()

        // RecognizerIntent 생성
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)    // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")         // 언어 설정


        // <말하기> 버튼 눌러서 음성인식 시작
        binding.speechBtn.setOnClickListener {
            // 새 SpeechRecognizer 를 만드는 팩토리 메서드
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@TransferConfirmationActivity)
            speechRecognizer.setRecognitionListener(recognitionListener)    // 리스너 설정
            speechRecognizer.startListening(intent)                         // 듣기 시작
        }

        //TODO:결제승인 과정에서 오류시 else 처리
        binding.approveBtn.setOnClickListener {

            if (soundState) {
                onSpeech(binding.approveBtn.text)
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
        return promptInfo as BiometricPrompt.PromptInfo
    }
    private fun setBiometricPrompt(): BiometricPrompt {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this@TransferConfirmationActivity, executor!!, object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@TransferConfirmationActivity, """"지문 인식 ERROR [ errorCode: $errorCode, errString: $errString ]""".trimIndent(), Toast.LENGTH_SHORT).show()
            }

            //지문 인식 성공시
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                playSuccessSound()
                Toast.makeText(this@TransferConfirmationActivity, "지문 인증에 성공하였습니다.", Toast.LENGTH_SHORT).show()

                // 받는 사람의 이름과 금액을 SharedPreferences에 저장
                val sharedPreferences = getSharedPreferences("transferInfo", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("recipientName", binding.receiverNameTv.text.toString())
                editor.putString("moneyAmount", binding.receiverMoneyAmountTv.text.toString())
                editor.apply()

               //송금완료 화면으로 이동
                val intent = Intent(this@TransferConfirmationActivity,TransferCompletedActivity::class.java)
                startActivity(intent)
            }

            //지문 인식 실패시
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                playFailureSound()
                Toast.makeText(this@TransferConfirmationActivity, "지문 인증에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }

        } )
        return biometricPrompt as BiometricPrompt
    }


    /*
    * 생체 인식 인증을 사용할 수 있는지 확인
    * */
    fun authenticateToEncrypt() = with(binding) {


        var textStatus = ""
        val biometricManager = BiometricManager.from(this@TransferConfirmationActivity)
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

                val dialogBuilder = AlertDialog.Builder(this@TransferConfirmationActivity)
                dialogBuilder //지문등록 필요함 안내 다이얼로그
                    .setTitle("SoriPay")
                    .setMessage("지문 등록이 필요합니다. 지문등록 설정화면으로 이동하시겠습니까?")
                    .setPositiveButton("확인") { dialog, which -> goBiometricSettings() }

                dialogBuilder.show()
            }

            //기타 실패
            else ->  textStatus = "생체 정보 인식 실패"

        }
       /* binding.statusTv.text = textStatus*/

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
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
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


    // 권한 설정 메소드
    private fun requestPermission() {
        // 버전 체크, 권한 허용했는지 체크
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(
                this@TransferConfirmationActivity,
                Manifest.permission.RECORD_AUDIO
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@TransferConfirmationActivity,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0
            )
        }
    }

    // 리스너 설정
    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        // 말하기 시작할 준비가되면 호출
        override fun onReadyForSpeech(params: Bundle) {
            Toast.makeText(applicationContext, "음성인식 시작", Toast.LENGTH_SHORT).show()
            binding.stateTv.text = "이제 말씀하세요!"
        }
        // 말하기 시작했을 때 호출
        override fun onBeginningOfSpeech() {
            binding.stateTv.text = "잘 듣고 있어요."
        }
        // 입력받는 소리의 크기를 알려줌
        override fun onRmsChanged(rmsdB: Float) {}
        // 말을 시작하고 인식이 된 단어를 buffer에 담음
        override fun onBufferReceived(buffer: ByteArray) {}
        // 말하기를 중지하면 호출
        override fun onEndOfSpeech() {
            binding.stateTv.text = "정상적으로 음성 인식이 완료되었습니다."
            //화면 정보 읽기

        }
        // 오류 발생했을 때 호출
        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                else -> "알 수 없는 오류임"
            }
            binding.stateTv.text = "에러 발생: $message"
        }
        // 인식 결과가 준비되면 호출
        override fun onResults(results: Bundle) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            for (i in matches!!.indices) binding.senderNameTv.text = matches[i] /*binding.resultTv.text = matches[i]*/


            val sharedPreferences = getSharedPreferences("sp1", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("toAccount", binding.resultTv.text.toString())
            editor.apply()
        }
        // 부분 인식 결과를 사용할 수 있을 때 호출
        override fun onPartialResults(partialResults: Bundle) {}
        // 향후 이벤트를 추가하기 위해 예약
        override fun onEvent(eventType: Int, params: Bundle) {}
    }


}