package com.example.ollie

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.*
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

class PMenuVideo : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1039"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_menu_video)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)


        val back : Button = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val name: TextView = findViewById(R.id.name)

        //취약계층 이름 가져오기
        val secondIntent = getIntent()
        val Vname = secondIntent.getStringExtra("vname")
        name.setText(Vname.toString())
        Log.e(ContentValues.TAG, Vname.toString())

        if (Vname != null) {
            send(Vname)
        }
    }
    private fun send(Vname: String){
        val map = HashMap<String?, String?>()

        map["name"] = Vname
        Log.e(ContentValues.TAG, "전송한 이름 $Vname")

        val call = retrofitInterface!!.executename(map)

        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    jsgetvideoO()
                    jsgetvideoV()
                    Log.e(TAG, "js에 이름 전송 성공")
                } else if (response.code() == 400) {
                    Log.e(TAG, "js에 이름 전송 실패")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }

    private fun jsgetvideoO() { // 이름과 디바이스 번호를 js에서 가져오는 코드
        Log.e(TAG, "jsgetvideo 함수 실행 성공 ")

        val map = HashMap<String?, String?>()

        var call = retrofitInterface!!.getVideoListO(map)
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

        var VideoList = ArrayList<String>()
        var dateList = ArrayList<String>()

        lateinit var adapterI: PMenuVideoAdapterI

        recyclerView = findViewById(R.id.recyclerViewO)

        recyclerView.layoutManager = LinearLayoutManager(this@PMenuVideo)

        try {
            Log.e(TAG, "parseJson()함수 실행 성공!!! ")
            val jsonObject: JSONArray = JSONObject(json).getJSONArray("outList")

            Log.e(TAG, "jsonObject 객체 선언 실행 성공")

            for (i in 0 until jsonObject.length()) {
                val actor: JSONObject = jsonObject.getJSONObject(i)

                var h_odate = actor.getString("h_odate")
                var h_oroute = actor.getString("h_oroute")

                Log.e(TAG, "외출 영상경로 : $h_oroute 외출 날자 : $h_odate")

                VideoList.add(h_oroute)
                dateList.add(h_odate)

                Log.e(TAG, "add 성공")

                adapterI = PMenuVideoAdapterI(VideoList, dateList, this@PMenuVideo)

                recyclerView.adapter= adapterI

            }

            Log.d(TAG, "jsonObject : $jsonObject.toString()")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun jsgetvideoV() { // 이름과 디바이스 번호를 js에서 가져오는 코드
        Log.e(TAG, "jsgetvideV 함수 실행 성공 ")

        val map = HashMap<String?, String?>()

        var call = retrofitInterface!!.getVideoListI(map)
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
                        parseJson2(response.body().toString())
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
    private fun parseJson2(json: String) {
        lateinit var recyclerView : RecyclerView

        var VideoList = ArrayList<String>()
        var dateList = ArrayList<String>()

        lateinit var adapterO: PMenuVideoAdapterO

        recyclerView = findViewById(R.id.recyclerViewI)

        recyclerView.layoutManager = LinearLayoutManager(this@PMenuVideo)

        try {
            Log.e(TAG, "parseJson()함수 실행 성공!!! ")
            val jsonObject: JSONArray = JSONObject(json).getJSONArray("inList")

            Log.e(TAG, "jsonObject 객체 선언 실행 성공")

            for (i in 0 until jsonObject.length()) {
                val actor: JSONObject = jsonObject.getJSONObject(i)

                var h_idate = actor.getString("h_idate")
                var h_iroute = actor.getString("h_iroute")

                Log.e(TAG, "외출 영상경로 : $h_iroute 외출 날자 : $h_idate")

                VideoList.add(h_iroute)
                dateList.add(h_idate)

                Log.e(TAG, "add 성공")

                adapterO = PMenuVideoAdapterO(VideoList, dateList, this@PMenuVideo)

                recyclerView.adapter= adapterO

            }

            Log.d(TAG, "jsonObject : $jsonObject.toString()")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}