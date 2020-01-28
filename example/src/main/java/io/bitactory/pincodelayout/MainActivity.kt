package io.bitactory.pincodelayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.hiddenStateButton
import kotlinx.android.synthetic.main.activity_main.pinCodeLayout

class MainActivity : AppCompatActivity() {

    var hidden = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hiddenStateButton.setOnClickListener {
            hidden = !hidden
            pinCodeLayout.setHiddenState(hidden)
        }
    }
}
