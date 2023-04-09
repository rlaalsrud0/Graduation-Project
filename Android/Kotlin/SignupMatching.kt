package com.example.ollie

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.HashMap
import android.telephony.TelephonyManager
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException

class SignupMatching : AppCompatActivity() {
    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null
    private val TAG = this.javaClass.simpleName

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1042"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_matching)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        //Button 객체 생성
        val check : Button = findViewById(R.id.signup)
        //확인 버튼 클릭 시 SSAID, 디바이스 번호, 디바이스 비밀번호를 서버로 전달할 것
        check.setOnClickListener{
            send()
            handleMatchingDialog()
        }
    }

    // 내 SSAID 값 보내기
    private fun send() { // js로 ssiad 번호 보내주기
        //고유 식별자
        val PSSAID : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)

        val map = HashMap<String?, String?>()

        map["ssaid"] = PSSAID

        val call = retrofitInterface!!.executeSend(map)

        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    jsgetinfo()
                    Log.e(TAG, "js에 ssaid 전송 성공")
                } else if (response.code() == 400) {
                    Log.e(TAG, "js에 ssaid 전송 실패")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }

    private fun jsgetinfo() { // 값을 js에서 가져오는 코드

        Log.e(TAG, "jsgetinfo()함수 실행 성공 ")

        val map = HashMap<String?, String?>()

        var call = retrofitInterface!!.ssaidpass

        Log.e(TAG, "객채 선언 성공 ")

        call?.enqueue(object : Callback<String?> {
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                Log.e(TAG, " onResponse() 함수 실행 성공!! ")

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

    //Json 파싱
    private fun parseJson(json: String?) {
        try {

            Log.e(TAG, "parseJson()함수 실행 성공!!! ")

            val jsonObject: JSONArray = JSONObject(json).getJSONArray("result")

            Log.e(TAG, "jsonObject 객체 선언 실행 성공JJJJJ ")

            var u_div = jsonObject.getString(0)

            Log.e(TAG, "DB 열 이름  실행 성공!!! ")

            val actor: JSONObject = jsonObject.getJSONObject(0)
            u_div = actor.getString("u_div")

            Log.e(TAG, "파싱한 구분 : $u_div")

            //login화면으로 가기
            val intent = Intent(this, Login::class.java)
            startActivity(intent)

            Log.d(TAG, "jsonObject : $jsonObject.toString()")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    // 매핑하기
    private fun handleMatchingDialog() {

        //고유 식별자
        val ssaid : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        //객체 생성
        val device : EditText = findViewById(R.id.device)
        val passwd : EditText = findViewById(R.id.passwd)

        val map = HashMap<String?, String?>()

        //map에 <KEY, VALUE> 세팅하기
        map["ssaid"] = ssaid
        map["device"] = device.text.toString()
        map["passwd"] = passwd.text.toString()

        val call = retrofitInterface!!.executeMatching(map)
        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {

                } else if (response.code() == 400) {
                    Toast.makeText(
                        this@SignupMatching,
                        "입력이 올바르지 않습니다.", Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }
}