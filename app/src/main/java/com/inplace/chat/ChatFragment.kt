package com.inplace.chat

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inplace.R
import com.inplace.chat.DateParser.getNowDate
import com.inplace.models.Chat
import com.inplace.models.Message
import com.inplace.models.Source
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class ChatFragment(var chat: Chat) : Fragment() {
    private val AVATAR_STATE = "avatarState"
    private val EDIT_TEXT_STATE = "editTextState"
    private val messagesPerPageCount = 20
    var messagesStart: Int = 0
    var messagesEnd: Int = messagesPerPageCount


    lateinit var messageEditText: EditText
    lateinit var toolbar: Toolbar
    lateinit var avatar: CircleImageView
    lateinit var username: TextView
    lateinit var userActivity: TextView
    lateinit var recycler: RecyclerView
    lateinit var nestedScrollView: NestedScrollView
    lateinit var messageLoadProgressBar: ProgressBar

    lateinit var chatAdapter: ChatAdapter
    lateinit var chatViewModel: ChatViewModel
    var messages: MutableList<Message> = mutableListOf()
    private var avatarIsLoaded = false
    private var isLoading = false


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
        nestedScrollView = view.findViewById(R.id.nested_scrollView)
        messageLoadProgressBar = view.findViewById(R.id.chat_progressBar)

        chatAdapter = ChatAdapter(LinkedList())

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






        chatViewModel.fetchMessages(chat.conversationVkId.toInt(), messagesStart, messagesEnd)

        chatViewModel.getAvatar().observe(viewLifecycleOwner) {
            avatar.setImageBitmap(it)
        }

        chatViewModel.getMessages().observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val sortedList: MutableList<Message> = it.sortedWith(compareBy { it.date }).toMutableList().asReversed()
                chatAdapter.setMessages(sortedList)
                messageLoadProgressBar.visibility = View.GONE
                if (messagesStart == 0) {
                    scrollToBottom()
                }
                isLoading = false
                messagesStart += it.size
                messagesEnd = messagesStart + messagesPerPageCount
            }
        }


        recycler = view.findViewById(R.id.chat_messages_container)

        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        recycler.adapter = chatAdapter

        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY == 0) {
                messageLoadProgressBar.visibility = View.VISIBLE
                chatViewModel.fetchMessages(chat.conversationVkId.toInt(), messagesStart, messagesEnd)
            }
        })

        //setting toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        val sendButton: RelativeLayout = view.findViewById(R.id.chat_send_button)
        val voiceButton: ImageView = view.findViewById(R.id.chat_voice_button)

        sendButton.setOnClickListener {
            val messageText: String = messageEditText.text.toString()
            val message = Message(getNowDate(), messageText, chat.user.id, true, Source.VK)

            chatAdapter.addMessage(message)
            scrollToBottom()

            //chatViewModel.sendMessage(chat.conversationVkId.toInt(),messageText)

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

    fun scrollToBottom() {
        nestedScrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                nestedScrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val nestedScrollViewHeight = nestedScrollView.height
                if (nestedScrollViewHeight > 0) {
                    val lastView = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)
                    val lastViewBottom = lastView.bottom + nestedScrollView.paddingBottom
                    val deltaScrollY = lastViewBottom - nestedScrollViewHeight - nestedScrollView.scrollY
                    nestedScrollView.scrollBy(0, deltaScrollY)
                }
            }
        })
    }

//    fun newInstance(bundle: Bundle): ChatFragment{
//        val fragment = ChatFragment()
//        fragment.arguments = bundle
//        return fragment
//    }

}

