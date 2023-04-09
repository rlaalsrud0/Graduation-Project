package com.example.ollie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.*
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

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams;
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList


class PDevice : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1030"

    private val TAG = this.javaClass.simpleName

    // recyclerView 객체 선언
    lateinit var recyclerView : RecyclerView
    var dNameList = ArrayList<String>()
    var doorList = ArrayList<Int>()
    lateinit var adapter: PDeviceAdapter

    // 취약계층이름, 디바이스번호 가 담긴 배열
    var deviceList = ArrayList<String>()
    var uNameList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_device)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        // 화면이 로드될 때, 나와 연결된 취약계층의 값을 가져오기
        send()

        // [추가] 버튼
        var plus : ImageButton = findViewById(R.id.plus)
        plus.setOnClickListener {
            val nextIntent = Intent(this, SignupMatching::class.java)
            startActivity(nextIntent)
        }

        //[마이페이지] 버튼
        val my : ImageButton = findViewById(R.id.my)
        my.setOnClickListener {
            val nextIntent = Intent(this, PMenuMypage::class.java)
            startActivity(nextIntent)
        }
    }


    private fun send() { // js로 ssaid 번호 보내주기
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
                    // js에서 취약계층 정보 가져오기
                    jsgetinfo()
                } else if (response.code() == 400) {
                    Log.e(TAG, "js로 ssaid 번호 보내주기 실패 ")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "실패")
            }
        })
    }

    private fun jsgetinfo() { // 값을 js에서 가져오는 코드

        Log.e(TAG, "jsgetinfo()함수 실행 성공 ")

        val map = HashMap<String?, String?>()
        var call = retrofitInterface!!.devicegetdata(map)
        call?.enqueue(object : Callback<String?> {
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                if(response.isSuccessful) {

                    Log.e(TAG, "객체 선언 후 성공성공 ")
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

            recyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this@PDevice)


            val actor: JSONObject = jsonObject.getJSONObject(0)
            var u_name = actor.getString("u_name")
            var device = actor.getString("device")
            Log.e(TAG, "파싱한 취약계층 이름,디바이스 : $u_name, $device")

            deviceList.add(device)
            Log.e(TAG, "일단 deviceList에 디바이스 번호 추가  : $deviceList")

            var deviceList = deviceList.distinct() // 리스트 안에 있는 디바이스번호 중복값 제거
            Log.e(TAG, "deviceList  : $deviceList")

            for (i in 0 until deviceList.size) {
                var mdevice = deviceList.get(i)
                dNameList.add(mdevice)
                Log.e(TAG, "dNameList  : $dNameList")
                doorList.add(R.drawable.device_address)
            }

            adapter = PDeviceAdapter(dNameList, doorList, this@PDevice)
            recyclerView.adapter = adapter

            // 디바이스 번호 클릭 시
            adapter.setOnItemClickListener(object : PDeviceAdapter.OnItemClickListener {
                override fun onItemClicked(position: Int) {
                    var d_num: String = dNameList.get(position) // 클릭한 디바이스 번호
                    // 디바이스 번호 PHome.kt 로 보내기, 화면전환
                    val intent = Intent(this@PDevice, PHome::class.java)
                    intent.putExtra("device", d_num)
                    startActivity(intent)
                    Log.e(TAG, "d_num  : $d_num")
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}