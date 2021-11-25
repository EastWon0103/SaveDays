package com.lilcode.aop.p4c03.googlemap

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lilcode.aop.p4c03.googlemap.activity.AddActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.marker.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    var fbAuth: FirebaseAuth? = FirebaseAuth.getInstance() //유저정보
    var fbFirestore: FirebaseFirestore? = FirebaseFirestore.getInstance() //데이터베이스

    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_addmark.setOnClickListener {
            val intent = Intent(this, MapSearchActivity::class.java)
            startActivity(intent)
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.d("login",fbAuth?.currentUser!!.email.toString())


        val marker_root_view = LayoutInflater.from(this).inflate(R.layout.marker, null);
        var marker = marker_root_view.findViewById(R.id.tv_marker) as TextView
        fbFirestore!!.collection(fbAuth?.currentUser!!.email.toString()).document("0").addSnapshotListener { snapshot, e ->
            fbFirestore!!.collection(fbAuth?.currentUser!!.email.toString()).document("0").get().addOnSuccessListener { documents ->
                var postnum = documents.data?.getValue("user_postnum").toString()
                var mark_max_length: Int = postnum.toInt() //인트로 바꿈

                for(i in 1..mark_max_length){
                    fbFirestore!!.collection(fbAuth?.currentUser!!.email.toString()).document(i.toString())
                        .get().addOnSuccessListener { documents ->
                            var latitude = documents.data?.getValue("ad_latitude").toString().toDouble()
                            var longitude = documents.data?.getValue("ad_longitude").toString().toDouble()
                            var title = documents.data?.getValue("fb_title").toString()

                            marker.setText(title)
                            val mark_position = LatLng(latitude,longitude)
                            mMap.addMarker(MarkerOptions().position(mark_position).title(i.toString()).icon(
                                BitmapDescriptorFactory.fromBitmap(createDrawable(this, marker_root_view))
                            ))
                        }
                }

            }

            val startPoint = LatLng(37.5302, 126.9651)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 10.toFloat()))
            mMap.setOnMarkerClickListener(this)
        }
    }

    private fun createDrawable(cxt: Context, v : View) : Bitmap?{
        val displayMetrics = DisplayMetrics()
        //(cxt as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)

        v.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        v.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        v.buildDrawingCache()
        val bitmap: Bitmap = Bitmap.createBitmap(
            v.getMeasuredWidth(),
            v.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        v.draw(canvas)
        return bitmap
    }

    override fun onMarkerClick(m: Marker?): Boolean {
        val post_intent = Intent(this, PostActivity::class.java)
        post_intent.putExtra("document_index", m?.title)
        startActivity(post_intent)
        return true
    }

}