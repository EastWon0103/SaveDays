package com.lilcode.aop.p4c03.googlemap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.view.*

class RegisterActivity : AppCompatActivity() {
    var firebaseAuth : FirebaseAuth? = FirebaseAuth.getInstance() //유저정보
    var fbFirestore : FirebaseFirestore? = FirebaseFirestore.getInstance() //데이터베이스
    var userInfo = UserInformation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        bt_register.setOnClickListener{
            createEmail()
        }
    }

    private fun createEmail(){
        firebaseAuth!!.createUserWithEmailAndPassword(et_id_register.text.toString(),et_pw_register.text.toString()).addOnCompleteListener(this){
            if(it.isSuccessful) {
                firebaseAuth!!.signInWithEmailAndPassword(et_id_register.text.toString(), et_pw_register.text.toString()).addOnCompleteListener(this) {
                    userInfo.user_email = et_id_register.text.toString()
                    userInfo.user_password = et_pw_register.text.toString()
                    userInfo.user_postnum = "0"

                    fbFirestore!!.collection(firebaseAuth?.currentUser!!.email.toString())?.document("0").set(userInfo)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Post info success", Toast.LENGTH_SHORT).show() }
                    Toast.makeText(this, "Register success", Toast.LENGTH_LONG).show()
//                    val loginIntent = Intent(this, LoginActivity::class.java)
//                    startActivity(loginIntent)
                    finish()
                }
            }else{
                Toast.makeText(this, "Register fail", Toast.LENGTH_LONG).show()
            }
        }
    }


}