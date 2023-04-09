package com.example.ollie

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ollie.Constants1.CHANNEL_ID
import com.example.ollie.Constants1.CHANNEL_NAME
import kotlinx.android.synthetic.main.p_home.*
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
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.HashMap
import kotlin.random.Random

class PHome : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    val currentTime : Long = System.currentTimeMillis() // ms로 반환
    val time = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(currentTime)

    private val TAG = this.javaClass.simpleName

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1051"

    var list = ArrayList<PHomeModel>()
    var h_div: String? = ""

    var testList = ArrayList<String>()

    var deviceNum: String? = null

    var h_num: String? = null
    var v_name: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_home)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        // [디바이스 계정] 버튼
        var plus : Button = findViewById(R.id.plus)
        plus.setOnClickListener {
            val nextIntent = Intent(this, PDevice::class.java)
            startActivity(nextIntent)
        }

        //[홈] 버튼
        val home : Button = findViewById(R.id.home)
        home.setOnClickListener {
            val nextIntent = Intent(this, PHome::class.java)
            startActivity(nextIntent)
        }

        //[메뉴] 버튼
        val menu : Button = findViewById(R.id.menu)
        menu.setOnClickListener {
            val nextIntent = Intent(this, PMenu::class.java)
            nextIntent.putExtra("device",deviceNum)
            startActivity(nextIntent)
        }

        // PDevice에서 전달한 디바이스번호 받기
        deviceNum = intent.getStringExtra("device")
        Log.e(TAG, "PDevice에서 받아온 디바이스번호 : $deviceNum")

        SendToJs() // PHome.js 로 내 ssaid,deviceNum(디바이스번호) 보내기

        val passedIntent = intent
        processIntent(passedIntent)
    }

    // PHome.js 로 내 ssaid,deviceNum(디바이스번호) 보내기
    private fun SendToJs() {

        // 내 ssaid
        val SSAID : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)

        val map = HashMap<String?, String?>()
        map["ssaid"] = SSAID
        map["device"] = deviceNum

        val call = retrofitInterface!!.sendHome(map)
        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.code() == 200) {
                    Log.e(TAG, "PHome.js로 디바이스번호 보내기 성공!!, " + response.body())

                    // PHomeService 로 데이터 전달하기
                    val serviceintent = Intent(applicationContext, PHomeService::class.java)
                    serviceintent.putExtra("SendToJs", "성공")
                    ContextCompat.startForegroundService(applicationContext, serviceintent)

                } else if (response.code() == 400) {
                    Log.e(TAG, "PHome.js로 디바이스번호 보내기 **실패**")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }

    // 서비스가 이미 실행되고 있으면 onNewIntent
    override fun onNewIntent(intent: Intent?) {
        processIntent(intent)
        super.onNewIntent(intent)
    }

    private fun processIntent(intent: Intent?) {

        if (intent != null) {
            Log.e(TAG,"intent?? : $intent")

            // PHomeService 에서 외출정보리스트 받기
            getPhomeservice?.let {
                LocalBroadcastManager.getInstance(this).registerReceiver(
                    it, IntentFilter("intent_PHomeList_test"))
            }
        }
    }

    private fun meNotification(title: String?, message: String?){ // 나에게 알림 띄우기
        val title = title
        val message = message

        // NotificationManager 객체 생성
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        // 알림을 구분하는 식별자 id는 랜덤으로 주게하기
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title) // 상단바 알림 제목
            .setContentText(message) // 상단바 알림 내용
            .setSmallIcon(R.drawable.ollie) // 아이콘
            .setAutoCancel(true) // 터치하면 자동으로 지워지도록 설정하는 것
            .setContentIntent(pendingIntent) // 실행할 작업이 담긴 PendingIntent
            .build()

        // notify(int id, Notification notification)
        // => 알림을 발생시킴, id는 알림을 구분하는 식별자,
        // 존재하는 알림의 id를 사용하면 알림이 update됨
        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Channel Description"
            enableLights(true)

        }
        notificationManager.createNotificationChannel(channel)
    }

    private var getPhomeservice: BroadcastReceiver? = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context?, intent: Intent?) {

            var div = intent?.getStringExtra("div")
            Log.e(TAG, "PHome 으로 div 받음!! = $div")

            if (div == "외출") {
                h_num = intent?.getStringExtra("h_num")
                v_name = intent?.getStringExtra("v_name")
                var h_odate = intent?.getStringExtra("h_odate")
                var h_oroute = intent?.getStringExtra("h_oroute")
                Log.e(TAG, "PHome 으로 h_num 받음!! = $h_num, $v_name, $h_odate, $h_oroute ")

                list.add(PHomeModel(PHomeModel.P_HOME_INOUT_VIEW,
                    h_num,deviceNum,h_odate,
                    v_name+"님이 외출하셨습니다.",h_oroute,null))

                meNotification(v_name+"님","외출 알림" + h_odate)

            } else if (div == "귀가") {
                h_num = intent?.getStringExtra("h_num")
                v_name = intent?.getStringExtra("v_name")
                var h_idate = intent?.getStringExtra("h_idate")
                var h_iroute = intent?.getStringExtra("h_iroute")
                Log.e(TAG, "PHome 으로 h_num 받음!! = $h_num, $v_name, $h_idate, $h_iroute ")

                list.add(PHomeModel(PHomeModel.P_HOME_INOUT_VIEW,
                    h_num,deviceNum,h_idate,
                    v_name+"님이 귀가하셨습니다.",h_iroute,null))
                Log.e(TAG, "귀가 귀가 h_num : $h_num")

                meNotification(v_name+"님","귀가 알림" + h_idate)
            } else if (div == "귀가X") {
                h_num = intent?.getStringExtra("h_num")
                v_name = intent?.getStringExtra("v_name")
                var t_time = intent?.getStringExtra("t_time")

                Log.e(TAG, "PHome 으로 h_num 받음!! = $h_num, $v_name, $t_time ")

                list.add(PHomeModel(PHomeModel.P_HOME_NOIN_VIEW,
                    h_num, deviceNum, time,
                    v_name + "님이 귀가하지 않았습니다!", null, null))

                Log.e(TAG, "귀가X 귀가X h_num : $h_num")
                meNotification(v_name+"님","**귀가X 비상**")
            } else {
                Log.e(TAG, "이건 그냥 else")
            }

            val adpater = MultiViewTypeAdapter(list,this@PHome)
            val recycler_view = findViewById<RecyclerView>(R.id.recycler_view)
            recycler_view.layoutManager = LinearLayoutManager(this@PHome, RecyclerView.VERTICAL, false)
            recycler_view.adapter = adpater

            adpater.notifyItemInserted(list.size)

            adpater.setOnItemClickListener(object : MultiViewTypeAdapter.OnItemClickListener {
                override fun onLocClicked(position: Int) { // 위치 버튼 클릭
                    // 취약계층 이름 전달
                    val intent = Intent(this@PHome, PMenuLocation::class.java)
                    intent.putExtra("vname", v_name)
                    startActivity(intent)
                    Log.e(TAG, "v_name  : $v_name")
                }
                override fun onOutClicked(position: Int) { // 외출정보 버튼 클릭
                    // 취약계층 ssaid 전달
                    val intent = Intent(this@PHome, PMenuOut::class.java)
                    intent.putExtra("ssaid", h_num)
                    startActivity(intent)
                    Log.e(TAG, "h_num  : $h_num")
                }
                override fun onCheckClicked(position: Int) { // 대신확인 버튼 클릭
                    Log.e(TAG, "대신확인 버튼 클릭")
                    list.add(PHomeModel(PHomeModel.P_HOME_NOTIFY_CHECK_VIEW,
                        h_num, deviceNum, time,
                        v_name + "님이 대신확인 되었습니다!", null, null))

                    adpater.notifyItemInserted(list.size)

                    jsPostCheck(h_num)

                }
                override fun onNotifyClicked(position: Int) { // 신고 버튼 클릭
                    Log.e(TAG, "신고 버튼 클릭")
                    list.add(PHomeModel(PHomeModel.P_HOME_NOTIFY_CHECK_VIEW, h_num, deviceNum, time,
                        v_name + "님이 신고되었습니다!", null, null))

                    adpater.notifyItemInserted(list.size)
                    val intent = Intent(this@PHome, PNotify::class.java)
                    intent.putExtra("ssaid", h_num)
                    startActivity(intent)
                    Log.e(TAG, "v_name  : $v_name")
                }
                override fun onVideoClicked(position: Int) { // 비디오뷰 클릭
                    // 취약계층 이름 전달
                    val intent = Intent(this@PHome, PMenuVideo::class.java)
                    intent.putExtra("vname", v_name)
                    startActivity(intent)
                    Log.e(TAG, "v_name  : $v_name")
                }
            })
        }
    }

    // js로 대신확인 보내기
    private fun jsPostCheck(h_num: String?) {
        val h_num: String? = h_num
        Log.e(TAG, "대신확인 된 취약계층의 h_seq : $h_num")

        val map = HashMap<String?, String?>()
        map["h_num"] = h_num

        val call = retrofitInterface!!.getNoInY(map)
        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>,
            ) {
                if (response.code() == 200) {
                    Log.e(TAG, "성공 : " + response.code())

                } else if (response.code() == 400) {
                    Log.e(TAG, "실패 : " + response.errorBody()!!.string())
                }
            }
            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })
    }
}