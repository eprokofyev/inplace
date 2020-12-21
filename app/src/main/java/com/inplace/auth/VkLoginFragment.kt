package com.inplace.auth

import android.widget.Button
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKScope
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inplace.R


class VkLoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vk_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val loginButton = view.findViewById<Button>(R.id.login)


        loginButton.isEnabled = true
        loginButton.visibility = View.VISIBLE
        loginButton.setOnClickListener {
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
    }
}