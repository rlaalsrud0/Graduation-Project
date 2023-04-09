package com.example.ollie

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.ollie.Constants1.CHANNEL_ID
import com.example.ollie.Constants1.CHANNEL_NAME
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
import kotlin.random.Random

class PasswdCheck : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1029"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.passwd_check)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)


        val check : Button = findViewById(R.id.check)
        check.setOnClickListener {
            send()
        }
    }
    private fun send() { // js로 ssaid 번호 보내주기
        //고유 식별자
        val SSAID : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)
        val name : EditText = findViewById(R.id.name)
        val phone : EditText = findViewById(R.id.phone)

        val map = HashMap<String?, String?>()

        map["ssaid"] = SSAID
        map["name"] = name.text.toString()
        map["phone"] = phone.text.toString()

        val call = retrofitInterface!!.executepasswd(map)

        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    // 성공메세지 띄우기
                    Log.e(TAG, "jsgetpasswd 함수실행 1")
                    jsgetpasswd()
                } else if (response.code() == 400) {
                    Toast.makeText(
                        this@PasswdCheck,
                        "새로운 비밀번호 발급에 실패했습니다.", Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }
    private fun jsgetpasswd() { // 이름과 디바이스 번호를 js에서 가져오는 코드
        Log.e(TAG, "jsgetpasswd함수 실행2")

        val SSAID : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)

        val map = HashMap<String?, String?>()
        map["ssaid"] = SSAID

        var call = retrofitInterface!!.getPasswdCheck(map)
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
                        parseJson((response.body().toString()))
                        finish()
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
        val passwdIntent = Intent(this, PasswdCheck::class.java)
        try {
            Log.e(TAG, "parseJson()함수 실행")

            val jsonObject: String = JSONObject(json).getString("result")
            val jsonObject2: String = JSONObject(json).getString("passwd")
            var result = jsonObject

            Log.e(TAG, "$result")

            if (result.equals("true")) {
                Log.e(TAG, "새로운 비밀번호 : $jsonObject2")
                Toast.makeText(
                    this@PasswdCheck, "비밀번호를 알림으로 전송했습니다.", Toast.LENGTH_LONG
                ).show()
                val title = "비밀번호 발급"
                val message = jsonObject2
                val notificationManager = getSystemService(NOTIFICATION_SERVICE)
                        as NotificationManager
                val notificationID = Random.nextInt()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel(notificationManager)
                }

                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ollie)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(notificationID, notification)
                Log.e(TAG, "발송 성공")
            } else if (result.equals("false")) {
                Log.e(TAG, "message.equals(false)")
                // js에서 받은 값이 false 이면, 비밀번호 다시 입력하게 하기
                startActivity(passwdIntent)
                Toast.makeText(
                    this@PasswdCheck, "이름이나 전화번호가 틀렸습니다. \n다시 입력하세요.", Toast.LENGTH_LONG
                ).show()
            }
        }catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
private fun createNotificationChannel(notificationManager: NotificationManager) {
    val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
        description = "Channel Description"
        enableLights(true)
    }
    notificationManager.createNotificationChannel(channel)
}

object Constants1 {
    const val CHANNEL_ID = "channel_id"
    const val CHANNEL_NAME = "channel_name"
}