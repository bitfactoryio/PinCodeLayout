/*
 *
 *  ██████╗ ██╗████████╗███████╗ █████╗  ██████╗████████╗ ██████╗ ██████╗ ██╗   ██╗
 *  ██╔══██╗██║╚══██╔══╝██╔════╝██╔══██╗██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗╚██╗ ██╔╝
 *  ██████╔╝██║   ██║   █████╗  ███████║██║        ██║   ██║   ██║██████╔╝ ╚████╔╝
 *  ██╔══██╗██║   ██║   ██╔══╝  ██╔══██║██║        ██║   ██║   ██║██╔══██╗  ╚██╔╝
 *  ██████╔╝██║   ██║   ██║     ██║  ██║╚██████╗   ██║   ╚██████╔╝██║  ██║   ██║
 *  ╚═════╝ ╚═╝   ╚═╝   ╚═╝     ╚═╝  ╚═╝ ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝   ╚═╝
 *
 *  Copyright (c) 2019 Bitfactory GmbH. All rights reserved.
 *  https://www.bitfactory.io
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are not permitted.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *  FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 *  COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES INCLUDING,
 *  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *  ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 * /
 */

package io.bitfactory.pincodelayout

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ViewSwitcher
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.pin_code_layout.view.*

class PinCodeLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var pinLength = 0
    private val defaultPinLength = 6
    private val alphaPropertyName = "alpha"
    private var animationDuration: Long = 0
    private val defaultAnimationDuration = 500
    private var alphaValueStart = 0f
    private var alphaValueEnd = 1f

    private var pinTextColor = 0

    private var pinBackgroundId = 0
    private var filledPinBackgroundId = 0
    private var inputBackgroundId = 0

    private var pinIsHidden = false
    private val pinIsHiddenDefault = false

    private var pinInterface: PinCodeActions? = null

    private var objAnimator: ObjectAnimator? = null

    private var inactiveColor = Color.WHITE

    private var activeColor = Color.GREEN

    private var attributeArray =
        context.obtainStyledAttributes(attrs, R.styleable.PinCodeLayout, 0, 0)

    private val actionCallback = object : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return false
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
        }
    }

    private val focusChangeListener = object : OnFocusChangeListener {

        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            v as EditText

            if (hasFocus) {
                bottomBar.setBackgroundColor(activeColor)
            }

            if (!hasFocus && v.text.length == pinLength) {
                pinInterface?.onPinFilled()
            }

            if (!hasFocus) {
                bottomBar.setBackgroundColor(inactiveColor)
                endAnimatePin()
                if (v.text.length == pinLength) {
                    objAnimator?.end()
                    objAnimator?.cancel()
                }

                return
            }

            if (v.text.isEmpty()) {
                animatePin(pinLinearLayout[0])
                return
            }

            if (v.text.length == pinLength) {
                animatePin(pinLinearLayout[v.text.length - 1])
            } else {
                animatePin(pinLinearLayout[v.text.length])
            }

        }
    }

    private val textWatcher = object : TextWatcher {
        var textLengthBefore = 0

        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            textLengthBefore = s?.length ?: 0
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            s?.let {

                if (before == 1
                    && count == 0
                ) {

                    clearPin(it.lastIndex + 1)

                    pinInterface?.onPinCleared()
                    return
                }
                if (it.length == pinLength) {
                    //From deeplink
                    if (textLengthBefore == 0) {
                        it.forEachIndexed { i, c ->

                            fillPin(i, c.toString())

                        }
                    } else {

                        fillPin(it.lastIndex, it.last().toString())
                    }

                    closeKeyboard()
                    pinInterface?.onPinEntered(it.toString())
                    return
                }

                if (textLengthBefore < it.length) {

                    fillPin(it.lastIndex, it.last().toString())

                } else {

                    clearPin(it.lastIndex + 1)
                    pinInterface?.onPinCleared()
                }
            }
        }
    }

    init {
        getAttributes()
        inflate(context, R.layout.pin_code_layout, this)
        setupEditText()
        addPins()
        setUpBottomBar()

        isFocusableInTouchMode = false
        isFocusable = true

    }

    private fun setUpBottomBar() {
        bottomBar.setBackgroundColor(inactiveColor)
    }

    private fun setupEditText() {
        invisibleTextInput.apply {
            text?.clear()
            filters = arrayOf(InputFilter.LengthFilter(pinLength), InputFilter.AllCaps())
            addTextChangedListener(textWatcher)
            customSelectionActionModeCallback = actionCallback
            onFocusChangeListener = focusChangeListener
            background = ContextCompat.getDrawable(context, inputBackgroundId)
        }
    }

    private fun addPins() {
        for (x in 0 until pinLength) {
            pinLinearLayout.addView(Pin(context).apply {
                findViewById<View>(R.id.pinIconView).background =
                    ContextCompat.getDrawable(context, pinBackgroundId)
            })
        }
    }

    private fun getAttributes() {

        pinLength = attributeArray.getInteger(R.styleable.PinCodeLayout_pinLength, defaultPinLength)

        animationDuration =
            attributeArray.getInteger(
                R.styleable.PinCodeLayout_animationDuration,
                defaultAnimationDuration
            ).toLong()

        pinTextColor = attributeArray.getColor(
            R.styleable.PinCodeLayout_pinTextColor,
            ContextCompat.getColor(context, R.color.textColor)
        )

        pinIsHidden =
            attributeArray.getBoolean(R.styleable.PinCodeLayout_hidePin, pinIsHiddenDefault)

        pinBackgroundId =
            attributeArray.getResourceId(
                R.styleable.PinCodeLayout_pinIcon,
                R.drawable.pincode_empty
            )

        inputBackgroundId = attributeArray.getResourceId(
            R.styleable.PinCodeLayout_pinBackground,
            R.drawable.pin_background
        )

        filledPinBackgroundId = attributeArray.getResourceId(
            R.styleable.PinCodeLayout_filledPinBackground,
            R.drawable.pincode_filled
        )

        activeColor = attributeArray.getResourceId(R.styleable.PinCodeLayout_activeColor,Color.YELLOW)

        inactiveColor = attributeArray.getResourceId(R.styleable.PinCodeLayout_inactiveColor,Color.RED)

        attributeArray.recycle()
    }

    private fun closeKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(invisibleTextInput.windowToken, 0)
        invisibleTextInput.clearFocus()
    }

    private fun fillPin(index: Int, char: CharSequence) {
        val pin: ViewGroup = pinLinearLayout[index] as ViewGroup
        val currentPin: ViewSwitcher = pin[0] as ViewSwitcher

        if (!pinIsHidden) {
            val pinText: MaterialTextView = currentPin[1] as MaterialTextView
            pinText.text = char
            pinText.setTextColor(pinTextColor)

            if (!currentPin[1].isShown) currentPin.showNext()

        } else {
            currentPin[0].background = ContextCompat.getDrawable(context, filledPinBackgroundId)
        }

        endAnimatePin()
        if (index < pinLength - 1) {
            val nextPinGroup: ViewGroup = pinLinearLayout[index + 1] as ViewGroup
            val nextPin: ViewSwitcher = nextPinGroup[0] as ViewSwitcher
            animatePin(nextPin)
        }
    }

    private fun clearPin(index: Int) {

        val pin: ViewGroup = pinLinearLayout[index] as ViewGroup
        val currentPin: ViewSwitcher = pin[0] as ViewSwitcher
        if (!pinIsHidden && currentPin[1].isShown) {
            currentPin.showPrevious()
        } else {
            currentPin[0].background = ContextCompat.getDrawable(context, pinBackgroundId)
        }
        endAnimatePin()
        animatePin(pin)
    }

    private fun animatePin(mCircle: View) {
        objAnimator =
            ObjectAnimator.ofFloat(mCircle, alphaPropertyName, alphaValueStart, alphaValueEnd)
        objAnimator?.run {
            duration = animationDuration
            repeatMode = ValueAnimator.REVERSE
            repeatCount = Animation.INFINITE
            start()
        }

    }

    private fun endAnimatePin() {
        objAnimator?.end()
        objAnimator?.cancel()
    }

    fun setCallback(pinInterface: PinCodeActions) {
        this.pinInterface = pinInterface
    }

    fun setPinResource(@DrawableRes pinResourceId: Int) {

        pinBackgroundId = pinResourceId

        for (x in 0 until pinLength) {
            pinLinearLayout[x].findViewById<View>(R.id.pinIconView).background =
                ContextCompat.getDrawable(context, pinBackgroundId)
        }
    }

    fun setBackground(@DrawableRes backgroundId: Int) {
        inputBackgroundId = backgroundId
        invisibleTextInput.background = ContextCompat.getDrawable(context, inputBackgroundId)
    }

    fun setActiveColor(@ColorRes colorId: Int){
        activeColor = ContextCompat.getColor(context,colorId)
    }

    fun inActiveColor(@ColorRes colorId: Int){
        inactiveColor = ContextCompat.getColor(context,colorId)
    }

    /*fun setHiddenState(hiddenState : Boolean){

    }*/

    /*fun setPinLength(length: Int) {
        pinLength = length
        invisibleTextInput.text?.clearSpans()
        invisibleTextInput.text?.clear()
        pinLinearLayout.removeAllViews()
        addPins()
    }*/

}