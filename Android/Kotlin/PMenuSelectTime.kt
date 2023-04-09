package com.example.ollie

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.HashMap


var div: MutableList<String> = ArrayList()

class PMenuSelectTime : AppCompatActivity() {
    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1037"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_menu_select_time)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        // PMenu에서 전달한 디바이스번호 받기
        val deviceNum = intent.getStringExtra("device")
        Log.e(TAG, "PMenu에서 받아온 디바이스번호 : $deviceNum")

        //js와 연동
        send()

        //Button 객체 생성
        val back : Button = findViewById(R.id.back)
        back.setOnClickListener{
            finish()
        }

        val plus : Button = findViewById(R.id.plus)
        plus.setOnClickListener{
            val intent = Intent(this, PDevice::class.java)
            startActivity(intent)
        }

        val home : Button = findViewById(R.id.home)
        home.setOnClickListener{
            val intent = Intent(this, PHome::class.java)
            startActivity(intent)
        }

        val menu : Button = findViewById(R.id.menu)
        menu.setOnClickListener{
            val intent = Intent(this, PMenu::class.java)
            intent.putExtra("device",deviceNum)
            startActivity(intent)
        }

        //선택한 취약계층의 이름을 가지고 넘어가야 한다.
        val select : Button = findViewById(R.id.select)
        select.setOnClickListener{
            val intent = Intent(this, PMenuTime::class.java)
            intent.putExtra("Vname", div[0])
            Log.e(TAG, "성공 : " + div[0])
            startActivity(intent)
        }
    }

    private fun send() {
        //고유 식별자
        val SSAID : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)

        val map = HashMap<String?, String?>()

        map["ssaid"] = SSAID

        val call = retrofitInterface!!.executeSend(map)

        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    getData()
                } else if (response.code() == 400) {
                    Log.e(TAG, "js로 ssaid 전송 실패")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }

    private fun getData() {
        val call = retrofitInterface!!.vselect

        call?.enqueue(object : Callback<String?> {
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                if(response.isSuccessful) {
                    Log.e(TAG, "값 불러와짐 ")
                    // do something
                    if (response.body() != null) {
                        Log.e(TAG, "성공 : " + response.body())
                        parseJson(response.body().toString())
                    }else {
                        try {
                            Log.e(TAG, "실패 : " + response.errorBody()!!.string())
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }else{
                    Log.e(TAG, "response 실패")
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }

    private fun parseJson(json: String) {
        try {

            Log.e(TAG, "parseJson()함수 실행 성공!!! ")

            val jsonObject: JSONArray = JSONObject(json).getJSONArray("result")
            var u_name = jsonObject.getString(0)

            for (i in 0 until jsonObject.length()) {
                val actor: JSONObject = jsonObject.getJSONObject(i)
                //div에 추가
                div.add(actor.getString("u_name"))
            }

            //spinner 객체 생성
            val vspinner: Spinner = findViewById(R.id.vspinner)

            //어댑터 생성
            val adapter: ArrayAdapter<String> = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,div
            )

            //어댑터 설정
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

            //스피너에 어댑터 적용
            vspinner.adapter= adapter

            Log.d(TAG, "jsonObject : $jsonObject.toString()")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}