package com.inplace.chats

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inplace.R
import com.inplace.api.vk.ApiVK
import com.inplace.api.vk.VkUser
import com.inplace.models.Chat
import com.inplace.models.User
import com.inplace.models.UserVK


class ChatsFragment : Fragment() {

    private lateinit var listener: SwitcherInterface

    private var chatsViewModel: ChatsViewModel? = null

    private var size = 0


    private var st: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (context is SwitcherInterface) {
            listener = context as SwitcherInterface
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        savedInstanceState?.let { st = it.getBoolean("st") }

        if (Build.VERSION.SDK_INT > 9) {
            val policy =
                    StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        if (user == null) {
            Log.d("ApiVK", "start of auth request")

            // todo hardcore name and pass
            val name: String = "89132776413"
            val pass: String = "1q2w3e4r123456789"

            val loginResult = ApiVK.login(name, pass)
            Log.d("ApiVK", "end of auth request")

            val res = ApiVK.getMe()
            if (res.result is VkUser) {
                val u = res.result as VkUser
                val vk = UserVK(
                    u.firstName + u.lastName,
                    u.photo200Square,
                    "8132776413",
                    "token",
                    u.id.toString(),
                    "email",
                    0
                )
                user = User(
                    vk.name,
                    vk.avatar,
                    vk,
                    null,
                    0,
                )

            }
            st = true
        }

        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean("st", st)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.list)
        /*val b = view.findViewById<View>(R.id.btn_do_it)
        b.setOnClickListener {
            DataSource.add()
            ++size
            recycler.adapter?.notifyItemInserted(DataSource.size())
        }
         */


        val adapter = ChatsRecyclerViewAdapter(listener)

        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        val observer: Observer<MutableList<Chat>> = Observer<MutableList<Chat>> { chats ->
            if (chats != null) {
                adapter.setChats(chats)
            }
        }

        chatsViewModel = activity?.let { ViewModelProvider(it) }?.get(
            ChatsViewModel::class.java
        )

        chatsViewModel?.refresh()

        chatsViewModel?.getChats()?.observe(viewLifecycleOwner, observer)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chats_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //get item id to handle item click

        return super.onOptionsItemSelected(item)
    }


    companion object {
        private const val NUMBERS = "numbers"
        var user: User? = null

        fun newInstance() = ChatsFragment().apply {
            if (arguments == null) {
                arguments = Bundle(1).apply {
                    putInt(NUMBERS, size)
                }
            }
        }
    }

}
