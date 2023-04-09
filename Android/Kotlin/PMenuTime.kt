package com.example.ollie

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.RequiresApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

var Oresult : String = ""

class PMenuTime : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    private val BASE_URL = "ADDRESS:1038"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_menu_time)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)
        val name : TextView = findViewById(R.id.name)

        //취약계층 이름 가져오기기
        val secondIntent = getIntent()
        val Vname = secondIntent.getStringExtra("Vname")
        name.setText(Vname.toString())
        Log.e(TAG, Vname.toString())

        //활성화 버튼
        val onoff : ToggleButton = findViewById(R.id.onoff)
        onoff.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                onoff.setBackgroundResource(R.drawable.on)
                Oresult = "Y" //map에 보낼 때 사용
                Log.e(TAG, Oresult)
            } else {
                onoff.setBackgroundResource(R.drawable.off)
                Oresult = "N" //map에 보낼 때 사용
                Log.e(TAG, Oresult)
            }
        })

        val back : Button = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val save : Button = findViewById(R.id.save)
        save.setOnClickListener {
            handleLoginDialog()
            Log.e(TAG, Oresult)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun handleLoginDialog() {
        val timePicker : TimePicker = findViewById(R.id.timePicker)

        val hour = timePicker.hour
        val minute = timePicker.minute

        Log.e(TAG, hour.toString() + "시" + minute.toString() + "분")

        //객체 생성
        val name : TextView = findViewById(R.id.name)

        //취약계층 이름 가져오기기
        val secondIntent = getIntent()
        val Vname = secondIntent.getStringExtra("Vname")
        name.setText(Vname)

        //div에 넣기
        var div: MutableList<String> = ArrayList()
        if (Vname != null) {
            div.add(Vname)
        }
        Log.e(TAG, div[0])

        val map = HashMap<String, String>()

        map["vname"] = div[0]
        map["hour"] = hour.toString()
        map["min"] = minute.toString()
        map["active"] = Oresult

        Log.e(TAG, "key세팅 완료")
        Log.e(TAG, Oresult)

        val intent = Intent(this, PMenuTime::class.java)
        val nextintent = Intent(this, PHome::class.java)

        val call = retrofitInterface!!.executeTime(map)
        Log.e(TAG, "여기는?")
        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    Toast.makeText(
                this@PMenuTime, "저장되었습니다.", Toast.LENGTH_LONG
                    ).show()
                    startActivity(nextintent)
                    Log.e(TAG, "성공")
                } else if (response.code() == 404) {
                    startActivity(intent)
                    Log.e(TAG, "실패")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }
}

