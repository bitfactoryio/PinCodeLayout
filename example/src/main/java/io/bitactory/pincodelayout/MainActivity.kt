package io.bitactory.pincodelayout

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.bitfactory.pincodelayout.PinCodeActions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

	private val callback: PinCodeActions = object : PinCodeActions {
		override fun onPinEntered(pin: String) {
			Log.d("PinCodeLayout", pin)
		}

		override fun onPinCleared() {
		}

		override fun onPinFilled() {
			Toast.makeText(applicationContext, "Pin Filled", Toast.LENGTH_SHORT).show()
		}
	}

	var hidden = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		hiddenStateButton.setOnClickListener {
			hidden = !hidden
			pinCodeLayout.setHiddenState(hidden)
		}
		pinCodeLayout.setCallback(callback)

		/*
		pinCodeLayout.setActiveBarColor(android.R.color.transparent)
		pinCodeLayout.setInActiveBarColor(android.R.color.transparent)
		pinCodeLayout.setInputBackground(android.R.color.transparent)
		pinCodeLayout.setPinTextColor(R.color.black)
		pinCodeLayout.setUnfilledPinIcon(R.drawable.ic_dot_empty)
		pinCodeLayout.setFilledPinIcon(R.drawable.ic_dot_filled)
		pinCodeLayout.setAnimationDuration(750L)
		*/
	}
}
