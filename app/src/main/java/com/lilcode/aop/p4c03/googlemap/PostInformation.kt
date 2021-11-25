package com.lilcode.aop.p4c03.googlemap

import android.net.Uri
import kotlin.collections.ArrayList

data class PostInformation(
    //파이어 베이스에 들어갈 정보 목록
    //이미지 0~9
    var fb_image0: String? = null,
    var fb_image1: String? = null,
    var fb_image2: String? = null,
    var fb_image3: String? = null,
    var fb_image4: String? = null,
    var fb_image5: String? = null,
    var fb_image6: String? = null,
    var fb_image7: String? = null,
    var fb_image8: String? = null,
    var fb_image9: String? = null,
    //제목과 내용
    var fb_title : String? = null,
    var fb_content : String? = null,
    //캘린더
    var ca_year : Int? = null,
    var ca_month : Int? = null,
    var ca_day : Int? = null,
    //주소
    var ad_latitude : String? = null,
    var ad_longitude : String? = null,
    var ad_addressName : String? = null,
    var ad_fullAdress : String? = null){
}
