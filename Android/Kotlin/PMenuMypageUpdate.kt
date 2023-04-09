package com.example.ollie

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

import android.provider.Settings
import android.util.Log
import android.widget.EditText
import android.widget.Toast
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



class PMenuMypageUpdate : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1043"

    private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_menu_mypage_update)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        send()

        val back : Button = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val save : Button = findViewById(R.id.save)
        save.setOnClickListener {
            handleSignupDialog()
            val intent = Intent(this, PMenuMypage::class.java)
            startActivity(intent)
        }

    }

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
                    // js에서 보호자 정보 가져오기
                    jsgetinfo()
                } else if (response.code() == 400) {
                    Log.e(TAG, "js로 ssaid 보내기 실패")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }


    private fun handleSignupDialog() { // 값을 js로 보내는 코드
        //고유 식별자
        val PSSAID : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)

        //객체 생성
        val name : EditText = findViewById(R.id.name)
        val phone : EditText = findViewById(R.id.phone)
        val passwd : EditText = findViewById(R.id.passwd)

        val map = HashMap<String?, String?>()

        // EditText 안에 있는 내용을 가져올거임
        // map 에 <KEY, VALUE> 세팅하기
        map["pssaid"] = PSSAID
        map["pname"] = name.text.toString()
        map["pphone"] = phone.text.toString()
        map["ppasswd"] = passwd.text.toString()

        val call = retrofitInterface!!.executeUpdatep(map)

        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    Toast.makeText(
                        this@PMenuMypageUpdate,
                        "정보 수정하였습니다.", Toast.LENGTH_LONG
                    ).show()
                } else if (response.code() == 400) {
                    Toast.makeText(
                        this@PMenuMypageUpdate,
                        "정보 수정에 실패했습니다.", Toast.LENGTH_LONG
                    ).show()
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

        //고유 식별자
        val PSSAID : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)
        map["ssaid"] = PSSAID

        var call = retrofitInterface!!.data1(map)
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

            var name : EditText = findViewById(R.id.name)
            var phone : EditText = findViewById(R.id.phone)
            var passwd : EditText = findViewById(R.id.passwd)

            val jsonObject: JSONArray = JSONObject(json).getJSONArray("result")

            val actor: JSONObject = jsonObject.getJSONObject(0)
            var u_num = actor.getString("u_num")
            var u_name = actor.getString("u_name")
            var u_phone = actor.getString("u_phone")
            var u_passwd = actor.getString("u_passwd")

            Log.e(TAG, "파싱한 보호자이름 : $u_name, 보호자이름 : $u_phone, 보호자이름 : $u_passwd, ")

            name.setText(u_name)
            phone.setText(u_phone)
            passwd.setText(u_passwd)

            Log.d(TAG, "jsonObject : $jsonObject.toString()")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}