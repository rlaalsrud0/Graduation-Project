package com.example.ollie

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class PMenuMypage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_menu_mypage)

        // PMenu에서 전달한 디바이스번호 받기
        val deviceNum = intent.getStringExtra("device")
        Log.e(TAG, "PMenu에서 받아온 디바이스번호 : $deviceNum")

        // [이전] 버튼
        val back : Button = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        // 네비게이션 바
        // [디바이스 방] 버튼
        val plus : Button = findViewById(R.id.plus)
        plus.setOnClickListener {
            val intent = Intent(this, PDevice::class.java)
            startActivity(intent)
        }

        // [홈] 버튼
        val home : Button = findViewById(R.id.home)
        home.setOnClickListener {
            val intent = Intent(this, PHome::class.java)
            startActivity(intent)
        }

        // [메뉴] 버튼
        val menu : Button = findViewById(R.id.menu)
        menu.setOnClickListener {
            val intent = Intent(this, PMenu::class.java)
            intent.putExtra("device",deviceNum)
            startActivity(intent)
        }

        // [모든 취약계층 목록] 버튼
        val vlist : Button = findViewById(R.id.vlist)
        vlist.setOnClickListener {
            val intent = Intent(this, PMenuMypageListVul::class.java)
            startActivity(intent)
        }

        // [본인 정보 수정] 버튼
        val update : Button = findViewById(R.id.update)
        update.setOnClickListener {
            //정보수정임을 명시해줘야한다
            val intent = Intent(this, MypagePasswd::class.java)
            intent.putExtra("pupdate","pupdate")
            startActivity(intent)
        }

        // [회원탈퇴] 버튼
        val delete : Button = findViewById(R.id.delete)
        delete.setOnClickListener {
            //회원탈퇴임을 명시해줘야한다
            val intent = Intent(this, MypagePasswd::class.java)
            intent.putExtra("pdelete","pdelete")
            startActivity(intent)
        }
    }
}