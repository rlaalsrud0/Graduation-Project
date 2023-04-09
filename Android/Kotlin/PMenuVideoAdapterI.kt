package com.example.ollie

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class PMenuVideoAdapterI (
    var videoList: ArrayList<String> = ArrayList<String>(),
    var dateList: ArrayList<String> = ArrayList<String>(),

    var context: Context
) : RecyclerView.Adapter<PMenuVideoAdapterI.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var video : VideoView = itemView.findViewById(R.id.video1)
        var cardView : CardView = itemView.findViewById(R.id.cardView2)
        var date : TextView = itemView.findViewById(R.id.text)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val view : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.p_menu_video_design_i,parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        // 여기에 cardView 구성 요소를 정의했기 때문에 모든 작업 수행
        // 화면에 표시하기
        holder.date.text = dateList.get(position)

        holder.video.setVideoPath(videoList.get(position))
        holder.cardView.setOnClickListener{
            //video재생 시작
            holder.video.requestFocus()
            holder.video.start()
        }
    }


    override fun getItemCount(): Int {
        return  videoList.size
    }

}