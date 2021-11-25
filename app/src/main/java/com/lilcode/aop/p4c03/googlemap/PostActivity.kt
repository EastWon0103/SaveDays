package com.lilcode.aop.p4c03.googlemap

import android.content.ContentValues.TAG
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lilcode.aop.p4c03.googlemap.activity.AddActivity
import com.lilcode.aop.p4c03.googlemap.activity.ImgViewAdapter
import com.lilcode.aop.p4c03.googlemap.activity.PostViewAdapter
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PostActivity : AppCompatActivity() {
    private val MIN_SCALE = 0.85f // 뷰가 몇퍼센트로 줄어들 것인지
    private val MIN_ALPHA = 0.5f // 어두워지는 정도를 나타낸 듯 하다.

    var fbAuth: FirebaseAuth? = FirebaseAuth.getInstance() //유저정보
    var fbFirestore: FirebaseFirestore? = FirebaseFirestore.getInstance() //데이터베이스

    var URI_LIST: ArrayList<String> = ArrayList()
    var adapter = PostViewAdapter(URI_LIST)

    lateinit var document_index: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        document_index = intent.getStringExtra("document_index").toString()

        post_viewPager.adapter = adapter
        post_viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        post_viewPager.setPageTransformer(ZoomOutPageTransformer())
        post_worm_dot_indicator.setViewPager2(post_viewPager)
        adapter.notifyDataSetChanged()

        getFirebase()

        post_setting.visibility = View.INVISIBLE
        post_back.setOnClickListener{
            finish()
        }

    }

    fun getFirebase() {
        fbFirestore!!.collection(fbAuth?.currentUser!!.email.toString()).document("0").get().addOnSuccessListener { documents ->
//            var postnum = documents.data?.getValue("user_postnum").toString()
//            var addpostnum: Int = postnum.toInt() //인트로 바꿈
//            addpostnum += 1
//            postnum = addpostnum.toString()
            var postnum = document_index

            fbFirestore!!.collection(fbAuth?.currentUser!!.email.toString()).document(postnum).addSnapshotListener { snapshot, e ->


                fbFirestore!!.collection(fbAuth?.currentUser!!.email.toString()).document(postnum)
                    .get().addOnSuccessListener { document ->
                    var year = document.data?.getValue("ca_year").toString()
                    var month = document.data?.getValue("ca_month").toString()
                    var day = document.data?.getValue("ca_day").toString()
                    var addressName = document.data?.getValue("ad_addressName").toString()
                    var title = document.data?.getValue("fb_title").toString()
                    var content = document.data?.getValue("fb_content").toString()

                    post_day.text = "${year}년 ${month}월 ${day}일"
                    post_location.text = addressName
                    post_title.text = title
                    post_content.text = content

                        //Log.d(TAG, "${document.id} => ${document.data}")

                        for (i in 0..9) {
                            var img = "fb_image" + i
                            var imageUri = document.data?.getValue(img).toString()


                            if (imageUri == "null") break

                            if(URI_LIST.size == i){
                                URI_LIST.add(imageUri)
                            }
                        }
                        adapter.notifyDataSetChanged()
                }
            }
        }
    }

    inner class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            view.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }
                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        alpha = (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }
    }
}