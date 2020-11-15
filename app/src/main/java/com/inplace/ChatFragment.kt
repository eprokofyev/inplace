package com.inplace

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment

class ChatFragment() : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //testing changing info about user in toolbar
        val toolbar: Toolbar = view.findViewById(R.id.chat_toolbar)
        val username: TextView = toolbar.findViewById(R.id.chat_user_name)
        val userActivity: TextView = toolbar.findViewById(R.id.chat_user_activity)
        username.text = "Aiden Pierce"
        userActivity.text = "last seen 5 minutes ago"

        (activity as AppCompatActivity).setSupportActionBar(toolbar)


        //testing message view
        val targetMessage = "Привет!\n" +
                "Предлагаю в воскресенье собраться в дисе, обсудить архитектуру и раскидать задачи. К воскресенью давайте закончим с текущими задачами и посмотрим повнимательнее материалы лекций, чтобы понимать о чём пойдёт речь"
        val hostMessage = "Окей"
        val targetMessageView: View = view.findViewById(R.id.target_message_test)
        ChatFragment.setMessageText(context, targetMessageView, targetMessage)
        val hostMessageView: View = view.findViewById(R.id.host_message_test)
        ChatFragment.setMessageText(context, hostMessageView, hostMessage)



        val messageEditText: EditText = view.findViewById(R.id.chat_message_edittext)
        val sendButton: RelativeLayout = view.findViewById(R.id.chat_send_button)
        val voiceButton: ImageView = view.findViewById(R.id.chat_voice_button)

        sendButton.setOnClickListener {
            ChatFragment.setMessageText(context, hostMessageView, messageEditText.text.toString())
            messageEditText.setText("")
        }

        //Adding a watcher to replace the send/voice buttons
        messageEditText.doOnTextChanged { text, _, _, _ ->
            when(text?.length?:0) {
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
        /**
         * Sets the text of the message
         * to the View consisting of two TextViews
         */
        fun setMessageText(context: Context?, messageView: View, message: String) {
            val upperMessageTextView: TextView = messageView.findViewById(R.id.upper_message_textView)
            val lowerMessageTextView: TextView = messageView.findViewById(R.id.lower_message_textView)

            val messageLines: MutableList<CharSequence> = ArrayList()
            var messageLinesCount: Int
            var isSet = false
            var upperTextViewResult = ""

            upperMessageTextView.text = ""
            lowerMessageTextView.text = ""
            upperMessageTextView.visibility = View.VISIBLE
            lowerMessageTextView.visibility = View.VISIBLE

            if (context != null) {
                val lowerMessageMaxLength = context.resources.getInteger(R.integer.lower_message_maxLength)

                upperMessageTextView.text = message
                upperMessageTextView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        upperMessageTextView.viewTreeObserver.removeOnPreDrawListener(this)

                        if (!isSet) {
                            messageLinesCount = upperMessageTextView.layout.lineCount

                            repeat(messageLinesCount){line ->
                                val start = upperMessageTextView.layout.getLineStart(line)
                                val end = upperMessageTextView.layout.getLineEnd(line)
                                val substring = upperMessageTextView.text.subSequence(start, end)
                                messageLines.add(substring)
                            }

                            messageLines.forEachIndexed { i, it ->
                                if (i == messageLinesCount - 1) {
                                    when {
                                        it.length <= lowerMessageMaxLength -> {
                                            if (messageLinesCount > 1) {
                                                upperMessageTextView.text = upperTextViewResult
                                                lowerMessageTextView.text = it
                                                isSet = true
                                            } else {
                                                upperMessageTextView.text = ""
                                                upperMessageTextView.visibility = View.GONE
                                                lowerMessageTextView.text = it
                                                isSet = true
                                            }
                                        }

                                        else -> {
                                            upperTextViewResult += it
                                            upperMessageTextView.text = upperTextViewResult
                                            lowerMessageTextView.visibility = View.GONE
                                            isSet = true
                                        }

                                    }
                                } else {
                                    upperTextViewResult += it
                                }
                            }
                        }
                        return isSet
                    }
                })
            }
        }
    }

}