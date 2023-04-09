package com.example.ollie
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.p_menu.*
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

class PMenuListUser : AppCompatActivity() {
    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1035"
    private val TAG = this.javaClass.simpleName

    //List변수 선언
    var nameList = ArrayList<String>()
    var divList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_menu_list_user)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        // PMenu 에서 전달한 디바이스번호 받기
        val deviceNum = intent.getStringExtra("device").toString()
        Log.e(ContentValues.TAG, "PMenu에서 받아온 디바이스번호 : $deviceNum")

        //디바이스 번호 보내기
        Dsend(deviceNum)

        Log.e(TAG, "디바이스 번호 넘어감")

        val back : Button = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val plus : Button = findViewById(R.id.plus)
        plus.setOnClickListener {
            val intent = Intent(this, PDevice::class.java)
            startActivity(intent)
        }

        val home : Button = findViewById(R.id.home)
        home.setOnClickListener {
            val intent = Intent(this, PHome::class.java)
            startActivity(intent)
        }

        val menu : Button = findViewById(R.id.menu)
        menu.setOnClickListener {
            val intent = Intent(this, PMenu::class.java)
            intent.putExtra("device",deviceNum)
            startActivity(intent)
        }
    }
    private fun Dsend(deviceNum: String){
        val map = HashMap<String?, String?>()

        map["device"] = deviceNum

        Log.e(TAG, "디바이스 전송1 ${deviceNum}")

        val call = retrofitInterface!!.executedevice(map)
        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    //getvuser()
                    getpuser(deviceNum)
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
    private fun getpuser(deviceNum: String){
        Log.e(TAG, "getpuser()함수 실행 성공")

        val map = HashMap<String?, String?>()
        map["device"] = deviceNum

        Log.e(TAG, "디바이스 전송 성공 p ${deviceNum}")

        var call = retrofitInterface!!.devicegetpuser(map)
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
                        parseJsonP(response.body().toString())
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

        var call2 = retrofitInterface!!.devicegetvuser(map)
        call2?.enqueue(object : Callback<String?> {
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                if(response.isSuccessful) {
                    Log.e(TAG, "취약계층 값 불러와짐 ")
                    // do something
                    if (response.body() != null) {
                        Log.e(TAG, "성공 : " + response.body())
                        parseJsonV(response.body().toString())
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

    private fun parseJsonP(json: String?) {

        lateinit var recyclerView : RecyclerView

        lateinit var adapter: UserAdapter

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this@PMenuListUser)

        try {

            Log.e(TAG, "parseJsonP함수 실행 성공!!! ")

            var div = "보호자"

            val jsonObject: JSONArray = JSONObject(json).getJSONArray("result")

            for (i in 0 until jsonObject.length()) {
                val actor: JSONObject = jsonObject.getJSONObject(i)
                var u_name = actor.getString("u_name")
                Log.e(TAG, "보호자 정보 가져옴")
                nameList.add(u_name)
                Log.e(TAG, "{$u_name, $div}")
                divList.add(div)
                Log.e(TAG, "{보호자 $nameList}")
                adapter = UserAdapter(nameList, divList,this@PMenuListUser)

                recyclerView.adapter = adapter
            }
            Log.d(TAG, "jsonObject : $jsonObject.toString()")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun parseJsonV(json: String?) {

        lateinit var recyclerView : RecyclerView

        lateinit var adapter: UserAdapter

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this@PMenuListUser)
        try {

            Log.e(TAG, "parseJsonV함수 실행 성공!!! ")

            var div = "취약계층"

            val jsonObject: JSONArray = JSONObject(json).getJSONArray("result")

            for (i in 0 until jsonObject.length()) {
                val actor: JSONObject = jsonObject.getJSONObject(i)
                var u_name = actor.getString("u_name")
                Log.e(TAG, "취약계층 정보 가져옴")
                nameList.add(u_name)
                Log.e(TAG, "{$u_name, $div}")
                divList.add(div)
                Log.e(TAG, "{취약계층 $nameList}")
                adapter = UserAdapter(nameList, divList, this@PMenuListUser)
            }
            Log.d(TAG, "jsonObject : $jsonObject.toString()")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}