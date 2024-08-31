package com.thespacemanatee.react_native_material_datetime_picker.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

fun format(date: Long): String{
    val calendar = Calendar.getInstance().apply {
        timeInMillis = date
    }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val formattedDate = dateFormat.format(calendar.time)

    return formattedDate
}

fun getCalendar(year: Int, month: Int, day: Int): Calendar {
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1) // Note: Months are 0-based, so August is 7
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, 0) // Optional: Set the hour
        set(Calendar.MINUTE, 0) // Optional: Set the minute
        set(Calendar.SECOND, 0) // Optional: Set the second
        set(Calendar.MILLISECOND, 0)
    }
}

fun getCalendarFromLong(date: Long): Calendar {
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = date
    }
}

fun calendarToYearMonth(day: Calendar): String {
    val year = day.get(Calendar.YEAR)
    val month = day.get(Calendar.MONTH) // Note: MONTH is zero-based
    return "$year-$month"
}

class Node(var value: Long) {
    var left: Node? = null
    var right: Node? = null
}
class BinaryTree {
    var root: Node? = null

    private fun addRecursive(current: Node?, value: Long): Node? {
        if (current == null) {
            return Node(value)
        }
        if (value < current.value) {
            current.left = addRecursive(current.left, value)
        } else if (value > current.value) {
            current.right = addRecursive(current.right, value)
        } else {
            return current
        }
        return current
    }

    fun add(value: Long) {
        root = addRecursive(root, value)
    }

    private fun findRecursive(current: Node?, value: Long): Node? {
        if (current == null || current.value == value) {
            return current
        }
        return if (value < current.value) {
            findRecursive(current.left, value)
        } else {
            findRecursive(current.right, value)
        }
    }

    fun find(value: Long): Node? {
        return findRecursive(root, value)
    }

    fun buildTree(sortedList: List<Long>) {
        root = buildTreeRecursive(sortedList, 0, sortedList.size - 1)
    }

    private fun buildTreeRecursive(sortedList: List<Long>, start: Int, end: Int): Node? {
        if (start > end) {
            return null
        }

        // Find the middle index
        val mid = (start + end) / 2
        // Create a new node with the middle value
        val node = Node(sortedList[mid])
        // Recursively build the left and right subtrees
        node.left = buildTreeRecursive(sortedList, start, mid - 1)
        node.right = buildTreeRecursive(sortedList, mid + 1, end)

        return node
    }
}
