package com.yuriy.notificationandgps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    interface OnButtonsClickListener {
        fun onLocationButtonClick()
        fun onNotificationButtonClick()
    }

    private lateinit var callback: OnButtonsClickListener

    fun setOnButtonsClickListener(callback: OnButtonsClickListener) {
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        id_location_button.setOnClickListener {
            callback.onLocationButtonClick()
        }

        id_notification_button.setOnClickListener {
            callback.onNotificationButtonClick()
        }
    }
}