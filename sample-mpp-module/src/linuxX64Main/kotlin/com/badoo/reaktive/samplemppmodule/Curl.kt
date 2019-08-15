package com.badoo.reaktive.samplemppmodule

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.get
import kotlinx.cinterop.staticCFunction
import libcurl.CURLE_OK
import libcurl.CURLOPT_URL
import libcurl.CURLOPT_WRITEDATA
import libcurl.CURLOPT_WRITEFUNCTION
import libcurl.curl_easy_cleanup
import libcurl.curl_easy_init
import libcurl.curl_easy_perform
import libcurl.curl_easy_setopt
import platform.posix.size_t

fun curl(url: String): ByteArray? {
    val curl = curl_easy_init()
    try {
        curl_easy_setopt(curl, CURLOPT_URL, url)
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, staticCFunction(::writeCallback))

        val data = arrayListOf<Byte>()

        val dataRef = StableRef.create(data)
        try {
            curl_easy_setopt(curl, CURLOPT_WRITEDATA, dataRef.asCPointer())

            if (curl_easy_perform(curl) != CURLE_OK) {
                return null
            }
        } finally {
            dataRef.dispose()
        }

        return ByteArray(data.size, data::get)
    } finally {
        curl_easy_cleanup(curl)
    }
}

private fun writeCallback(buffer: CPointer<ByteVar>?, itemSize: size_t, itemCount: size_t, userData: COpaquePointer?): size_t {
    if (buffer == null) {
        return 0U
    }

    val data = userData?.asStableRef<MutableList<Byte>>()?.get()
    if (data != null) {
        for (i in 0 until itemCount.toInt()) {
            data += buffer[i]
        }
    }

    return itemSize * itemCount
}