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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inplace.R
import com.inplace.api.vk.ApiVK
import com.inplace.api.vk.VkUser
import com.inplace.models.*
import kotlinx.coroutines.launch


class ChatsFragment : Fragment() {

    lateinit var recycler: RecyclerView

    private lateinit var listener: SwitcherInterface

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

        superUser = savedInstanceState?.getParcelable(key) ?: arguments?.getParcelable(key)

        if (Build.VERSION.SDK_INT > 9) {
            val policy =
                    StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        if (superUser == null) {
            Log.d("ApiVK", "start of auth request")

            // todo hardcore name and pass
            val res = ApiVK.getMeSKD()
            Log.d("user", res.toString())
            val vk = res.result?.firstOrNull()
            vk?.let {
                Log.d("user", "not null")
                val user = VKUser(it.id, it.firstName, it.lastName, it.photo200Square)
                superUser = SuperUser(user.name, user.lastName, user.avatarURL, user, null)
            }

        }

        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(key, superUser)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById<RecyclerView>(R.id.list)
        /*val b = view.findViewById<View>(R.id.btn_do_it)
        b.setOnClickListener {
            DataSource.add()
            ++size
            recycler.adapter?.notifyItemInserted(DataSource.size())
        }
         */


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
