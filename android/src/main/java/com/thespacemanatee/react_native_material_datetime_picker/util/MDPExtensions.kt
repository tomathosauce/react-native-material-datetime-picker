package com.thespacemanatee.react_native_material_datetime_picker.util

import android.annotation.SuppressLint
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.thespacemanatee.react_native_material_datetime_picker.model.MDPArguments
import com.thespacemanatee.react_native_material_datetime_picker.model.MDPDate
import com.thespacemanatee.react_native_material_datetime_picker.util.MDPConstants.KEY_ALLOWED_DATES
import com.thespacemanatee.react_native_material_datetime_picker.util.MDPConstants.KEY_END_DATE
import com.thespacemanatee.react_native_material_datetime_picker.util.MDPConstants.KEY_FIRST_DAY_OF_WEEK
import com.thespacemanatee.react_native_material_datetime_picker.util.MDPConstants.KEY_FULLSCREEN
import com.thespacemanatee.react_native_material_datetime_picker.util.MDPConstants.KEY_INPUT_MODE
import com.thespacemanatee.react_native_material_datetime_picker.util.MDPConstants.KEY_IS_24_HOUR
import com.thespacemanatee.react_native_material_datetime_picker.util.MDPConstants.KEY_MAX_DATE
import com.thespacemanatee.react_native_material_datetime_picker.util.MDPConstants.KEY_MIN_DATE
import com.thespacemanatee.react_native_material_datetime_picker.util.MDPConstants.KEY_NEG_BUTTON_TEXT
import com.thespacemanatee.react_native_material_datetime_picker.util.MDPConstants.KEY_POS_BUTTON_TEXT
import com.thespacemanatee.react_native_material_datetime_picker.util.MDPConstants.KEY_START_DATE
import com.thespacemanatee.react_native_material_datetime_picker.util.MDPConstants.KEY_TITLE_TEXT
import com.thespacemanatee.react_native_material_datetime_picker.util.MDPConstants.KEY_TYPE
import com.thespacemanatee.react_native_material_datetime_picker.util.MDPConstants.KEY_VALUE
import java.util.Calendar

fun ReadableMap.createDialogArguments() = MDPArguments().apply {
  if (hasKey(KEY_VALUE) && !isNull(KEY_VALUE)) {
    value = getDouble(KEY_VALUE).toLong()
  }
  if (hasKey(KEY_TITLE_TEXT) && !isNull(KEY_TITLE_TEXT)) {
    title = getString(KEY_TITLE_TEXT)
  }
  if (hasKey(KEY_MIN_DATE) && !isNull(KEY_MIN_DATE)) {
    minDate = getDouble(KEY_MIN_DATE).toLong()
  }
  if (hasKey(KEY_MAX_DATE) && !isNull(KEY_MAX_DATE)) {
    maxDate = getDouble(KEY_MAX_DATE).toLong()
  }
  if (hasKey(KEY_START_DATE) && !isNull(KEY_START_DATE)) {
    startDate = getDouble(KEY_START_DATE).toLong()
  }
  if (hasKey(KEY_END_DATE) && !isNull(KEY_END_DATE)) {
    endDate = getDouble(KEY_END_DATE).toLong()
  }
  if (hasKey(KEY_IS_24_HOUR) && !isNull(KEY_IS_24_HOUR)) {
    is24Hour = getBoolean(KEY_IS_24_HOUR)
  }
  if (hasKey(KEY_POS_BUTTON_TEXT) && !isNull(KEY_POS_BUTTON_TEXT)) {
    positiveButtonText = getString(KEY_POS_BUTTON_TEXT)
  }
  if (hasKey(KEY_NEG_BUTTON_TEXT) && !isNull(KEY_NEG_BUTTON_TEXT)) {
    negativeButtonText = getString(KEY_NEG_BUTTON_TEXT)
  }
  if (hasKey(KEY_INPUT_MODE) && !isNull(KEY_INPUT_MODE)) {
    inputMode = getString(KEY_INPUT_MODE)
  }
  if (hasKey(KEY_FULLSCREEN) && !isNull(KEY_FULLSCREEN)) {
    fullscreen = getBoolean(KEY_FULLSCREEN)
  }
  if (hasKey(KEY_TYPE) && !isNull(KEY_TYPE)) {
    type = getString(KEY_TYPE)
  }
  if (hasKey(KEY_FIRST_DAY_OF_WEEK) && !isNull(KEY_FIRST_DAY_OF_WEEK)) {
    firstDayOfWeek = getString(KEY_FIRST_DAY_OF_WEEK)
  }
  if (hasKey(KEY_ALLOWED_DATES) && !isNull(KEY_ALLOWED_DATES)) {
    val allowedDatesArray = getArray(KEY_ALLOWED_DATES)
    if(allowedDatesArray != null){
      val listMap = hashMapOf<String, MutableList<Long>>()

      for (i in 0 until allowedDatesArray.size()) {

        when (allowedDatesArray.getType(i)) {
          ReadableType.Number -> {
            val number = allowedDatesArray.getDouble(i).toLong()
            Log.d("allowedDatesArray", "$number")
            val identifier = calendarToYearMonth(getCalendarFromLong(number))
            if(!listMap.containsKey(identifier)) {
              listMap[identifier] = mutableListOf()
            }
            listMap[identifier]?.add(number)
          }
          else -> {
            // Handle cases where the element is not a Number
            throw IllegalArgumentException("ReadableArray contains non-numeric element at index $i")
          }
        }
      }
      allowedDates = listMap
    }
  }
}

