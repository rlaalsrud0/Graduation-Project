package com.example.ollie

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class PListAdapter(
    var pNameList: ArrayList<String>,

    // context 클래스 객체
    // 생성자를 사용하여 기본 활동에서 만든 데이터를 어댑터로 보낸다.
    var context: Context) : RecyclerView.Adapter<PListAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

            var name : TextView = itemView.findViewById(R.id.name)
            var cardView : CardView = itemView.findViewById(R.id.cardView)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.plist_design,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 여기에 cardView 구성 요소를 정의했기 때문에 모든 작업 수행
        // 화면에 표시하기
        holder.name.text = pNameList.get(position)

        Log.e(TAG, "")

        // cardView 구성 요소 클릭 리스너 추가
        holder.cardView.setOnClickListener{
            Log.e(TAG, "You Selected the ${pNameList.get(position)}")
        }
    }

    override fun getItemCount(): Int {
        // 항목 수 가져오기 방법
        return  pNameList.size
    }
}