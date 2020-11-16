package com.inplace.chats


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.inplace.R


class NumberFragment : Fragment() {

    private var number: Int = 0
    private var color: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            number = getInt(NUMBER, 0)
            color = getInt(COLOR, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        savedInstanceState?.apply {
            number = getInt(NUMBER)
            color = getInt(COLOR)
        }

        return inflater.inflate(R.layout.fragment_number, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = view.findViewById<TextView>(R.id.number)
        textView.text = number.toString()
        textView.setTextColor(color)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(NUMBER, number)
        outState.putInt(COLOR, color)
    }


    companion object {

        const val NUMBER = "number"
        const val COLOR = "color"

        @JvmStatic
        fun newInstance(number: Int, color: Int) =
            NumberFragment().apply {
                arguments = Bundle(2).apply {
                    putInt(NUMBER, number)
                    putInt(COLOR, color)
                }
            }
    }
}