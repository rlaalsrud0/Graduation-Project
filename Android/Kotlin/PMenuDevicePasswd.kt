package com.example.ollie

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.p_menu_device_passwd.*
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



class PMenuDevicePasswd : AppCompatActivity() {


    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1034"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_menu_device_passwd)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        // PMenu 에서 전달한 디바이스번호 받기
        val deviceNum = intent.getStringExtra("device").toString()
        Log.e(TAG, "PMenu에서 받아온 디바이스번호 : $deviceNum")

        val back : Button = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val input : Button = findViewById(R.id.input)
        input.setOnClickListener {
            send(deviceNum)
        }
    }


    private fun send(deviceNum: String) { //js에 device번호와 입력받은 비밀번호 전송
        //객체 생성
        val passwd : EditText = findViewById(R.id.passwd)

        val map = HashMap<String?, String?>()

        map["device"] = deviceNum
        map["passwd"] = passwd.text.toString()

        val call = retrofitInterface!!.executeDeviceSend(map)

        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    // 성공메세지 띄우기
                    getInfo(deviceNum)
                } else if (response.code() == 400) {
                    Toast.makeText(
                        this@PMenuDevicePasswd,
                        "비밀번호를 다시 입력하세요.", Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }

    private fun getInfo(deviceNum: String) {

        Log.e(TAG, "getInfo()함수 실행 성공 ")

        var call = retrofitInterface!!.getDeviceInfo

        Log.e(TAG, "객채 선언 성공 ")

        call?.enqueue(object : Callback<String?> {
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                Log.e(TAG, " onResponse() 함수 실행 성공!! ")

                if(response.isSuccessful) {
                    Log.e(TAG, "성공적이지 않은거야?")
                    // do something
                    if (response.body() != null) {
                        Log.e(TAG, "성공 : " + response.body())
                        parseJson(response.body().toString(), deviceNum)
                    }else {
                        try {
                            Log.e(TAG, "실패 : " + response.errorBody()!!.string())
                            Toast.makeText(
                                this@PMenuDevicePasswd,
                                "비밀번호를 다시 입력하세요.", Toast.LENGTH_LONG
                            ).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }

    private fun parseJson(json: String, deviceNum: String) {
        val nextintent = Intent(this, PMenuDeviceDelete::class.java)
        nextintent.putExtra("device", deviceNum)
        Log.e(TAG, "성공 : " + deviceNum)

        try{
            val jsonObject = JSONObject(json).getBoolean("result")
            var result = jsonObject.toString()

            if(result.equals("true")){
                startActivity(nextintent)
            }
            else if(result.equals("false")){
                passwd.setText(null)
                Toast.makeText(
                    this@PMenuDevicePasswd,
                    "비밀번호를 다시 입력하세요.", Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}