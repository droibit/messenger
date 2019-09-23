package com.droibit.looking2.core.util.ext

fun String.unescapeHtml(): String {
    val unescapeStr = this@unescapeHtml
    var i = 0
    return buildString(unescapeStr.length) {
        while (i < unescapeStr.length) {
            val charAt = unescapeStr[i]
            when {
                charAt != '&' -> {
                    append(charAt); i++
                }
                unescapeStr.startsWith("&amp;", i) -> {
                    append('&'); i += 5
                }
                unescapeStr.startsWith("&apos;", i) -> {
                    append('\''); i += 6
                }
                unescapeStr.startsWith("&quot;", i) -> {
                    append('"'); i += 6
                }
                unescapeStr.startsWith("&lt;", i) -> {
                    append('<'); i += 4
                }
                unescapeStr.startsWith("&gt;", i) -> {
                    append('>'); i += 4
                }
            }
        }
    } // buildString
}

fun String.unescapeRegex(): String {
    val unescapeStr = this@unescapeRegex
    return buildString(unescapeStr.length) {
        unescapeStr.indices
            .map { unescapeStr[it] }
            .forEach {
                when (it) {
                    '?', '.', '*', '+', '\\' -> append("\\").append(it)
                    else -> append(it)
                }
            }
    }
}