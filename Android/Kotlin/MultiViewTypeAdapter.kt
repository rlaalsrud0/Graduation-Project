package com.example.ollie

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.plist_design.view.*
import android.app.Activity
import android.util.TypedValue
import kotlinx.android.synthetic.main.p_home_inout_view.view.*


class MultiViewTypeAdapter(
    var list: ArrayList<PHomeModel>,
    var context: Context,

    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = this.javaClass.simpleName

    var vssaid : String? = ""
    var deviceNum : String? = ""

    //==== [Click 이벤트 구현을 위해 추가된 코드] ======================
    //OnItemClickListener 인터페이스 선언
    interface OnItemClickListener {
        fun onLocClicked(position: Int)
        fun onOutClicked(position: Int)
        fun onCheckClicked(position: Int)
        fun onNotifyClicked(position: Int)
        fun onVideoClicked(position: Int)
    }

    // OnItemClickListener 참조 변수 선언
    private var itemClickListener: OnItemClickListener? = null

    // OnItemClickListener 전달 메소드
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClickListener = listener
    }
    //==================================================================


    // getItemViewType의 리턴값 Int가 viewType으로 넘어온다.
    // viewType으로 넘어오는 값에 따라 viewHolder를 알맞게 처리해주면 된다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        return when (viewType) {
            PHomeModel.P_HOME_INOUT_VIEW -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.p_home_inout_view, parent, false)
                InOutViewHolder(view)
            }
            PHomeModel.P_HOME_NOIN_VIEW -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.p_home_noin_view, parent, false)
                NoInViewHolder(view)
            }
            PHomeModel.P_HOME_NOTIFY_CHECK_VIEW -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.p_home_notify_check_view, parent, false)
                NotifyCheckViewHolder(view)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.e("onBindViewHolder", "${list[position]}")
        val obj = list[position]
        when (obj.type) {
            PHomeModel.P_HOME_INOUT_VIEW -> {
                vssaid = obj.vssaid // 취약계층 ssaid
                Log.e("vssaid", "$vssaid")
                (holder as InOutViewHolder).time.text = obj.timeText // 날짜-시간
                holder.ment.text = obj.mentString // "00님이 외출/귀가 하였습니다" 멘트

                holder.videoView.setVideoPath(obj.video) // 영상 경로
                // 영상 보여주기
                holder.videoView.start()

                holder.itemView.videoView.setOnClickListener{
                    holder.videoView.requestFocus()
                    holder.videoView.start()
                }
                // [비디오] 버튼 클릭 시,
                holder.videoView.setOnClickListener {
                    itemClickListener!!.onVideoClicked(position)
                }
                // [외출정보] 버튼 클릭 시
                holder.out.setOnClickListener{
                    itemClickListener!!.onOutClicked(position)
                }
            }

            PHomeModel.P_HOME_NOIN_VIEW -> {
                (holder as NoInViewHolder).time.text = obj.timeText
                holder.title.text = obj.mentString

                // [외출정보] 버튼 클릭 시
                holder.out.setOnClickListener{
                    itemClickListener!!.onOutClicked(position)
                }
                // [대신확인] 버튼 클릭 시
                holder.check.setOnClickListener {
                    itemClickListener!!.onCheckClicked(position)
                    // 버튼 숨기기
//                    holder.loc.setVisibility(View.INVISIBLE)
//                    holder.out.setVisibility(View.INVISIBLE)
//                    holder.check.setVisibility(View.INVISIBLE)
//                    holder.notify.setVisibility(View.INVISIBLE)
                }
                // [신고] 버튼 클릭 시
                holder.notify.setOnClickListener {
                    itemClickListener!!.onNotifyClicked(position)
                    // 버튼 숨기기
//                    holder.loc.setVisibility(View.INVISIBLE)
//                    holder.out.setVisibility(View.INVISIBLE)
//                    holder.notify.setVisibility(View.INVISIBLE)
                }
            }
            PHomeModel.P_HOME_NOTIFY_CHECK_VIEW -> {
                (holder as NotifyCheckViewHolder).time.text = obj.timeText
                holder.title.text = obj.mentString

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    // 여기서 받는 position은 데이터의 index다.
    override fun getItemViewType(position: Int): Int {
        Log.e("getItemViewType", "position: $position")
        return list[position].type
    }

    inner class InOutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoView: VideoView = itemView.findViewById(R.id.videoView)
        val time: TextView = itemView.findViewById(R.id.time)
        val ment: TextView = itemView.findViewById(R.id.ment)
        val out: Button = itemView.findViewById(R.id.out)
    }

    inner class NoInViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.time)
        val title: TextView = itemView.findViewById(R.id.title)
        val out: Button = itemView.findViewById(R.id.out)
        val check: Button = itemView.findViewById(R.id.check)
        val notify: Button = itemView.findViewById(R.id.notify)
    }

    inner class NotifyCheckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.time)
        val title: TextView = itemView.findViewById(R.id.title)

    }
}