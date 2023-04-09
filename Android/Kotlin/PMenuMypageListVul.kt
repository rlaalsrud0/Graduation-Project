package com.example.ollie

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class PMenuMypageListVul : AppCompatActivity() {
    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1036"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_menu_mypage_list_vul)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        val back : Button = findViewById(R.id.back)
        back.setOnClickListener{
            finish()
        }

        // 화면이 로드될 때, 나의 ssaid 전송
        send()
    }

    private fun send() { // js로 ssaid 번호 보내주기
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
                    // js에서 보호자 이름 가져오기
                    jsgetname()
                } else if (response.code() == 400) {
                    Log.e(TAG, "js로 ssaid 전송 실패")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }

    private fun jsgetname() { // 이름과 디바이스 번호를 js에서 가져오는 코드
        Log.e(TAG, "jsgetname()함수 실행 성공 ")

        val map = HashMap<String?, String?>()

        var call = retrofitInterface!!.getVName(map)
        call?.enqueue(object : Callback<String?>{
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

    private fun parseJson(json: String) {
        lateinit var recyclerView : RecyclerView

        var nameList = ArrayList<String>()
        var deviceList = ArrayList<String>()

        lateinit var adapter: VulAdapter

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager= LinearLayoutManager(this@PMenuMypageListVul)

        try {
            Log.e(TAG, "parseJson()함수 실행 성공!!! ")

            val jsonObject: JSONArray = JSONObject(json).getJSONArray("result")

            Log.e(TAG, "jsonObject 객체 선언 실행 성공")
            Log.e(TAG, "DB 열 이름  실행 성공!!! ")


            for (i in 0 until jsonObject.length()) {
                val actor: JSONObject = jsonObject.getJSONObject(i)

                var u_name = actor.getString("u_name")
                var device = actor.getString("device")

                Log.e(TAG, "파싱한 보호자 이름 : $u_name")

                nameList.add(u_name)
                deviceList.add(device)

                Log.e(TAG, "add 성공")

                adapter = VulAdapter(nameList, deviceList, this@PMenuMypageListVul)

                recyclerView.adapter= adapter
            }
            Log.d(TAG, "jsonObject : $jsonObject.toString()")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}