package com.example.ollie

data class PHomeModel(

    val type: Int, // 레이아웃  번호
    val vssaid: String?, // 취약계층 ssaid
    val h_device: String?, // PDevice에서 넘겨받은 디바이스번호
    val timeText: String?, // 날짜-시간
    val mentString: String?, // 알림멘트
    val video: String?, // 비디오 경로
    val ny: String? // 대신확인/신고 여부

) {

    companion object {
        const val P_HOME_INOUT_VIEW = 0
        const val P_HOME_NOIN_VIEW = 1
        const val P_HOME_NOTIFY_CHECK_VIEW = 2
    }
}

// 첫 번째 인자 값 : 우리가 만든 3가지 형태의 뷰들 중,
// 어떤 형태의 뷰인지 Int값으로 넘겨줄 것이다.
// 그 Int은 ViewTypeEnum 을 사용한다.

// 두 번째 인자 값 : 텍스트를 입력받을 파라미터이다.
// 텍스트 하나만 랜더링 하는 뷰에서는 그 텍스트를,
// 텍스트1개 이미지1개인 뷰에서 텍스트를,
// 텍스트2 이미지1개인 뷰에서는 제목 부분을 담당할 String이다.

// 세 번째 인자 값 : 이미지가 필요한 뷰라면 이미지를 넣어줄 파라미터.

// 네 번째 인자 값 : 텍스트2 이미지1개인 image_type2.xml 뷰에서
// 제목 아래의 컨텐츠 부분의 값을 담당할 String이다.