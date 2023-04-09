package com.example.ollie


import android.bluetooth.BluetoothAdapter
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.HashMap

class VHelp : AppCompatActivity() {
    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1044"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.v_help)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        //블루트스 권한 요청
        var bluetoothAdapter: BluetoothAdapter? = null
        val intent: Intent
        if(bluetoothAdapter?.isEnabled() == true){
            // 블루투스 관련 실행 진행
        } else {
            // 블루투스 활성화 하도록
            intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, 1)
        }
        //비콘 서비스 스타트
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(bluetoothAdapter==null){
                stopService(Intent(this,BeaconService::class.java))
                Toast.makeText(this@VHelp, "블루투스를 지원하지 않는 기기이므로 \n서비스를 사용할 수 없습니다.", Toast.LENGTH_LONG
                ).show()
                Log.d(TAG, "블루투스를 지원하지 않는 기기입니다.")
            } else{
                startForegroundService(Intent(this, BeaconService::class.java))
            }
        } else {
            startService(Intent(this, BeaconService::class.java))
        }

        val my : ImageButton = findViewById(R.id.my)
        my.setOnClickListener {
            val nextIntent = Intent(this, VMypage::class.java)
            startActivity(nextIntent)
        }

        val help : ImageButton = findViewById(R.id.help)
        help.setOnClickListener {
            //js와 연결
            send()
        }

        val serviceIntent = Intent(this, PHelp::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun send() { // js로 ssiad 번호 보내주기
        //고유 식별자
        val SSAID : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)
        Log.e(TAG, SSAID)

        val map = HashMap<String?, String?>()

        map["ssaid"] = SSAID

        val call = retrofitInterface!!.executeSend(map)

        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    // 성공메세지 띄우기
                    Log.e(TAG, "request 테이블에 insert 성공")
                } else if (response.code() == 400) {
                    Log.e(TAG, "request 테이블에 insert 실패")
                }
            }
            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }
}