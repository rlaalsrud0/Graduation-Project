package com.example.ollie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

class MypagePasswd : AppCompatActivity() {

    private var retrofit: Retrofit? = null
    private var retrofitInterface: RetrofitInterface? = null

    // url로 파라미터 요청하기
    private val BASE_URL = "ADDRESS:1028"

    private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mypage_passwd)

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInterface = retrofit?.create(RetrofitInterface::class.java)

        //[이전] 버튼
        val back : Button = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        // [비밀번호 찾기] 버튼
        val find_passwd : Button = findViewById(R.id.find_passwd)
        find_passwd.setOnClickListener {
            val nextIntent = Intent(this, PasswdCheck::class.java)
            startActivity(nextIntent)
        }

        // [입력] 버튼
        val input : Button = findViewById(R.id.input)
        input.setOnClickListener {

            passwdCheck() // 입력한 비밀번호,ssaid를 js로 보내서 체크
        }

    }

    private fun passwdCheck() { // js로 ssaid, passwd 보내주기
        //고유 식별자
        val PSSAID : String = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)

        val map = HashMap<String?, String?>()
        //객체 생성
        val passwd : EditText = findViewById(R.id.passwd)

        map["ssaid"] = PSSAID
        map["passwd"] = passwd.text.toString()

        val call = retrofitInterface!!.passwdcheck(map)
        call?.enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>,
                response: Response<Void?>
            ) {
                if (response.code() == 200) {

                    // js에서 결과 받기
                    jsgetinfo()
                    Log.e(TAG, "비밀번호 값 일치")
                } else if (response.code() == 400) {
                    Toast.makeText(
                        this@MypagePasswd,
                        "비밀번호가 틀렸습니다. \n" +
                                "다시 입력하세요.", Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e(TAG, "에러 : " + t.message)
            }
        })

    }

    // js에서 결과 받기
    private fun jsgetinfo() { // 값을 js에서 가져오는 코드

        Log.e(TAG, "jsgetinfo()함수 실행 성공 ")

        val map = HashMap<String?, String?>()

        var call = retrofitInterface!!.passwdresult(map)
        call?.enqueue(object : Callback<String?> {
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                if(response.isSuccessful) {
                    // do something
                    if (response.body() != null) {
                        Log.e(TAG, "js에서 결과 받기 성공 : " + response.body())
                        parseJson(response.body().toString())
                    }else {
                        try {
                            Log.e(TAG, "js에서 결과 받기 실패 : " + response.errorBody()!!.string())
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

            val jsonBoolean = JSONObject(json).getBoolean("result")
            var pwresult = jsonBoolean.toString()
            Log.e(TAG, "pwresult : $pwresult")

            // PMenuMypage 에서 정보 받기
            // [본인 정보 수정] 버튼 클릭 시
            val pupdate : String? = intent.getStringExtra("pupdate")
            Log.e(TAG, "pupdate : $pupdate")
            // [회원탈퇴] 버튼 클릭 시
            val pdelete : String? = intent.getStringExtra("pdelete")
            Log.e(TAG, "pdelete : $pdelete")
            val pupdateIntent = Intent(this, PMenuMypageUpdate::class.java)
            val pdeleteIntent = Intent(this, MypageDelete::class.java)

            // VMypage 에서 정보 받기
            // [본인 정보 수정] 버튼 클릭 시
            val vupdate : String? = intent.getStringExtra("vupdate")
            Log.e(TAG, "vupdate : $vupdate")
            // [회원탈퇴] 버튼 클릭 시
            val vdelete : String? = intent.getStringExtra("vdelete")
            Log.e(TAG, "vdelete : $vdelete")
            val vupdateIntent = Intent(this, VMypageUpdate::class.java)
            val vdeleteIntent = Intent(this, MypageDelete::class.java)

            // js에서 받은 값이 true 이면, 다음 페이지로 넘어가기s
            if(pwresult.equals("true")) {
                Log.e(TAG, "pwresult.equals(true)")

                if (pupdate.equals("pupdate")) {
                    startActivity(pupdateIntent)
                }
                else if (pdelete.equals("pdelete")){
                    startActivity(pdeleteIntent)
                }
                else if (vupdate.equals("vupdate")){
                    startActivity(vupdateIntent)
                    Log.e(TAG, "vupdate 비밀번호 OK")
                }
                else if (vdelete.equals("vdelete")){
                    startActivity(vdeleteIntent)
                    Log.e(TAG, "vdelete 비밀번호 OK")
                }

            }
            else if (pwresult.equals("false")) {
                Log.e(TAG, "pwresult.equals(false)")
                // js에서 받은 값이 false 이면, 비밀번호 다시 입력하게 하기
                //객체 생성
                val passwd : EditText = findViewById(R.id.passwd)
                passwd.setText(null)
                Toast.makeText(
                    this@MypagePasswd, "비밀번호가 틀렸습니다. \n다시 입력하세요.", Toast.LENGTH_LONG
                ).show()

            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}