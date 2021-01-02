package com.inplace.chat

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.inplace.R
import com.inplace.api.vk.ApiVk
import com.inplace.api.vk.VkUser
import com.inplace.models.*
import com.inplace.services.NotificationService
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import kotlin.random.Random


class ChatFragment : Fragment(), OnImageRemoveClickListener, OnUnreadMessageSight {
    private val AVATAR_STATE = "avatarState"
    private val EDIT_TEXT_STATE = "editTextState"
    private val IMAGES_PICK_CODE = 0
    private var avatarIsLoaded = false

    lateinit var chat: SuperChat
    lateinit var messageEditText: EditText
    lateinit var toolbar: Toolbar
    lateinit var avatar: CircleImageView
    lateinit var username: TextView
    lateinit var userActivity: TextView
    lateinit var recycler: RecyclerView
    lateinit var chatViewModel: ChatViewModel
    lateinit var sendButton: RelativeLayout
    lateinit var voiceButton: ImageView
    lateinit var sendImageButton: ImageView
    lateinit var pickedImagesRecyclerView: RecyclerView
    lateinit var pickedImagesAdapter: PickedImagesAdapter
    lateinit var chatAdapter: ChatAdapter
    private var myID:Int = 0

    private lateinit var imageUris: ArrayList<Uri>

    @ExperimentalPagingApi
    private val newMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val newMessages =
                intent?.getParcelableArrayListExtra<Message>(NotificationService.EXTRAS_NAME)?.map {
                    Message(
                        it.messageID,
                        it.date * 1000,
                        it.text,
                        it.userID,
                        it.chatID,
                        it.myMsg,
                        it.fromMessenger,
                        it.status,
                        it.isRead,
                        it.photos,
                        it.userName
                    )
                }
            val chatID = chat.vkChats[0].chatID
            val inMessages =
                newMessages?.filter { it.chatID == chatID && !it.myMsg }
            val outMessages = newMessages?.filter { it.myMsg }
            if (outMessages != null && outMessages.isNotEmpty()) {
                chatViewModel.insertMessages(outMessages)
            }
            val newOutRead: Int
            var newInMessages: List<Message>
            Log.d("newMessage", "newMessage: " + newMessages.toString())
            if (inMessages != null && inMessages.isNotEmpty()) {
                if (inMessages.size == 1 && inMessages[0].isRead) {
                    newOutRead = inMessages[0].messageID
                    chatAdapter.updateOutRead(newOutRead)
                    chatViewModel.updateOutRead(newOutRead,chatID)
                } else {
                    chatViewModel.insertMessages(inMessages)
                    recycler.smoothScrollToPosition(0)
                }
                Log.d("newMessage", "thisChat: " + inMessages.toString())

            }
        }

    }

    @ExperimentalPagingApi
    override fun onResume() {
        super.onResume()
        Log.d("newMessage", "registerReceiver")
        activity?.registerReceiver(
            newMessageReceiver,
            IntentFilter(NotificationService.BROADCAST_ACTION)
        )
    }

