package com.example.ollie

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.HashMap


class PMenuDeviceDelete : AppCompatActivity() {
    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1033"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_menu_device_delete)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        // PMenuDevicePasswd 에서 전달한 디바이스번호 받기
        val deviceNum = intent.getStringExtra("device").toString()
        Log.e(TAG, "PMenuDevicePasswd에서 받아온 디바이스번호 : $deviceNum")

        val back : Button = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        val yes : Button = findViewById(R.id.yes)
        yes.setOnClickListener {
            send(deviceNum)
            val intent = Intent(this, PDevice::class.java)
            startActivity(intent)
        }
    }

    private fun send(deviceNum: String) { //js에 ssaid, device번호 전송
        //고유 식별자
        val SSAID : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)

        val map = HashMap<String?, String?>()

        map["ssaid"] = SSAID
        map["device"] = deviceNum

        val call = retrofitInterface!!.executeDeviceNumSend(map)

        val nextintent = Intent(this, PDevice::class.java)

        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    // 성공메세지 띄우기
                    Toast.makeText(
                        this@PMenuDeviceDelete,
                        "디바이스가 삭제되었습니다.", Toast.LENGTH_LONG
                    ).show()
                    startActivity(nextintent)
                } else if (response.code() == 400) {
                    Toast.makeText(
                        this@PMenuDeviceDelete,
                        "디바이스 삭제에 실패했습니다.", Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }
}