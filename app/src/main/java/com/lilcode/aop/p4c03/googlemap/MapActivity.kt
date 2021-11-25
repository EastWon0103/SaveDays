package com.lilcode.aop.p4c03.googlemap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.lilcode.aop.p4c03.googlemap.activity.AddActivity
import com.lilcode.aop.p4c03.googlemap.databinding.ActivityMapBinding
import com.lilcode.aop.p4c03.googlemap.model.LocationLatLngEntity
import com.lilcode.aop.p4c03.googlemap.model.SearchResultEntity
import com.lilcode.aop.p4c03.googlemap.utility.RetrofitUtil
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MapActivity : AppCompatActivity(), OnMapReadyCallback, CoroutineScope {
    val STRING_KEY_NAME = "get_name"
    val STRING_KEY_LAT = "get_lat"
    val STRING_KEY_LONG = "get_long"
    val STRING_KEY_FULL = "get_full"

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    private lateinit var binding: ActivityMapBinding
    private lateinit var map: GoogleMap
    private var currentSelectMarker: Marker? = null

    private lateinit var searchResult: SearchResultEntity

    private lateinit var locationManager: LocationManager // 안드로이드 에서 위치정보 불러올 때 관리해주는 유틸 클래스

    private lateinit var myLocationListener: MyLocationListener // 나의 위치를 불러올 리스너

    companion object {
        const val SEARCH_RESULT_EXTRA_KEY: String = "AIzaSyC-j_A3WXarUCF3w4c0f8AbjiWGLC5awhs"
        //AIzaSyBsYyU9GY07xxczHnDcvwxp0q1asBUHCaw
        const val CAMERA_ZOOM_LEVEL = 17f
        const val PERMISSION_REQUEST_CODE = 2021
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()

        if (::searchResult.isInitialized.not()) {
            intent?.let {
                searchResult = it.getParcelableExtra<SearchResultEntity>(SEARCH_RESULT_EXTRA_KEY)
                    ?: throw Exception("데이터가 존재하지 않습니다.")
                setupGoogleMap()
            }
        }

        bindViews()
        var latitude = searchResult.locationLatLng.latitude
        var longitude = searchResult.locationLatLng.longitude
        var name = searchResult.name
        var fulladdress = searchResult.fullAddress
        map_no.setOnClickListener {
            val intent = Intent(this,MapSearchActivity::class.java)
            startActivity(intent)
            finish()
        }
        map_yes.setOnClickListener {
//            val mIntent = Intent(this, AddActivity::class.java).apply{
//                putExtra(STRING_KEY_LAT, latitude.toString())
//                putExtra(STRING_KEY_LONG, longitude.toString())
//                putExtra(STRING_KEY_NAME, name)
//                putExtra(STRING_KEY_FULL, fulladdress)
//            }
//            setResult(RESULT_OK, mIntent)
//            if(!isFinishing) finish()
            val intent = Intent(this,AddActivity::class.java)
            intent.putExtra("latitude",latitude.toString())
            intent.putExtra("longitude",longitude.toString())
            intent.putExtra("name",name)
            intent.putExtra("fulladdress",fulladdress)
            startActivity(intent)
        }
    }

    private fun bindViews() = with(binding) {
        // 현재 위치 버튼 리스너
        currentLocationButton.setOnClickListener {
            binding.progressCircular.isVisible = true
            getMyLocation()
        }
    }


    private fun setupGoogleMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(binding.mapFragment.id) as SupportMapFragment
        mapFragment.getMapAsync(this) // callback 구현 (onMapReady)

        // 마커 데이터 보여주기

    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        currentSelectMarker = setupMarker(searchResult)

        currentSelectMarker?.showInfoWindow()
    }

    private fun setupMarker(searchResult: SearchResultEntity): Marker {

        // 구글맵 전용 위도/경도 객체
        val positionLatLng = LatLng(
            searchResult.locationLatLng.latitude.toDouble(),
            searchResult.locationLatLng.longitude.toDouble()
        )

        // 구글맵 마커 객체 설정
        val markerOptions = MarkerOptions().apply {
            position(positionLatLng)
            title(searchResult.name)
            snippet(searchResult.fullAddress)
        }

        // 카메라 줌 설정
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, CAMERA_ZOOM_LEVEL))

        return map.addMarker(markerOptions)
    }

    private fun getMyLocation() {
        // 위치 매니저 초기화
        if (::locationManager.isInitialized.not()) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        // GPS 이용 가능한지
        val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        // 권한 얻기
        if (isGpsEnable) {
            when {
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) && shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) -> {
                    showPermissionContextPop()
                }

                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED -> {
                    makeRequestAsync()
                }

                else -> {
                    setMyLocationListener()
                }
            }
        }
    }

    private fun showPermissionContextPop() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("내 위치를 불러오기위해 권한이 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                makeRequestAsync()
            }
            .create()
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocationListener() {
        val minTime = 3000L // 현재 위치를 불러오는데 기다릴 최소 시간
        val minDistance = 100f // 최소 거리 허용

        // 로케이션 리스너 초기화
        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }

        // 현재 위치 업데이트 요청
        with(locationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                myLocationListener
            )
            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime,
                minDistance,
                myLocationListener
            )
        }
    }

    private fun onCurrentLocationChanged(locationLatLngEntity: LocationLatLngEntity) {
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    locationLatLngEntity.latitude.toDouble(),
                    locationLatLngEntity.longitude.toDouble()
                ), CAMERA_ZOOM_LEVEL
            )
        )

        loadReverseGeoInformation(locationLatLngEntity)
        removeLocationListener() // 위치 불러온 경우 더이상 리스너가 필요 없으므로 제거
    }

    private fun loadReverseGeoInformation(locationLatLngEntity: LocationLatLngEntity) {
        // 코루틴 사용
        launch(coroutineContext) {
            try {
                binding.progressCircular.isVisible = true

                // IO 스레드에서 위치 정보를 받아옴
                withContext(Dispatchers.IO) {
                    val response = RetrofitUtil.apiService.getReverseGeoCode(
                        lat = locationLatLngEntity.latitude.toDouble(),
                        lon = locationLatLngEntity.longitude.toDouble()
                    )
                    if (response.isSuccessful) {
                        val body = response.body()

                        // 응답 성공한 경우 UI 스레드에서 처리
                        withContext(Dispatchers.Main) {
                            Log.e("list", body.toString())
                            body?.let {
                                currentSelectMarker = setupMarker(
                                    SearchResultEntity(
                                        fullAddress = it.addressInfo.fullAddress ?: "주소 정보 없음",
                                        name = "내 위치",
                                        locationLatLng = locationLatLngEntity
                                    )
                                )
                                // 마커 보여주기
                                currentSelectMarker?.showInfoWindow()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MapActivity, "검색하는 과정에서 에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressCircular.isVisible = false
            }
        }
    }

    private fun removeLocationListener() {
        if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
            locationManager.removeUpdates(myLocationListener) // myLocationListener 를 업데이트 대상에서 지워줌
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setMyLocationListener()
                } else {
                    Toast.makeText(this, "권한을 받지 못했습니다.", Toast.LENGTH_SHORT).show()
                    binding.progressCircular.isVisible = false
                }
            }
        }
    }

    private fun makeRequestAsync() {
        // 퍼미션 요청 작업. 아래 작업은 비동기로 이루어짐
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            // 현재 위치 콜백
            val locationLatLngEntity = LocationLatLngEntity(
                location.latitude.toFloat(),
                location.longitude.toFloat()
            )

            onCurrentLocationChanged(locationLatLngEntity)
        }

    }

    override fun onDestroy() {
        super.onDestroy()

    }
}