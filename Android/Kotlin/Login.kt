package com.example.ollie

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ollie.databinding.ActivityMainBinding
import org.altbeacon.beacon.Beacon
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

class Login : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1026"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        //객체 생성
        val find_passwd : Button = findViewById(R.id.find_passwd)
        find_passwd.setOnClickListener {
            val intent = Intent(this, PasswdCheck::class.java)
            startActivity(intent)
        }

        //객체 생성
        val login_btn : Button = findViewById(R.id.login_btn)
        //버튼 클릭 시 이름, 전화번호, 비밀번호를 서버로 넘긴다.
        login_btn.setOnClickListener {
            handleLoginDialog()
            //구조요청 서비스 스타트
            val serviceIntent = Intent(this, PHelp::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
    }

    private fun handleLoginDialog() {
        //고유 식별자 (SSAID)
        val SSAID : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)

        //객체 생성
        val name : EditText = findViewById(R.id.login_name)
        val phone : EditText = findViewById(R.id.login_phone)
        val passwd : EditText = findViewById(R.id.login_passwd)

        val map = HashMap<String?, String?>()

        //map에 <KEY, VALUE> 세팅하기
        map["ssaid"] = SSAID
        map["name"] = name.text.toString()
        map["phone"] = phone.text.toString()
        map["passwd"] = passwd.text.toString()

        val nextintent = Intent(this, Login::class.java)

        val call = retrofitInterface!!.executeLogin(map)
        call?.enqueue(object : Callback<LoginResult?> {
            override fun onResponse(
                call: Call<LoginResult?>,
                response: Response<LoginResult?>
            ) {
                if(response.code() == 200){
                    //js호출
                    jsGetLoginInfo()
                }else if(response.code() == 400){
                    Log.e(TAG, "정보가 일치하지 않습니다.")
                    startActivity(nextintent)
                }
            }

            override fun onFailure(call: Call<LoginResult?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }

    private fun jsGetLoginInfo() {
        //보호자인지 취약계층인지 받기
        Log.e(TAG, "jsGetLoginInfo() 함수 실행 성공")

        val map = HashMap<String?, String?>()

        var call = retrofitInterface!!.loginData(map)
        call?.enqueue(object : Callback<String?>{
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                if(response.isSuccessful){
                    Log.e(TAG, "구분 읽어와짐")
                    if(response.body() != null){
                        Log.e(TAG, "response 결과 : " + response.body().toString())
                        parseJsonDiv(response.body().toString())
                    }else{
                        try{
                            Log.e(TAG, "실패 : " + response.errorBody()!!.string())
                        } catch (e: IOException){
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

    private fun parseJsonDiv(json: String) {
        val Pintent = Intent(this, PDevice::class.java)
        val Vintent = Intent(this, VHelp::class.java)

        try {
            Log.e(TAG, "parseJsonDiv 함수 실행 성공!")

            val jsonObject: JSONArray = JSONObject(json).getJSONArray("result")
            val actor: JSONObject = jsonObject.getJSONObject(0)
            var u_div = actor.getString("u_div")

            Log.e(TAG, u_div)

            if(u_div.equals("보호자")){
                Log.e(TAG, "result.equals(보호자)")
                startActivity(Pintent)
            }else if(u_div.equals("취약계층")) {
                Log.e(TAG, "result.equals(취약계층)")
                startActivity(Vintent)
            }
        } catch (e: JSONException){
            e.printStackTrace()
        }
    }
}