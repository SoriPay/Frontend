package com.example.graduation

//은행 로고 이미지, 은행이름(하나은행 등), 계좌번호
data class PaymentMethod(
    val imageResId: Int,
    val engBank:String,
    val korBank: String,
    val accountNumber: String,
    var isSelected: Boolean = false
)