@SuppressLint("RestrictedApi")
fun MDPArguments.createCalendarConstraints(): CalendarConstraints {
  val date = MDPDate(value).fixDate()
  val listValidators = mutableListOf<CalendarConstraints.DateValidator>()
  val builder = CalendarConstraints.Builder().apply {
    setOpenAt(date.timeInMillis)
    minDate?.let {
      date.timeInMillis = it
      date.fixDate(true)
      setStart(date.timeInMillis)
      listValidators.add(DateValidatorPointForward.from(date.timeInMillis))
    }
    maxDate?.let {
      date.timeInMillis = it
      date.fixDate()
      setEnd(date.timeInMillis)
      listValidators.add(DateValidatorPointBackward.before(date.timeInMillis))
    }
    allowedDates?.let {
      listValidators.add(AllowedDatesValidator(it))
    }

    var weekDay = Calendar.SUNDAY

    firstDayOfWeek?.let {
      weekDay = when(it){
        "monday" -> Calendar.MONDAY
        "tuesday"-> Calendar.TUESDAY
        "wednesday" -> Calendar.WEDNESDAY
        "thursday" -> Calendar.THURSDAY
        "friday" -> Calendar.FRIDAY
        "saturday" -> Calendar.SATURDAY
        "sunday" -> Calendar.SUNDAY
        else -> Calendar.SUNDAY
      }
    }

    //This only works on version 1.7.0 or greater of 'com.google.android.material'
    setFirstDayOfWeek(weekDay)

    setValidator(CompositeDateValidator.allOf(listValidators))
  }
  return builder.build()
}

fun MDPDate.fixDate(start: Boolean = false) = apply {
  if (start) {
    hour = 0
    minute = 0
    second = 0
    millisecond = 0
  } else {
    hour = 23
    minute = 59
    second = 59
    millisecond = 999
  }
}

fun dismissDialog(activity: FragmentActivity?, tag: String, promise: Promise) {
  if (activity == null) {
    promise.reject(
      MDPConstants.ERROR_NO_ACTIVITY,
      "Tried to dismiss dialog while not attached to an Activity"
    )
    return
  }
  (activity.supportFragmentManager.findFragmentByTag(tag) as DialogFragment?)?.let {
    it.dismiss()
    promise.resolve(true)
  } ?: run { promise.resolve(false) }
}
