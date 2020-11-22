package com.inplace.chat

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inplace.R
import com.inplace.chat.DateParser.convertDateToString
import com.inplace.chat.DateParser.convertTimeToString
import com.inplace.chat.DateParser.getDateAsUnix
import com.inplace.models.Message
import com.inplace.models.Source
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet


class ChatFragment() : Fragment() {

    lateinit var chatViewModel: ChatViewModel
    lateinit var messages: MutableList<Message>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.chat_messages_container)

//        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)


        //testing changing info about user in toolbar
        val toolbar: Toolbar = view.findViewById(R.id.chat_toolbar)
        val username: TextView = toolbar.findViewById(R.id.chat_user_name)
        val userActivity: TextView = toolbar.findViewById(R.id.chat_user_activity)

//        chatViewModel.getMessages()

        var messages: MutableList<Message> = ArrayList()
        messages.add(Message(1606049697107, "hello", 123, false, Source.VK))
        messages.add(Message(1606049709137, "Что делаешь?Что делаешь?Что делаешь?Что делаешь?Что делаешь?", 123, true, Source.VK))
        messages.add(Message(1606049711685, "ничегоЧто делаешь?Что делаешь?Что делаешь?Что делаешь?Что делаешь?Что делаешь?", 123, false, Source.VK))
        messages.add(Message(1606049714080, "hello", 123, true, Source.TELEGRAM))
        messages.add(Message(1606049716352, "3", 123, true, Source.VK))
        messages.add(Message(1606049718754, "3", 123, true, Source.VK))
        messages.add(Message(1606049721418, "hello", 123, false, Source.VK))
        messages.add(Message(1606049724546, "Что делаешь?Что делаешь?Что делаешь?Что делаешь?", 123, true, Source.VK))
        messages.add(Message(1606049726821, "ничего", 123, false, Source.TELEGRAM))
        messages.add(Message(1606049729778, "helloЧто делаешь?Что делаешь?Что делаешь?Что делаешь?", 123, true, Source.VK))
        messages.add(Message(1606049732357, "3Что делаешь?Что делаешь?Что делаешь?Что делаешь?Что делаешь?Что делаешь?Что делаешь?", 123, true, Source.VK))
        messages.add(Message(1606049735046, "3", 123, true, Source.VK))
        messages.add(Message(1606049737544, "hello", 123, false, Source.VK))
        messages.add(Message(1606049739994, "Что делаешь?", 123, true, Source.VK))
        messages.add(Message(1606049742804, "ничего", 123, false, Source.VK))
        messages.add(Message(1606049745759, "hello", 123, true, Source.VK))
        messages.add(Message(1606049748306, "3", 123, true, Source.VK))
        messages.add(Message(1606049750679, "3", 123, true, Source.VK))



        var resList = sortMessagesByDate(messages)

        recycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,true)
        val adapter = ChatAdapter(resList)
        recycler.adapter = adapter


        //setting toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        val messageEditText: EditText = view.findViewById(R.id.chat_message_editText)
        val sendButton: RelativeLayout = view.findViewById(R.id.chat_send_button)
        val voiceButton: ImageView = view.findViewById(R.id.chat_voice_button)

        sendButton.setOnClickListener {
            //TODO: write message send logic
            messageEditText.setText("")
        }

        //Adding a watcher to replace the send/voice buttons
        messageEditText.doOnTextChanged { text, _, _, _ ->
            when (text?.length ?: 0) {
                0 -> {
                    sendButton.visibility = View.GONE
                    voiceButton.visibility = View.VISIBLE
                }
                else -> {
                    sendButton.visibility = View.VISIBLE
                    voiceButton.visibility = View.GONE
                }
            }
        }

    }

    private fun sortMessagesByDate(messagesList: List<Message>):MutableList<ListObject> {
        val sortedList: List<Message> = messagesList.sortedWith(compareBy { it.date })
        val groupedHashMap: TreeMap<Long, MutableSet<Message>> = TreeMap()
        var list: MutableSet<Message>
        for (message in sortedList) {
            val hashMapKey = getDateAsUnix(message.date)

            if (groupedHashMap.containsKey(hashMapKey)) {
                // The key is already in the HashMap; add the object
                // against the existing key.
                groupedHashMap[hashMapKey]!!.add(message)
            } else {
                // The key is not there in the HashMap; create a new key-value pair
                list = LinkedHashSet()
                list.add(message)
                groupedHashMap[hashMapKey] = list
            }
        }

        // We linearly add every item into the consolidatedList.
        val resultList: MutableList<ListObject> = ArrayList()
        for (date in groupedHashMap.keys) {
            val dateItem = DateObject()
            dateItem.date = convertDateToString(date)
            resultList.add(dateItem)
            for (message in groupedHashMap[date]!!) {
                val generalItem = MessagesObject()
                generalItem.message=message
                resultList.add(generalItem)
            }
        }

        return resultList.asReversed()
    }

}

