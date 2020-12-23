package com.inplace.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
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
import com.inplace.R
import com.inplace.db.AppDatabase
import com.inplace.models.ChatType
import com.inplace.models.Message
import com.inplace.models.Source
import com.inplace.models.SuperChat
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import kotlin.random.Random


class ChatFragment : Fragment(), OnImageRemoveClickListener {
    private val AVATAR_STATE = "avatarState"
    private val EDIT_TEXT_STATE = "editTextState"
    private val IMAGES_PICK_CODE = 0
    private var avatarIsLoaded = false
    private var sendMessage = false
    private val database = this.activity?.applicationContext?.let { AppDatabase.getInstance(it) }
    private val messageDao = database?.getMessageDao()

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

    private lateinit var imageUris: ArrayList<Uri>

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("qwerty","onAcativityResult")
        if (requestCode == IMAGES_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("qwerty","imagesClear")
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

        val chatAdapter = ChatAdapter()
        pickedImagesAdapter = PickedImagesAdapter(context, this)
        pickedImagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = pickedImagesAdapter
        }

        val vkChats = chat.vkChats
        val tgChats = chat.telegramChats

        //setting toolbar info
        if (vkChats.size == 1 && vkChats[0].type == ChatType.PRIVATE) {
            username.text = vkChats[0].title
            userActivity.text = "online"
            if (!avatarIsLoaded) {
                chatViewModel.fetchAvatar(vkChats[0].avatarUrl)
                avatarIsLoaded = true
            }
        } else if(vkChats.size == 1 && vkChats[0].type == ChatType.GROUP) {
            username.text = vkChats[0].title
            userActivity.text = "${vkChats[0].sobesedniks.size} members"
            if (!avatarIsLoaded) {
                chatViewModel.fetchAvatar(vkChats[0].avatarUrl)
                avatarIsLoaded = true
            }
        }

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        recycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            isNestedScrollingEnabled = false
            adapter = chatAdapter
//            adapter = chatAdapter.withLoadStateFooter(
//                    footer = ChatLoadStateAdapter { chatAdapter.retry() }
//            )
        }


        lifecycleScope.launch {
            chatViewModel.getMessages(vkChats[0].chatID).observe(viewLifecycleOwner) {
                chatAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                if (sendMessage){
                    recycler.smoothScrollBy(0,0)
                    sendMessage = false
                }
            }
        }

        chatViewModel.getAvatar().observe(viewLifecycleOwner) {
            avatar.setImageBitmap(it)
        }


        sendButton.setOnClickListener {
            val messageText: String = messageEditText.text.toString()
            //TODO send telegram messages

            val message = Message(
                Random.nextInt(),
                DateParser.getNowDate(),
                messageText,
                0,
                vkChats[0].chatID,
                true,
                Source.VK,
                false,
                ArrayList(imageUris.map { it.toString() })
            )

            chatViewModel.sendMessage(message)

//            chatAdapter.refresh()

            messageEditText.setText("")
            clearPickedImages()
            sendMessage = true
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

    private fun clearPickedImages(){
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
}

interface OnImageRemoveClickListener {
    fun removeImage(uri: Uri)
}

