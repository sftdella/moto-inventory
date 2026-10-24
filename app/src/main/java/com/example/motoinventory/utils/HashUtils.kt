package com.example.motoinventory.utils

import java.security.MessageDigest

object HashUtils {
    fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(plain: String, hash: String): Boolean {
        return sha256(plain) == hash
    }
}
