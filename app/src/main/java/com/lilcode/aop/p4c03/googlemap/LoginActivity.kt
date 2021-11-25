package com.lilcode.aop.p4c03.googlemap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.et_id
import kotlinx.android.synthetic.main.activity_login.et_pw

class LoginActivity : AppCompatActivity() {
    //파이어베이스
    var fbAuth: FirebaseAuth? = FirebaseAuth.getInstance() //유저정보
    var fbFirestore : FirebaseFirestore? = FirebaseFirestore.getInstance() //데이터베이스
    //구글
    private var googleSignInClient : GoogleSignInClient? = null //구글 클라이언트
    private val RC_SIGN_IN = 99 //구글 request code

    var userInfo = UserInformation() //구글 저장시 구글 사용자 설정
//
//    private val getResult = registerForActivityResult(){ result->
//        if (result.resultCode == RESULT_OK) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//            try {
//                //구글 로그인에 성공시 파이어베이스와 연동
//                val account = task.getResult(ApiException::class.java)
//                Log.w("LoginActivity", "Google sign in successed")
//                firebaseAuthWithGoogle(account!!)
//
//            } catch (e: ApiException) {
//                // 오류날시 오류랑 로그인 실패를 log로 띄어줌
//                Log.e("LoginActivity", "Google sign in failed", e)
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val registerIntent = Intent(this, RegisterActivity::class.java)

        //구글 로그인 구성
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_web_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        //로그인 버튼
        bt_login.setOnClickListener(){
            //아이디 비번 빈칸 처리
            if(et_id.text.toString().length==0){
                Toast.makeText(this,"아이디를 확인해주세요",Toast.LENGTH_LONG).show()
            }else if(et_pw.text.toString().length==0) {
                Toast.makeText(this, "비밀번호를 확인해주세요", Toast.LENGTH_LONG).show()
            }else{
                loginEmail()
            }
        }
        //구글 로그인 버튼
        bt_googlelogin.setOnClickListener {
            loginGoogle()
        }
        //회원가입 버튼
        bt_goregister.setOnClickListener(){
            startActivity(registerIntent)
        }
    }
    private fun loginEmail(){
        fbAuth!!.signInWithEmailAndPassword(et_id.text.toString(),et_pw.text.toString()).addOnCompleteListener(this){
            if(it.isSuccessful){ //로그인 성공
                //activity_main 이동
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)

                Toast.makeText(this,"Email Login success", Toast.LENGTH_SHORT).show()
            }else{ //로그인 실패
                Toast.makeText(this,"Email Login fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginGoogle() { //구글 로그인 화면 이동
        val signInIntent = googleSignInClient?.signInIntent
        Log.d("testLine1", "checked")
        //getResult.launch(signInIntent)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // GoogleSignInApi.getSignInIntent에서 Intent를 실행하여 반환된 결과
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //구글 로그인에 성공시 파이어베이스와 연동
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)

            } catch (e: ApiException) {
                // 오류날시 오류랑 로그인 실패를 log로 띄어줌
                Log.w("LoginActivity", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) { //파이어 베이스와 구글 연동
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null) //Google SignInAccount 객체에서 ID 토큰을 가져와서 Firebase Auth로 교환하고 Firebase에 인증
        fbAuth!!.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) { //로그인 성공시
                fbFirestore!!.collection(fbAuth?.currentUser!!.email.toString())?.document("0").get().addOnSuccessListener { document-> //기존 데이터베이스 있는지 확인
                    if(document.data?.getValue("user_postnum") != null){ //postnum이 null아니면 userinfo를 하지 않는다.
                        Toast.makeText(this, "Google login success", Toast.LENGTH_SHORT).show()
                        //메인 엑티비티 이동
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }else{
                        //유저 정보 저장 //구글로그인시 비밀번호는 google로 설정
                        userInfo.user_email =fbAuth?.currentUser!!.email.toString()
                        userInfo.user_password = "google"
                        userInfo.user_postnum = "0"

                        fbFirestore!!.collection(fbAuth?.currentUser!!.email.toString())?.document("0").set(userInfo).addOnSuccessListener {
                            Toast.makeText(this, "Google login and UserInfo success", Toast.LENGTH_SHORT).show()
                        }
                        //메인 엑티비티 이동
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }

            }else{
                Toast.makeText(this, "Google login fail", Toast.LENGTH_SHORT).show()
            }
        }
    }
}