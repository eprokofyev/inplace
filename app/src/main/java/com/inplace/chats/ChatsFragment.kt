package com.inplace.chats

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inplace.R
import com.inplace.api.vk.ApiVk
import com.inplace.api.vk.VkUser
import com.inplace.models.*
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class ChatsFragment : Fragment() {

    lateinit var recycler: RecyclerView

    lateinit var toolbar: Toolbar

    private lateinit var listener: SwitcherInterface

    private var chatsViewModel: ChatsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (context is SwitcherInterface) {
            listener = context as SwitcherInterface
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.chats_menu, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        superUser = savedInstanceState?.getParcelable(key) ?: arguments?.getParcelable(key)

        if (Build.VERSION.SDK_INT > 9) {
            val policy =
                    StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        if (superUser == null) {
            Log.d("ApiVK", "start of auth request")

            // todo hardcore name and pass
            val res = ApiVk.getMe()
            Log.d("user", res.toString())
            val vk = res.result
            vk?.let {
                Log.d("user", "not null")
                val user = VKUser(it.id.toLong(), it.firstName, it.lastName, it.photo200Square)
                superUser = SuperUser(user.name, user.lastName, user.avatarURL, user, null)
            }
        }

        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(key, superUser)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.chats_toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        recycler = view.findViewById<RecyclerView>(R.id.list)
        /*val b = view.findViewById<View>(R.id.btn_do_it)
        b.setOnClickListener {
            DataSource.add()
            ++size
            recycler.adapter?.notifyItemInserted(DataSource.size())
        }
         */


        val chatsAdapter = ChatsRecyclerViewAdapter(listener, activity)

        recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatsAdapter
        }


        chatsViewModel = activity?.let { ViewModelProvider(it) }?.get(
            ChatsViewModel::class.java
        )

        lifecycleScope.launch {
            chatsViewModel?.apply {
                getChats().observe(viewLifecycleOwner) {
                chatsAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
            }
        }

    }

    companion object {
        private const val key = "user"
        var superUser: SuperUser? = null

        fun newInstance() = ChatsFragment().apply {
            if (arguments == null) {
                arguments = Bundle(1).apply {
                    putParcelable(key, superUser)
                }
            }
        }
    }

}
