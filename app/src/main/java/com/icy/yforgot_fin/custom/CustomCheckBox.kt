package com.icy.yforgot_fin.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatCheckBox
import com.icy.yforgot_fin.R
import com.icy.yforgot_fin.task.Task

@SuppressLint("UseCompatLoadingForDrawables")
class CustomCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatCheckBox(context, attrs, defStyleAttr) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)

    init {
        Log.e("CustomCheckBox", "CustomCheckBox initialized")
        setOnCheckedChangeListener { _, isChecked ->
            task?.isCompleted = isChecked
            saveTaskStateToPreferences(isChecked)
            Log.e("CustomCheckBox", "Checkbox checked state: $isChecked")
        }
        // Set the button drawable to a custom checkbox drawable
        buttonDrawable = context.getDrawable(R.drawable.custom_checkbox_selector)
    }

    var task: Task? = null
        set(value) {
            field = value
            isChecked = loadTaskStateFromPreferences() ?: value?.isCompleted ?: false
            Log.e("CustomCheckBox", "Task set: $value")
        }

    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)
        task?.isCompleted = checked
        saveTaskStateToPreferences(checked)
        Log.e("CustomCheckBox", "Checkbox setChecked: $checked")
    }

    override fun toggle() {
        super.toggle()
        task?.isCompleted = isChecked
        saveTaskStateToPreferences(isChecked)
        Log.e("CustomCheckBox", "Checkbox toggled: $isChecked")
    }

    private fun saveTaskStateToPreferences(isChecked: Boolean) {
        task?.let {
            sharedPreferences.edit()
                .putBoolean(it.id.toString(), isChecked)
                .apply()
        }
    }

    private fun loadTaskStateFromPreferences(): Boolean? {
        return task?.let {
            if (sharedPreferences.contains(it.id.toString())) {
                sharedPreferences.getBoolean(it.id.toString(), false)
            } else {
                null
            }
        }
    }
}