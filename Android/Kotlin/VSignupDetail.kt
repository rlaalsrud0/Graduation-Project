package com.example.ollie

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.v_signup_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.HashMap

class VSignupDetail : AppCompatActivity() {
    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1046"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.v_signup_detail)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        //spinner에 들어가는 데이터
        val div = arrayOf("남", "여")

        //spinner 객체 생성
        val gspinner: Spinner = findViewById(R.id.gspinner)

        //어댑터 생성
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line, div
        )

        //어댑터 설정
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        //스피너에 어댑터 적용
        gspinner.adapter = adapter

        Log.e(TAG, adapter.toString())

        //Button 객체 생성
        val next : Button = findViewById(R.id.next)

        //버튼 클릭 시 ssaid, 생년월일, 성별, 키, 몸무게를 서버로 전달할 것
        next.setOnClickListener {
            val spinnerGender = gspinner.selectedItem.toString()
            handleUpdateDialog(spinnerGender)
        }
    }

    private fun handleUpdateDialog(spinnerGender: String) {
        //고유 식별자
        val ssaid : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        //객체 생성
        val birth : EditText = findViewById(R.id.birth)
        val height : EditText = findViewById(R.id.height)
        val weight : EditText = findViewById(R.id.weight)

        val map = HashMap<String?, String?>()

        //map에 <KEY, VALUE> 세팅하기
        map["ssaid"] = ssaid
        map["birth"] = birth.text.toString()
        if(spinnerGender == "남"){
            map["gender"] = "남"
        }
        else if(spinnerGender == "여"){
            map["gender"] = "여"
        }
        map["height"] = height.text.toString()
        map["weight"] = weight.text.toString()

        val call = retrofitInterface!!.executeDetail(map)
        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    Log.e(TAG, "취약계층 디테일 update 성공")
                } else if (response.code() == 400) {
                    Log.e(TAG, "취약계층 디테일 update 실패")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })

        val nextIntent = Intent(this, SignupMatching::class.java)
        startActivity(nextIntent)
    }
}