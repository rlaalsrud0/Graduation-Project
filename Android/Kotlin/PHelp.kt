package com.example.ollie

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
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
import java.lang.Exception
import kotlin.concurrent.thread
import android.app.NotificationManager

import android.app.NotificationChannel
import androidx.core.content.ContextCompat.getSystemService
import com.example.ollie.Constants.CHANNEL_ID_Help
import com.example.ollie.Constants.CHANNEL_NAME_Help


class PHelp : Service() {
    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1044"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        val notification = NotificationCompat.Builder(this,CHANNEL_ID_Help)
            .setContentTitle("올리사랑")
            .setContentText("백그라운드에서 앱이 실행중입니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setSmallIcon( R.drawable.ollie)
            .build()
        startForeground(2, notification)
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent == null){
            return START_REDELIVER_INTENT
        }else{
            processCommand(intent, flags, startId)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun processCommand(intent: Intent, flags: Int, startId: Int) {
        thread(start = true){
            while (true) {
                try {
                    jsgetinfo()
                    Log.d(TAG, "jsgetinfo() 호출")
                    Thread.sleep(5000)
                } catch (e: Exception) { }
                Log.d(TAG, "Waiting :  sec")
            }
        }
    }

    private fun jsgetinfo() { //js에서 값 가져오기
        Log.d(TAG, "jsgetinfo() 실행")

        var call = retrofitInterface!!.getdata
        call?.enqueue(object : Callback<String?> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                Log.d(TAG, "onResponse 아래")
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseJson(json: String) { //json 파싱
        //고유 식별자
        val SSAID : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)

        try {
            val jsonObject: JSONArray = JSONObject(json).getJSONArray("pList")
            Log.e(TAG, jsonObject.toString())

            for (i in 0 until jsonObject.length()) {
                val actor: JSONObject = jsonObject.getJSONObject(i)
                var pnum = actor.getString("m_pnum")

                //pnum과 동일한 ssaid를 가진 사람들에게 알림 보내기
                if(pnum == SSAID){
                    Log.e(TAG, "나는 보호자")
                    showNotification()
                }
            }
            Log.d(TAG, "jsonObject : $jsonObject.toString()")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(
                CHANNEL_ID_Help, "My Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            manager.createNotificationChannel(serviceChannel)
        }
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(){
        Log.d(TAG, "showNotification() 호출")

        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID_Help)
            .setSmallIcon( R.drawable.ollie)
            .setContentTitle("Title")
            .setContentText("취약계층 구조 요청")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .build()

        startForeground(CHANNEL_NAME_Help, notification)
    }
}

object Constants {
    const val CHANNEL_ID_Help = "channel_id"
    const val CHANNEL_NAME_Help = 123
}