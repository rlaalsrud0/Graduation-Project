package com.example.ollie

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.HashMap
import android.widget.Toast

import java.io.*


var r_route = ""
var r_name = ""
var r_birth = ""
var r_gender = ""
var r_phone = ""


class PNotify : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1040"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_notify)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        //PHome에서 전달받은 취약계층의 ssaid
        val vssaid = intent.getStringExtra("ssaid").toString()
        Log.e(TAG, "PHome에서 받아온 ssaid : $vssaid")

        val back : Button = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        //js에 취약계층의 ssaid 전송
        send(vssaid)

        val notify : ImageButton = findViewById(R.id.notify)
        notify.setOnClickListener {
            updateJS()
            //문자로 넘어가기
            sendMmsIntent("01012341234")
        }

    }

    private fun updateJS() {
        val map = HashMap<String?, String?>()
        val call = retrofitInterface!!.report(map)

        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    Log.e(TAG, "update 성공")
                } else {
                    Log.e(TAG, "update 실패")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }

    private fun send(vssaid: String) {
        val map = HashMap<String?, String?>()

        map["ssaid"] = vssaid
        Log.e(TAG, "전송한 ssaid ${vssaid}")

        val call = retrofitInterface!!.executeSend(map)

        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    // 성공
                    getjsInfo()
                } else if (response.code() == 400) {
                    Log.e(TAG, "js에 취약계층의 ssaid 전송 실패")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }

    private fun getjsInfo() { //js로부터 영상 경로, 이름, 생년월일, 성별, 전화번호 정보 받기
        Log.e(TAG, "getjsInfo 함수 실행")

        var call = retrofitInterface!!.getVInfo
        call?.enqueue(object : Callback<String?> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                if(response.isSuccessful) {
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
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseJson(json: String) {
        //객체 생성
        val video : VideoView = findViewById(R.id.videoView)
        val name : TextView = findViewById(R.id.name)
        val birth : TextView = findViewById(R.id.birth)
        val gender : TextView = findViewById(R.id.gender)
        val phone : TextView = findViewById(R.id.phone)

        try {
            Log.e(TAG, "parseJson()함수 실행")
            val jsonObject: JSONArray = JSONObject(json).getJSONArray("result")
            Log.e(TAG, "jsonObject 객체 선언")

            val h_oroute: String = JSONObject(json).getString("route")
            Log.e(TAG, h_oroute)

            val actor: JSONObject = jsonObject.getJSONObject(0)

            var u_name = actor.getString("u_name")
            var u_birth = actor.getString("u_birth")
            var u_gender = actor.getString("u_gender")
            var u_phone = actor.getString("u_phone")

            Log.e(TAG, "외출 영상경로 : $h_oroute , 이름 : $u_name, 생년월일 : $u_birth, 성별 : $u_gender, 전화번호 : $u_phone")

            video.setVideoPath(h_oroute)
            name.setText(u_name)
            birth.setText(u_birth)
            gender.setText(u_gender)
            phone.setText(u_phone)

            video.requestFocus()
            video.start()

            r_route = h_oroute
            r_name = u_name
            r_birth = u_birth
            r_gender = u_gender
            r_phone = u_phone

            Log.d(TAG, "jsonObject : $jsonObject.toString()")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun sendMmsIntent(number: String?) {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra("address", number)
            sendIntent.putExtra("subject", "MMS Test")
            sendIntent.putExtra(
                "sms_body",
                "노인 실종 신고\n" +
                        "외출 당시 영상 : ${r_route},\n" +
                        " 이름 : ${r_name},\n" +
                        " 생년월일 : ${r_birth},\n" +
                        " 성별 : ${r_gender},\n" +
                        " 전화번호 :${r_phone}"
            )
            sendIntent.type = "video/*"
            startActivity(Intent.createChooser(sendIntent, resources.getString(R.string.app_name)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}