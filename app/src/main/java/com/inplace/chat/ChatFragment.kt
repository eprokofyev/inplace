package com.inplace.chat

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inplace.R
import com.inplace.chat.DateParser.convertDateToString
import com.inplace.chat.DateParser.getDateAsUnix
import com.inplace.models.*
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet


class ChatFragment : Fragment() {
    private val AVATAR_STATE = "avatarState"
    private val EDIT_TEXT_STATE = "editTextState"

    lateinit var messageEditText: EditText
    lateinit var toolbar: Toolbar
    lateinit var avatar: CircleImageView
    lateinit var username: TextView
    lateinit var userActivity: TextView

    lateinit var chatViewModel: ChatViewModel
    var messages: MutableList<Message> = mutableListOf()
    var chat: Chat
    private var avatarIsLoaded = false

    init {
        val sobesednikList = mutableListOf<Sobesednik>(Sobesednik("Rick Sanchez", "https://avatarfiles.alphacoders.com/131/131749.png", null, null, "123"))
        val user = User("Me", "1234", null, null, 1233)
        chat = Chat(user, sobesednikList, messages, true, "1234", "1234", Source.VK, "123")
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
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(AVATAR_STATE, avatarIsLoaded)
        outState.putString(EDIT_TEXT_STATE, messageEditText.text.toString())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageEditText = view.findViewById(R.id.chat_message_editText)
        toolbar = view.findViewById(R.id.chat_toolbar)
        avatar = toolbar.findViewById(R.id.chat_user_avatar)
        username = toolbar.findViewById(R.id.chat_user_name)
        userActivity = toolbar.findViewById(R.id.chat_user_activity)

        avatarIsLoaded = savedInstanceState?.getBoolean(AVATAR_STATE) ?: false
        messageEditText.append(savedInstanceState?.getString(EDIT_TEXT_STATE) ?: "")

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        val sobesedniks = chat.sobesedniks


        if (sobesedniks.size == 1) {
            username.text = sobesedniks[0].name
            userActivity.text = sobesedniks[0].vk?.activeTime
                    ?: sobesedniks[0].telegram?.activeTime ?: ""
            if (!avatarIsLoaded) {
                chatViewModel.fetchAvatar(sobesedniks[0].avatar)
                avatarIsLoaded = true
            }
        }

        val resList = sortMessagesByDate(messages)
        val adapter = ChatAdapter(resList)

        chatViewModel.getAvatar().observe(viewLifecycleOwner) {
            avatar.setImageBitmap(it)
        }

        chatViewModel.getMessages().observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val listObject = sortMessagesByDate(it)
                adapter.setMessages(listObject)
            }
        }


        val recycler: RecyclerView = view.findViewById(R.id.chat_messages_container)


        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)

        recycler.adapter = adapter


        //setting toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

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

    private fun sortMessagesByDate(messagesList: List<Message>): MutableList<ListObject> {
        val sortedList: List<Message> = messagesList.sortedWith(compareBy { it.date })
        val groupedTreeMap: TreeMap<Long, MutableSet<Message>> = TreeMap()
        var list: MutableSet<Message>
        for (message in sortedList) {
            val treeMapKey = getDateAsUnix(message.date)

            if (groupedTreeMap.containsKey(treeMapKey)) {
                // The key is already in the HashMap; add the object
                // against the existing key.
                groupedTreeMap[treeMapKey]!!.add(message)
            } else {
                // The key is not there in the HashMap; create a new key-value pair
                list = LinkedHashSet()
                list.add(message)
                groupedTreeMap[treeMapKey] = list
            }
        }

        // We linearly add every item into the sortedList.
        val resultList: MutableList<ListObject> = ArrayList()
        for (date in groupedTreeMap.keys) {
            val dateItem = DateObject()
            dateItem.date = convertDateToString(date)
            resultList.add(dateItem)
            for (message in groupedTreeMap[date]!!) {
                val generalItem = MessagesObject()
                generalItem.message = message
                resultList.add(generalItem)
            }
        }

        return resultList.asReversed()
    }

}

