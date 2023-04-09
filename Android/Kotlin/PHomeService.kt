package com.example.ollie

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
import kotlin.concurrent.thread
import android.app.Activity

import android.app.ActivityManager




class PHomeService : Service() {

    val CHANNEL_ID_phome = "ForegroundChannel" // 서비스 채널 아이디

    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1051"

    private val TAG = this.javaClass.simpleName

    var deviceNum: String? = null
    var success: String? = null

    override fun onCreate() { // 서비스가 처음 생성될 때 수행되는 메서드
        super.onCreate()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        // 오레오 부터는 notification channel을 설정해 주어야 함
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val name = "여기에 들어가는게 뭐야?"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val serviceChannel = NotificationChannel(CHANNEL_ID_phome, name,importance)

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        } // 안드로이드 버전 때문에 넣음

        runBackGround()

    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    // Started Service에서 서비스 시작시 호출
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "PHomeService의 onStartCommand() 함수 실행 성공! ")

        // 해당 Notification을 눌렀을 때 PHome 액티비티 띄우기
        val notificationIntent = Intent(this, PHomeService::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0)

        // 해당 Notification 디자인
        val notification = NotificationCompat.Builder(this,CHANNEL_ID_phome)
            .setContentTitle("올리사랑")
            .setContentText("백그라운드에서 앱이 실행중입니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setContentIntent(pendingIntent)
            .setSmallIcon( R.drawable.ollie)
            .build()

        startForeground(1, notification)

        val runcheck = isServiceRunningCheck()
        Log.e(TAG, "서비스 실행중인지 체크 : $runcheck")

        return super.onStartCommand(intent, flags, startId)
    }

    fun runBackGround() {
        thread(start = true) {
            while (true) {
                Thread.sleep(800)
                Log.e("서비스","서비스 실행중")
                jsOutGet()

            }
        }
    }

    fun isServiceRunningCheck(): Boolean {
        val manager = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if ("com.example.ollie.PHomeService" == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }


    // 외출일 때, 값을 js에서 가져오는 코드
    private fun jsOutGet() {
        Log.e(TAG, "jsOutGet() 함수 실행 성공 ")
        val map = HashMap<String?, String?>()
        var call_out = retrofitInterface!!.getOut(map)
        call_out?.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    if (response.body() != null) {
                        Log.e(TAG, "js에서 외출 값 받아오기 : " + response.body())
                        parseJsonOut(response.body().toString())

                    }else {
                        Log.e(TAG, "실패 : " + response.errorBody()!!.string())
                    } } }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            } })
    }

    //Json 파싱
    // 외출일 때, js에서 취약계층 ssaid, 이름, 시간, 영상  받아서 처리
    private fun parseJsonOut(json: String?) {
        Log.e(TAG, "parseJson()함수 외출 실행 성공!!! ")
        try {
            val jsonObject: JSONArray = JSONObject(json).getJSONArray("result")
            val actor_div: JSONObject = jsonObject.getJSONObject(0)
            val actor: JSONObject = jsonObject.getJSONObject(1)

            val div = actor_div.getString("div")
            Log.e(TAG, "div,외출: $div")
            if (div == "외출하지 않음") {
                Log.e(TAG, "외출하지 않음")
                jsInGet()
            } else if(div == "외출") {
                var h_num = actor.getString("u_num") // 취약계층 ssaid
                var v_name = actor.getString("u_name") // 취약계층 이름
                var h_odate = actor.getString("h_odate") // 취약계층 외출 날짜-시간
                var h_oroute = actor.getString("h_oroute") // 취약계층 외출 영상

                Log.e(TAG, "외출 값 받아옴, $h_num, $v_name, $h_odate,$h_oroute")

                // 홈화면에 추가할 외출정보리스트, 서비스에서 PHome 액티비티로 보내기
                val intent = Intent("intent_PHomeList_test")
                intent.putExtra("div", div)
                intent.putExtra("h_num", h_num)
                intent.putExtra("v_name", v_name)
                intent.putExtra("h_odate", h_odate)
                intent.putExtra("h_oroute", h_oroute)
                LocalBroadcastManager.getInstance(this@PHomeService).sendBroadcast(intent)
                Log.e(TAG, "PHome로 값 보내기 성공! , $intent")

            } else {
                Log.e(TAG, "에러")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    // 귀가일 때, 값을 js에서 가져오는 코드
    private fun jsInGet() {
        Log.e(TAG, "귀가, jsInGet() 함수 실행 성공 ")
        val map = HashMap<String?, String?>()
        var call = retrofitInterface!!.getIn(map)
        call?.enqueue(object : Callback<String?> { override fun onResponse(call: Call<String?>,response: Response<String?>,) {
            if(response.isSuccessful) {
                if (response.body() != null) {
                    Log.e(TAG, "귀가성공 : " + response.body())
                    parseJsonIn(response.body().toString())
                }else {
                    Log.e(TAG, "실패 : " + response.errorBody()!!.string())
                } } }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            } })
    }

    //Json 파싱
    // 귀가일 때, js에서 취약계층 ssaid, 이름, 시간 받아서 처리
    private fun parseJsonIn(json: String?) {
        Log.e(TAG, "귀가, parseJsonIn()함수 실행 성공!!! ")
        try {
            val jsonObject: JSONArray = JSONObject(json).getJSONArray("result")
            val actor_div: JSONObject = jsonObject.getJSONObject(0)
            val actor: JSONObject = jsonObject.getJSONObject(1)

            val div = actor_div.getString("div") // home 테이블 번호
            Log.e(TAG, "div,귀가: $div")
            if (div == "귀가하지 않음") {
                Log.e(TAG, "귀가하지 않음")
                jsNoInGet()

            } else if (div == "귀가") {
                var h_num = actor.getString("u_num") // 취약계층 ssaid
                var v_name = actor.getString("u_name") // 취약계층 이름
                var h_idate = actor.getString("h_idate") // 취약계층 외출 날짜-시간
                var h_iroute = actor.getString("h_iroute") // 취약계층 외출 영상

                // 홈화면에 추가할 외출정보리스트, 서비스에서 PHome 액티비티로 보내기
                val intent = Intent("intent_PHomeList_test")
                intent.putExtra("div", div)
                intent.putExtra("h_num", h_num)
                intent.putExtra("v_name", v_name)
                intent.putExtra("h_idate", h_idate)
                intent.putExtra("h_iroute", h_iroute)
                LocalBroadcastManager.getInstance(this@PHomeService).sendBroadcast(intent)
                Log.e(TAG, "PHome로 귀가 값 보내기 성공! , $intent")

            } else {
                Log.e(TAG, "에러")
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    // 귀가X 일 때, 값을 js에서 가져오는 코드
    private fun jsNoInGet() {
        Log.e(TAG, "미귀가, jsNoInGet() 함수 실행 성공 ")
        val map = HashMap<String?, String?>()
        var call = retrofitInterface!!.getNoIn(map)
        call?.enqueue(object : Callback<String?> { override fun onResponse(call: Call<String?>,response: Response<String?>,) {
            if(response.isSuccessful) {
                if (response.body() != null) {
                    Log.e(TAG, "미귀가성공 : " + response.body())
                    parseJsonNoIn(response.body().toString())
                }else {
                    Log.e(TAG, "실패 : " + response.errorBody()!!.string())
                } } }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            } })
    }

    //Json 파싱
    // 귀가X 일 때, js에서 취약계층 ssaid, 이름, 시간 받아서 처리
    private fun parseJsonNoIn(json: String?) {
        Log.e(TAG, "미귀가, parseJsonNoIn()함수 실행 성공!!! ")
        try {
            val jsonObject: JSONArray = JSONObject(json).getJSONArray("result")
            val actor_div: JSONObject = jsonObject.getJSONObject(0)
            val actor: JSONObject = jsonObject.getJSONObject(1)

            val div = actor_div.getString("div")
            Log.e(TAG, "미귀가 시 : $div")
            if (div == "아직 시간 전") {
                Log.e(TAG, "미귀가, 아직 시간 전")

            } else if (div == "귀가하지 않았습니다.") {
                var h_num = actor.getString("u_num") // 취약계층 ssaid
                var v_name = actor.getString("u_name") // 취약계층 이름
                var t_time = actor.getString("t_time") // 설정한 귀가시간

                // 홈화면에 추가할 미귀가 정보리스트, 서비스에서 PHome 액티비티로 보내기
                val intent = Intent("intent_PHomeList_test")
                intent.putExtra("div", "귀가X")
                intent.putExtra("h_num", h_num)
                intent.putExtra("v_name", v_name)
                intent.putExtra("t_time", t_time)
                LocalBroadcastManager.getInstance(this@PHomeService).sendBroadcast(intent)
                Log.e(TAG, "PHome로 미귀가 값 보내기 성공! , $intent")

            } else {
                Log.e(TAG, "에러")
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}