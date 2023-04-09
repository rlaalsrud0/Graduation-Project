package com.example.ollie

import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap
import java.util.HashMap

interface RetrofitInterface {
    @POST("/signup2")
    fun executeSignup(@Body map: HashMap<String?, String?>?): Call<Void?>?

    @POST("/signup_detail")
    fun executeDetail(@Body map: HashMap<String?, String?>?): Call<Void?>?

    @POST("/matching")
    fun executeMatching(@Body map: HashMap<String?, String?>?): Call<Void?>?

    @POST("/login")
    fun executeLogin(@Body map: HashMap<String?, String?>?): Call<LoginResult?>?

    @POST("/posts")
    fun posts() : Call<Void?>?

    @get:GET("/")
    val data: Call<String?>?

    @GET("/mypages")
    fun data1(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    @POST("/pmypageu")
    fun executeUpdatep(@Body map: HashMap<String?, String?>): Call<Void?>?

    @POST("/send")
    fun executeSend(@Body map: HashMap<String?, String?>): Call<Void?>?

    //select
    @get:GET("/mypages")
    val data1: Call<String?>?

    //update
    @POST("/vmypageu")
    fun executeUpdatev(@Body map: HashMap<String?, String?>): Call<Void?>?

    //mapping테이블에서 보호자 리스트 받기
    @get:GET("/help")
    val getdata: Call<String?>?

    //JS코드에서 PDevice로 값을 가져와
    @GET("/device")
    fun devicegetdata(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    //select
    @get:GET("/vselect")
    val vselect: Call<String?>?

    @get:GET("/ssaidpass")
    val ssaidpass: Call<String?>?

    //time
    @POST("/aa")
    fun executeTime(@Body map: HashMap<String, String>): Call<Void?>?


    //보호자 이름 가져오기
    @GET("/pname")
    fun getPName(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    //AllUser
    @GET("/puser")
    fun devicegetpuser(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    @GET("/vuser")
    fun devicegetvuser(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    @POST("/devicesend")
    fun executedevice(@Body map: HashMap<String?, String?>): Call<Void?>?

    //취약계층 이름 가져오기
    @GET("/vname")
    fun getVName(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    //디바이스 번호와 비밀번호 전송
    @POST("/sendDeviceData")
    fun executeDeviceSend(@Body map: HashMap<String?, String?>): Call<Void?>?

    //디바이스 번호 전송
    @POST("/sendDevice")
    fun executeDeviceNumSend(@Body map: HashMap<String?, String?>): Call<Void?>?

    @get:GET("/SelectDevicePasswd")
    val getDeviceInfo: Call<String?>?

    // js로 ssaid, passwd 보내기
    @POST("/sendData")
    fun passwdcheck(@Body map: HashMap<String?, String?>): Call<Void?>?

    //비밀번호 결과 받기
    @GET("/SelectPasswd")
    fun passwdresult(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    //비밀번호 찾기
    @POST("/sendPasswdInfo")
    fun executepasswd(@Body map: HashMap<String?, String?>): Call<Void?>?

    @GET("/passwd_check")
    fun getPasswdCheck(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    //영상모아보기
    @POST("/sendName")
    fun executename(@Body map: HashMap<String?, String?>): Call<Void?>?

    @GET("/outList")
    fun getVideoListO(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    @GET("/inList")
    fun getVideoListI(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    //PNotify, PMenuOut 정보 받기
    @get:GET("/getVInfo")
    val getVInfo: Call<String?>?

    //메뉴 이름가져오기
    @GET("/PMenuN")
    fun getPMenuName(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    //취약계층 위치 받기
    @GET("/vLocation")
    fun getLocation(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    //구분 가져오기
    @GET("/loginData")
    fun loginData(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    //신고페이지 home update
    @POST("/report")
    fun report(@Body map: HashMap<String?, String?>?): Call<Void?>?

    // PHome
    @POST("/sendHome")
    fun sendHome(@Body map: HashMap<String?, String?>): Call<Void?>?

    //JS코드에서 취약계층 외출 시
    @GET("/insertMapping")
    fun getOut(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    //JS코드에서 취약계층 귀가 시
    @GET("/updateMapping")
    fun getIn(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    //JS코드에서 취약계층 미귀가 시
    @GET("/timeOK")
    fun getNoIn(@QueryMap map: HashMap<String?, String?>): Call<String?>?

    //JS코드에서 취약계층 대신확인
    @POST("/checkInstead")
    fun getNoInY(@Body map: HashMap<String?, String?>): Call<Void?>?

    //JS코드에서 취약계층 신고
    @POST("/report")
    fun getNoInN(@Body map: HashMap<String?, String?>): Call<Void?>?

}