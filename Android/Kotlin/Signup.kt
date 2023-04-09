package com.example.ollie


import android.Manifest
import android.Manifest.permission
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.HashMap
import android.provider.Settings;
import android.telephony.TelephonyManager
import android.widget.*
import androidx.core.app.ActivityCompat
import com.example.ollie.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.p_menu_list_user_design.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.bluetooth.BluetoothAdapter
import android.content.ContentValues.TAG
import android.util.Log


class Signup : AppCompatActivity() {
    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1041"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        //spinner에 들어가는 데이터
        val div = arrayOf("보호자", "취약계층")

        //spinner 객체 생성
        val divSpinner: Spinner = findViewById(R.id.divSpinner)

        //Button 객체 생성
        val next : Button = findViewById(R.id.next)
        val agree : Button = findViewById(R.id.agree)

        //어댑터 생성
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line, div
        )

        //어댑터 설정
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        //스피너에 어댑터 적용
        divSpinner.adapter = adapter

        //버튼 클릭 시 ssaid, 이름, 전화번호, 비밀번호, 구분을 서버로 전달할 것
        next.setOnClickListener {
            val spinnerText = divSpinner.selectedItem.toString()
            handleSignupDialog(spinnerText)
        }
        agree.setOnClickListener{
            dialog()
        }

    }
    private fun dialog(){
        val check : CheckBox = findViewById(R.id.check)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("개인정보 수집 이용 동의 안내")
            .setView(layoutInflater.inflate(R.layout.custom_dialog, null))
            .setPositiveButton("동의",
                DialogInterface.OnClickListener { dialog, id ->
                    check.setChecked(true)
                })
        // 다이얼로그를 띄워주기
        builder.show()
    }

    private fun handleSignupDialog(spinnerText: String) {
        val check : CheckBox = findViewById(R.id.check)
        //고유 식별자 (SSAID)
        val ssaid : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        //객체 생성
        val name : EditText = findViewById(R.id.name)
        val passwd : EditText = findViewById(R.id.passwd)
        val phone : EditText = findViewById(R.id.phone)


        val map = HashMap<String?, String?>()

        //map에 <KEY, VALUE> 세팅하기
        map["ssaid"] = ssaid
        map["name"] = name.text.toString()
        map["phone"] = phone.text.toString()
        map["passwd"] = passwd.text.toString()
        if(spinnerText == "보호자"){
            map["div"] = "p"
        }
        else if(spinnerText == "취약계층"){
            map["div"] = "v"
        }


        val call = retrofitInterface!!.executeSignup(map)
        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {
                    Toast.makeText(
                        this@Signup,
                        "디바이스 연결을 위해 정보를 입력해 주세요.", Toast.LENGTH_LONG
                    ).show()
                } else if (response.code() == 400) {
                    Toast.makeText(
                        this@Signup,
                        "회원가입에 실패했습니다.", Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })

        if(spinnerText == "보호자" && check.isChecked()){
            //화면 이동
            val intent = Intent(this, SignupMatching::class.java)
            startActivity(intent)
        }
        else if(spinnerText == "취약계층" && check.isChecked()){
            //화면 이동
            val intent = Intent(this, VSignupDetail::class.java)
            startActivity(intent)
        }
        else{
            Toast.makeText(
                this@Signup,
                "개인정보이용을 동의하셔야 사용하실 수 있습니다.", Toast.LENGTH_LONG
            ).show()
            val thisIntnent = Intent(this, Signup::class.java)
            startActivity(thisIntnent)
        }
    }
}