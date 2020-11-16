package com.inplace

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ItemFragment : Fragment() {

    private lateinit var listener: SwitcherInterface

    private var size = 0

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

        size = savedInstanceState?.getInt(NUMBERS) ?: (arguments?.getInt(NUMBERS, 0) ?: 0)

        DataSource.create(size)

        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_item_list, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(NUMBERS, size)
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

        with(recycler) {
            layoutManager = LinearLayoutManager(context)

            adapter = MyItemRecyclerViewAdapter(DataSource.get(), listener)
        }

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

        fun newInstance(size: Int) = ItemFragment().apply {
            if (arguments == null) {
                arguments = Bundle(1).apply {
                    putInt(NUMBERS, size)
                }
            }
        }
    }

}