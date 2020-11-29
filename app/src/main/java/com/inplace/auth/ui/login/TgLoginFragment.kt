package com.inplace.auth.ui.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inplace.R

class TgLoginFragment : Fragment() {

    companion object {
        fun newInstance() = TgLoginFragment()
    }

    private lateinit var viewModel: TgLoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tg_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TgLoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

}