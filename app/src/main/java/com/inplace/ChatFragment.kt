package com.inplace

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

class ChatFragment() : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("myLogs", "onCreateView")
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("myLogs", "onCreateOptionsMenu")
        inflater.inflate(R.menu.chat_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("myLogs", "onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        //testing changing info about user in toolbar
        val toolbar: Toolbar = view.findViewById(R.id.chat_toolbar)
        val username: TextView = toolbar.findViewById(R.id.chat_user_name)
        val userActivity: TextView = toolbar.findViewById(R.id.chat_user_activity)
        username.text = "Aiden Pierce"
        userActivity.text = "last seen 5 minutes ago"
        //up here)


        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        val messageEditText: EditText = view.findViewById(R.id.chat_message_edittext)
        val sendButton: RelativeLayout = view.findViewById(R.id.chat_send_button)
        val voiceButton: ImageView = view.findViewById(R.id.chat_voice_button)

        //Adding a watcher to replace the send/voice buttons
        messageEditText.addTextChangedListener(object: TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("myLogs", ("CharSequence: "+s?.length) as String)
                when{
                    s?.length?:0 > 0 -> {
                        sendButton.visibility = View.VISIBLE
                        voiceButton.visibility = View.GONE
                    }

                    s?.length?:0 < 1 -> {
                        sendButton.visibility = View.GONE
                        voiceButton.visibility = View.VISIBLE
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }

        })
    }
}