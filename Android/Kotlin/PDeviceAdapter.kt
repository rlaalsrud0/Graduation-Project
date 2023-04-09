package com.example.ollie

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.p_menu_list_user_design.view.*
import kotlinx.android.synthetic.main.p_menu_mypage_list_vul.view.*
import kotlinx.android.synthetic.main.plist_design.view.*
import java.util.ArrayList

class PDeviceAdapter(
    var dNameList: ArrayList<String>,
    var doorList: ArrayList<Int>,

    var context: Context,

    ) : RecyclerView.Adapter<PDeviceAdapter.PDeviceViewHolder>() {

    class PDeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var vname : TextView = itemView.findViewById(R.id.vname)
        var door : ImageView = itemView.findViewById(R.id.door)
        var cardView : CardView = itemView.findViewById(R.id.cardView)

    }

    //==== [Click 이벤트 구현을 위해 추가된 코드] ======================
    //OnItemClickListener 인터페이스 선언
    interface OnItemClickListener {
        fun onItemClicked(position: Int)
    }

    // OnItemClickListener 참조 변수 선언
    private var itemClickListener: OnItemClickListener? = null

    // OnItemClickListener 전달 메소드
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClickListener = listener
    }
    //==================================================================

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDeviceViewHolder {
        val view : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.pdevice_card_design, parent, false)

        return PDeviceViewHolder(view)
    }

    // 실제 화면에 데이터와 레이아웃을 연결하는 onBindViewHolder() 함수.
    override fun onBindViewHolder(holder: PDeviceViewHolder, position: Int) {
        // 선택한 뷰에 해당하는 디바이스번호 가져오기
        holder.vname.text = dNameList.get(holder.adapterPosition)
        holder.door.setImageResource(doorList.get(position))

        holder.itemView.setOnClickListener {
            itemClickListener!!.onItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        // 항목 수 가져오기 방법
        return  dNameList.size
    }
}