package com.example.ollie

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
import java.util.HashMap

class VMypageUpdate : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1043"
    private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.v_mypage_update)

        val back : Button = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        send()

        val save : Button = findViewById(R.id.save)
        save.setOnClickListener {

            handleVMypageUpdate() //update
        }
    }
    //send
    private fun send(){
        val ssaid : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        val map = HashMap<String?, String?>()

        map["ssaid"] = ssaid

        val call = retrofitInterface!!.executeSend(map)
        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    editNiceView()
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

    //update
    private fun handleVMypageUpdate(){
        //화면
        val nextintent = Intent(this, VMypage::class.java)
        //고유 식별자 (SSAID)
        val vssaid : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        //객체 생성
        val vname : EditText = findViewById(R.id.name)
        val vpasswd : EditText = findViewById(R.id.passwd)
        val vphone : EditText = findViewById(R.id.phone)
        val vbirth : EditText = findViewById(R.id.birth)
        val vheight : EditText = findViewById(R.id.height)
        val vweight : EditText = findViewById(R.id.weight)

        val map = HashMap<String?, String?>()

        //map에 <KEY, VALUE> 세팅하기
        map["vssaid"] = vssaid
        map["vname"] = vname.text.toString()
        map["vphone"] = vphone.text.toString()
        map["vpasswd"] = vpasswd.text.toString()
        map["vbirth"] = vbirth.text.toString()
        map["vheight"] = vheight.text.toString()
        map["vweight"] = vweight.text.toString()

        val call = retrofitInterface!!.executeUpdatev(map)
        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    startActivity(nextintent)
                    Toast.makeText(
                        this@VMypageUpdate,
                        "정보 수정되었습니다.", Toast.LENGTH_LONG
                    ).show()
                } else if (response.code() == 400) {
                    Toast.makeText(
                        this@VMypageUpdate,
                        "정보 수정에 실패했습니다.", Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })

    }

    //select
    private fun editNiceView(){

        var call = retrofitInterface!!.data1
        call?.enqueue(object : Callback<String?> {
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

    //Json 파싱
    private fun parseJson(json: String?) {

        try {

            val jsonObject: JSONArray = JSONObject(json).getJSONArray("result")

            var name : EditText = findViewById(R.id.name)
            var phone : EditText = findViewById(R.id.phone)
            var passwd : EditText = findViewById(R.id.passwd)
            var birth : EditText = findViewById(R.id.birth)
            var height : EditText = findViewById(R.id.height)
            var weight : EditText = findViewById(R.id.weight)

            val actor: JSONObject = jsonObject.getJSONObject(0)
            var u_num = actor.getString("u_num")
            var u_name = actor.getString("u_name")
            var u_phone = actor.getString("u_phone")
            var u_passwd = actor.getString("u_passwd")
            var u_birth = actor.getString("u_birth")
            var u_height = actor.getString("u_height")
            var u_weight = actor.getString("u_weight")

            Log.e(TAG, "$u_name 님의 정보를 가져옴")

            name.setText(u_name)
            phone.setText(u_phone)
            passwd.setText(u_passwd)
            birth.setText(u_birth)
            height.setText(u_height)
            weight.setText(u_weight)

            Log.d(TAG, "jsonObject : $jsonObject.toString()")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}

