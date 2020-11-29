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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inplace.R
import com.inplace.chat.DateParser.getNowDate
import com.inplace.models.Chat
import com.inplace.models.Message
import com.inplace.models.Source
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch


class ChatFragment : Fragment() {
    private val AVATAR_STATE = "avatarState"
    private val EDIT_TEXT_STATE = "editTextState"
    private var avatarIsLoaded = false

    lateinit var chat: Chat
    lateinit var messageEditText: EditText
    lateinit var toolbar: Toolbar
    lateinit var avatar: CircleImageView
    lateinit var username: TextView
    lateinit var userActivity: TextView
    lateinit var recycler: RecyclerView
    lateinit var chatViewModel: ChatViewModel
    lateinit var sendButton: RelativeLayout
    lateinit var voiceButton: ImageView


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
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.chat_menu, menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(AVATAR_STATE, avatarIsLoaded)
        outState.putString(EDIT_TEXT_STATE, messageEditText.text.toString())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chat = arguments?.getParcelable("chat")!!
        avatarIsLoaded = savedInstanceState?.getBoolean(AVATAR_STATE) ?: false
        messageEditText = view.findViewById(R.id.chat_message_editText)
        messageEditText.append(savedInstanceState?.getString(EDIT_TEXT_STATE) ?: "")
        toolbar = view.findViewById(R.id.chat_toolbar)
        avatar = toolbar.findViewById(R.id.chat_user_avatar)
        username = toolbar.findViewById(R.id.chat_user_name)
        userActivity = toolbar.findViewById(R.id.chat_user_activity)
        recycler = view.findViewById(R.id.chat_messages_container)
        sendButton = view.findViewById(R.id.chat_send_button)
        voiceButton = view.findViewById(R.id.chat_voice_button)

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        val chatAdapter = ChatAdapter()

        val sobesedniks = chat.sobesedniks

        //setting toolbar info
        if (sobesedniks.size == 1) {
            username.text = sobesedniks[0].name
            userActivity.text = sobesedniks[0].vk?.activeTime
                ?: sobesedniks[0].telegram?.activeTime ?: ""
            if (!avatarIsLoaded) {
                chatViewModel.fetchAvatar(sobesedniks[0].avatar)
                avatarIsLoaded = true
            }
        } else {
            //TODO group chat
        }

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        recycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            setHasFixedSize(true)
            adapter = chatAdapter
//            adapter = chatAdapter.withLoadStateHeader(
//                    header = ChatLoadStateAdapter { chatAdapter.retry() }
//            )
        }

        lifecycleScope.launch {
            chatViewModel.getMessages(chat.conversationVkId.toInt()).observe(viewLifecycleOwner) {
                chatAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }

        chatViewModel.getAvatar().observe(viewLifecycleOwner) {
            avatar.setImageBitmap(it)
        }


        sendButton.setOnClickListener {
            val messageText: String = messageEditText.text.toString()
            //TODO send telegram messages
            val message = Message(-1, getNowDate(), messageText, chat.user.id, true, Source.VK)

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

    companion object {
        fun newInstance(chat: Chat): ChatFragment {
            val fragment = ChatFragment()
            val bundle = Bundle()
            bundle.putParcelable("chat", chat)
            fragment.arguments = bundle
            return fragment
        }
    }

}

