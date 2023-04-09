package com.example.ollie

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class UserAdapter(
    var nameList: ArrayList<String> = ArrayList<String>(),
    var divList: ArrayList<String> = ArrayList<String>(),

    // context 클래스 객체
    // 클릭하여 화면에 토스트 메세지를 표시 할 수 있다.
    // 생성자를 사용하여 기본 활동에서 만든 데이터를 어댑터로 보낸다.
    var context: Context
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var name : TextView = itemView.findViewById(R.id.name)
        var text : TextView = itemView.findViewById(R.id.text)
        var cardView : CardView = itemView.findViewById(R.id.cardView)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val view : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.p_menu_list_user_design,parent,false)

        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        // 여기에 cardView 구성 요소를 정의했기 때문에 모든 작업 수행
        // 화면에 표시하기
        holder.name.text = nameList.get(position)
        holder.text.text = divList.get(position)
//        holder.name.text = nameList2.get(position)
//        holder.text.text = divList2.get(position)

    }
    override fun getItemCount(): Int {
        // 항목 수 가져오기 방법

        return  nameList.size
    }


}