//    @ExperimentalPagingApi
//    override fun onPause() {
//        super.onPause()
//        Log.d("newMessage", "unregisterReceiver")
//        activity?.unregisterReceiver(newMessageReceiver)
//    }

    @ExperimentalPagingApi
    override fun onStop() {
        super.onStop()
        NotificationService.chatIdToNotShow = -1
        Log.d("newMessage", "unregisterReceiver")
        activity?.unregisterReceiver(newMessageReceiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGES_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            imageUris.clear()
            val clipData = data.clipData
            val resultData = data.data
            if (clipData != null) {
                val imagesCount = clipData.itemCount
                for (i in 0 until imagesCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    imageUris.add(imageUri)
                }
            } else if (resultData != null) {
                imageUris.add(resultData)
            }
            showPickedImages(imageUris)
        }
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
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.chat_menu, menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(AVATAR_STATE, avatarIsLoaded)
        outState.putString(EDIT_TEXT_STATE, messageEditText.text.toString())
    }


    @ExperimentalPagingApi
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
        sendImageButton = view.findViewById(R.id.send_image_button)
        pickedImagesRecyclerView = view.findViewById(R.id.pickedImages_recycler)

        imageUris = ArrayList()

        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        val vkChat = chat.vkChats[0]

        chatAdapter = ChatAdapter(vkChat.inRead, vkChat.outRead, this)

        pickedImagesAdapter = PickedImagesAdapter(context, this)
        pickedImagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = pickedImagesAdapter
        }

        chatViewModel.fetchMe()

        chatViewModel.getMe().observe(viewLifecycleOwner){
            myID = it
        }


        //setting toolbar info
        if (vkChat.type == ChatType.PRIVATE) {
            username.text = vkChat.title
            userActivity.text = "online"
            if (!avatarIsLoaded) {
                chatViewModel.fetchAvatar(vkChat.avatarUrl)
                avatarIsLoaded = true
            }
        } else {
            username.text = vkChat.title
            userActivity.text = "${vkChat.sobesedniks.size} members"
            if (!avatarIsLoaded) {
                chatViewModel.fetchAvatar(vkChat.avatarUrl)
                avatarIsLoaded = true
            }
        }

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)

        (recycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recycler.apply {
            layoutManager = linearLayoutManager
            isNestedScrollingEnabled = false
            adapter = chatAdapter
//            adapter = chatAdapter.withLoadStateFooter(
//                    footer = ChatLoadStateAdapter { chatAdapter.retry() }
//            )
        }

        lifecycleScope.launch {
            chatViewModel.getMessages(vkChat.chatID).observe(viewLifecycleOwner) {
                chatAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }

        chatViewModel.apply {
            getAvatar().observe(viewLifecycleOwner) {
                avatar.setImageBitmap(it)
            }
            getRefreshMessage().observe(viewLifecycleOwner) {
                recycler.smoothScrollToPosition(0)
            }
        }





        sendButton.setOnClickListener {
            if (imageUris.size > 10) {
                Toast.makeText(
                    context,
                    context?.resources?.getString(R.string.too_many_photos_to_send),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val messageText: String = messageEditText.text.toString()
                //TODO send telegram messages

                var a = Random.nextInt(0, Int.MAX_VALUE) * -1
                Log.d("random", a.toString())
                val message = Message(
                    a,
                    DateParser.getNowDate(),
                    messageText,
                    myID.toLong(),
                    vkChat.chatID,
                    true,
                    Source.VK,
                    MessageStatus.SENDING,
                    false,
                    ArrayList(imageUris.map { it.toString() })
                )

                chatViewModel.sendMessage(1, message)

                messageEditText.setText("")
                clearPickedImages()
            }
        }

        sendImageButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select image"), IMAGES_PICK_CODE)
        }

        //Adding a watcher to replace the send/voice buttons
        messageEditText.doOnTextChanged { text, _, _, _ ->
            if (text?.length ?: 0 > 0) {
                sendButton.isVisible = true
                voiceButton.isVisible = false
            } else if (text?.length ?: 0 == 0 && imageUris.isEmpty()) {
                sendButton.isVisible = false
                voiceButton.isVisible = true
            }
        }

    }

    private fun clearPickedImages() {
        imageUris.clear()
        pickedImagesRecyclerView.isVisible = false
        sendButton.isVisible = false
        voiceButton.isVisible = true
    }

    private fun showPickedImages(imageUris: ArrayList<Uri>) {
        if (imageUris.size > 0 && pickedImagesRecyclerView.visibility == View.GONE) {
            pickedImagesRecyclerView.isVisible = true
            sendButton.isVisible = true
            voiceButton.isVisible = false

            pickedImagesAdapter.setData(imageUris)
        } else if (imageUris.size > 0) {
            pickedImagesAdapter.setData(imageUris)
        }
    }

    override fun removeImage(uri: Uri) {
        imageUris.remove(uri)
        if (imageUris.size == 0) {
            when (messageEditText.text.isEmpty()) {
                true -> {
                    pickedImagesRecyclerView.isVisible = false
                    sendButton.isVisible = false
                    voiceButton.isVisible = true
                }
                false -> {
                    pickedImagesRecyclerView.isVisible = false
                }
            }
        }
    }


    companion object {
        fun newInstance(chat: SuperChat): ChatFragment {
            val fragment = ChatFragment()
            val bundle = Bundle()
            bundle.putParcelable("chat", chat)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun markAsRead() {
        chatViewModel.markChatAsRead(chat.vkChats[0].chatID)
    }
}

interface OnImageRemoveClickListener {
    fun removeImage(uri: Uri)
}

interface OnUnreadMessageSight {
    fun markAsRead()
}

