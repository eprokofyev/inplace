package com.inplace.auth.ui.login

import android.content.Intent
import android.os.Build
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.inplace.api.ApiImageLoader.getImageByUrl
import com.inplace.api.vk.*
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import kotlin.concurrent.thread
import android.util.Log
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.Toast
import com.inplace.MainActivity
import com.inplace.R
import java.io.Serializable


class VkLoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vk_login, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("odred", "result")
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                Log.d("tokenn", token.toString())
                updateUiWithUser()
            }

            override fun onLoginFailed(errorCode: Int) {
                Log.d("tokenn", "error")
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("odred", "create")

        if (Build.VERSION.SDK_INT > 9) {
            val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }


        val loginButton = view.findViewById<Button>(R.id.login)


        loginButton.isEnabled = true
        loginButton.visibility = View.VISIBLE
        loginButton.setOnClickListener {
            it.isClickable = true
            activity?.let {
                VK.login(
                    it, arrayListOf(
                        VKScope.FRIENDS,
                        VKScope.EMAIL,
                        VKScope.WALL,
                        VKScope.PHOTOS,
                        VKScope.MESSAGES,
                        VKScope.DOCS,
                        VKScope.GROUPS,
                        VKScope.PAGES,
                        VKScope.MESSAGES,
                        VKScope.OFFLINE
                    )
                )
            }
        }

        Log.d("tokenn", "hello")


    }

    private fun updateUiWithUser() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showLoginFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }
}