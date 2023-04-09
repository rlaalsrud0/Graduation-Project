package com.example.ollie

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import java.util.ArrayList
import java.util.HashMap

class PMenu : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1032"
    private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_menu)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        // PHome 에서 전달한 디바이스번호 받기
        val deviceNum = intent.getStringExtra("device").toString()
        Log.e(ContentValues.TAG, "PHome에서 받아온 디바이스번호 : $deviceNum")

        val back : Button = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val my : Button = findViewById(R.id.my)
        my.setOnClickListener {
            val intent = Intent(this, PMenuMypage::class.java)
            intent.putExtra("device", deviceNum)
            startActivity(intent)
        }

        val set_time : Button = findViewById(R.id.set_time)
        set_time.setOnClickListener {
            val intent = Intent(this, PMenuSelectTime::class.java)
            intent.putExtra("device", deviceNum)
            startActivity(intent)
        }


        val list : Button = findViewById(R.id.list)
        list.setOnClickListener {
            val intent = Intent(this, PMenuListUser::class.java)
            intent.putExtra("device", deviceNum)
            startActivity(intent)
        }


        val device : Button = findViewById(R.id.device)
        device.setOnClickListener {
            val intent = Intent(this, PMenuDevicePasswd::class.java)
            intent.putExtra("device", deviceNum)
            startActivity(intent)
        }

        send()
    }
    private fun send(){
        val SSAID : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)

        val map = HashMap<String?, String?>()
        map["ssaid"] = SSAID

        Log.e(TAG, "ssaid 전송 ${SSAID}")

        val call = retrofitInterface!!.executeSend(map)
        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    getpname()
                    Log.e(TAG, "함수실행")
                } else if (response.code() == 400) {
                    Log.e(TAG, "실패")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }
    private fun getpname(){
        Log.e(TAG, "getpname 함수 실행 성공")

        val SSAID : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)

        val map = HashMap<String?, String?>()
        map["ssaid"] = SSAID

        Log.e(TAG, "ssaid 전송 ${SSAID}")

        var call = retrofitInterface!!.getPMenuName(map)
        call?.enqueue(object : Callback<String?> {
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                if(response.isSuccessful) {
                    Log.e(TAG, "보호자 값 불러와짐 ")
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
    private fun parseJson(json: String?) {
        try {
            Log.e(TAG, "parseJson()함수 실행 성공!!! ")

            var name : TextView = findViewById(R.id.menu_name)

            val jsonObject: JSONArray = JSONObject(json).getJSONArray("result")

            val actor: JSONObject = jsonObject.getJSONObject(0)
            var u_name = actor.getString("u_name")

            Log.e(TAG, "파싱한 보호자이름 : $u_name")
            name.setText(u_name)
            Log.d(TAG, "jsonObject : $jsonObject.toString()")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}