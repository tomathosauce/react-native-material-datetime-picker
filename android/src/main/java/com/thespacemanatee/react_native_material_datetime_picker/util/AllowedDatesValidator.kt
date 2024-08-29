package com.thespacemanatee.react_native_material_datetime_picker.util

import android.util.Log
import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import java.util.Calendar
import java.util.TimeZone

class AllowedDatesValidator: CalendarConstraints.DateValidator {
    private var days: Array<Long> = arrayOf<Long>()
    private var listMap = hashMapOf<String, MutableList<Long>>()
    private var list: List<Long> = listOf<Long>()

    constructor( days: Array<Long>){
        this@AllowedDatesValidator.days = days
    }

    constructor( list: List<Long>){
        this@AllowedDatesValidator.list = list
    }

    constructor( listMap: HashMap<String, MutableList<Long>>){
        this@AllowedDatesValidator.listMap = listMap
    }

    constructor(parcel: Parcel) : this(
        parcel.createLongArray()!!.toTypedArray()
    )

    override fun isValid(date: Long): Boolean {
        Log.d("AllowedDatesValidator.isValid", "$date")
        if(listMap.size > 0){
            val daycal  = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                timeInMillis = date
            }

            val L = listMap[calendarToYearMonth(daycal)]
            if(L != null){
                for(day in L){
                    Log.d("AllowedDatesValidator.isValid", "${format(day)},${format(date)} | ${day},$date")
                    if(day == date){
                        return true
                    }
                }
            }

            return false
        }

        if(list.isNotEmpty()){
            for(day in list){
                if(day == date){
                    return true
                }
            }
            return false
        }

        for (day in days){
            if(format(day) == format(date))
                if(date == day) {
                    return true
                }
        }

        return false
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeArray(days)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AllowedDatesValidator> {
        override fun createFromParcel(parcel: Parcel): AllowedDatesValidator {
            return AllowedDatesValidator(parcel)
        }

        override fun newArray(size: Int): Array<AllowedDatesValidator?> {
            return arrayOfNulls(size)
        }
    }
}

