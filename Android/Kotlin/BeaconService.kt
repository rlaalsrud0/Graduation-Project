package com.example.ollie


import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.ollie.BObject.CHANNEL_B_ID
import com.example.ollie.BObject.CHANNEL_B_NAME
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.random.Random

class BeaconService : Service() {

    var beacon: Beacon? = null
    var beaconParser: BeaconParser? = null
    var beaconTransmitter: BeaconTransmitter? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val notification = NotificationCompat.Builder(this, Constants.CHANNEL_ID_Help)
            .setContentTitle("올리사랑")
            .setContentText("백그라운드에서 앱이 실행중입니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setSmallIcon( R.drawable.ollie)
            .build()
        startForeground(3, notification)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val beaconChannel = NotificationChannel(
                CHANNEL_B_ID, "My Beacon Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            manager.createNotificationChannel(beaconChannel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand실행")
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(mBluetoothAdapter==null){
            Log.d(TAG, "블루투스를 지원하지 않는 기기입니다.")
        } else{
            processCommand()
        }
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun processCommand(){
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        thread(start = true) {
            while(true){
                try {
                    if(mBluetoothAdapter.isEnabled() == true){
                        val UUID = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
                        val major = "1"
                        val minor = "999"
                        BeaconSendStart(UUID, major, minor)
                        Log.d(TAG, "BEACON SATART")
                    } else if(mBluetoothAdapter.isEnabled() == false) {
                        onBluetoothNotification()
                    }
                }catch (e: Exception) { }
            }
        }
    }

    //TODO [실시간 비콘 신호 활성 시작]
    private fun BeaconSendStart(UUID: String, MAJOR: String, MINOR: String) {
        Log.d("---", "---")
        Log.d("//===========//", "================================================")
        Log.d(
            "", """
     
                [BeaconSend > BeaconScanStart() 메소드 : 실시간 비콘 신호 활성 수행]
                """
        )
        Log.d("//===========//", "================================================")
        Log.d("---", "---")
        try {
            //TODO [beacon 객체 설정 실시]
            beacon = Beacon.Builder()
                .setId1(UUID) //TODO [UUID 지정]
                .setId2(MAJOR) //TODO [major 지정]
                .setId3(MINOR) //TODO [minor 지정]
                .setManufacturer(0x004c) //TODO [제조사 지정 : IOS 호환]
                //.setManufacturer(0x0118) // [제조사 지정]
                .setTxPower(-59) //TODO [신호 세기]
                //.setTxPower(59) //[신호 세기]
                .setDataFields(Arrays.asList(*arrayOf(0L))) //TODO [레이아웃 필드]
                .build()

            //TODO [레이아웃 지정 : IOS 호환 (ibeacon)]
            beaconParser =
                BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
            //beaconParser = new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");

            //TODO [비콘 신호 활성 상태 확인 실시]
            beaconTransmitter = BeaconTransmitter(applicationContext, beaconParser)
            beaconTransmitter!!.startAdvertising(beacon, object : AdvertiseCallback() {
                override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                    super.onStartSuccess(settingsInEffect)
                    Log.d("---", "---")
                    Log.w("//===========//", "================================================")
                    Log.d(
                        "", """
     
                        [BeaconSend > BeaconScanStart() 메소드 : 실시간 비콘 신호 활성 성공]
                        """
                    )
                    Log.d("", "\n[UUID : $UUID]")
                    Log.d("", "\n[MAJOR : $MAJOR]")
                    Log.d("", "\n[MINOR : $MINOR]")
                    Log.d(
                        "", """
     
                    [시작 시간 : $nowTime]
                    """
                    )
                    Log.w("//===========//", "================================================")
                    Log.d("---", "---")
                }

                override fun onStartFailure(errorCode: Int) {
                    super.onStartFailure(errorCode)
                    Log.d("---", "---")
                    Log.e("//===========//", "================================================")
                    Log.d(
                        "", """
     
                    [BeaconSend > BeaconScanStart() 메소드 : 실시간 비콘 신호 활성 실패]
                    """
                    )
                    Log.d("", "\n[UUID : $UUID]")
                    Log.d("", "\n[MAJOR : $MAJOR]")
                    Log.d("", "\n[MINOR : $MINOR]")
                    Log.d("", "\n[Error : $errorCode]")
                    Log.e("//===========//", "================================================")
                    Log.d("---", "---")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        // [현재 시간 알아오는 메소드]
        val nowTime: String
            get() {
                val time = System.currentTimeMillis()
                val dayTime =
                    SimpleDateFormat("yyyy-MM-dd kk:mm:ss")
                return dayTime.format(Date(time))
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onBluetoothNotification() {
        val go_ble = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        go_ble.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        var title = "[블루투스 기능 활성 여부 확인]"
        var message = "블루투스 기능이 비활성화 상태입니다.\n블루투스 기능을 활성화해야 정상 기능 사용이 가능합니다."

        val pendingIntent = PendingIntent.getActivity(this, 0, go_ble, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, CHANNEL_B_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ollie)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(CHANNEL_B_NAME, notification)
    }
}



object BObject {
    const val CHANNEL_B_ID = "channel_id"
    const val CHANNEL_B_NAME = 456
}