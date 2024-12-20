package com.example.storyapp.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class UsernameEditText : TextInputEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var isError = false

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateUsername(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateUsername(username: String) {
        isError = when {
            username.isEmpty() -> {
                (parent.parent as? TextInputLayout)?.error = "Username cannot be empty"
                true
            }
            username.length < 3 -> {
                (parent.parent as? TextInputLayout)?.error = "Username must be at least 3 characters"
                true
            }
            else -> {
                (parent.parent as? TextInputLayout)?.error = null
                false
            }
        }
    }

    fun isValid(): Boolean = !isError && !text.isNullOrEmpty()
}

class EmailEditText : TextInputEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var isError = false

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateEmail(email: String) {
        isError = when {
            email.isEmpty() -> {
                (parent.parent as? TextInputLayout)?.error = "Email cannot be empty"
                true
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                (parent.parent as? TextInputLayout)?.error = "Invalid email format"
                true
            }
            else -> {
                (parent.parent as? TextInputLayout)?.error = null
                false
            }
        }
    }

    fun isValid(): Boolean = !isError && !text.isNullOrEmpty()
}

class PasswordEditText : TextInputEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var isError = false

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validatePassword(password: String) {
        isError = when {
            password.isEmpty() -> {
                (parent.parent as? TextInputLayout)?.error = "Password cannot be empty"
                true
            }
            password.length < 8 -> {
                (parent.parent as? TextInputLayout)?.error = "Password must be at least 8 characters"
                true
            }
            else -> {
                (parent.parent as? TextInputLayout)?.error = null
                false
            }
        }
    }

    fun isValid(): Boolean = !isError && !text.isNullOrEmpty()
}