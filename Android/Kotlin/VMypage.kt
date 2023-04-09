package com.example.ollie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class VMypage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.v_mypage)

        // [이전] 버튼
        val back : Button = findViewById(R.id.back)
        back.setOnClickListener{
            finish()
        }

        // [본인 정보 수정] 버튼
        val update : Button = findViewById(R.id.update)
        update.setOnClickListener{
            val intent = Intent(this, MypagePasswd::class.java)
            intent.putExtra("vupdate","vupdate")
            startActivity(intent)
        }

        // [모든 보호자 목록] 버튼
        val vlist : Button = findViewById(R.id.vlist)
        vlist.setOnClickListener{
            val nextIntent = Intent(this, VListPro::class.java)
            startActivity(nextIntent)
        }

        // [회원탈퇴] 버튼
        val delete : Button = findViewById(R.id.delete)
        delete.setOnClickListener{
            val intent = Intent(this, MypagePasswd::class.java)
            intent.putExtra("vdelete","vdelete")
            startActivity(intent)
        }
    }
}