package com.example.zensqlite.utils

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return email.isNotBlank() && emailRegex.matches(email.trim())
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isValidName(name: String): Boolean {
        return name.trim().length >= 2
    }

    fun isPasswordMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun isValidProductCode(code: String): Boolean {
        return code.trim().length >= 3
    }

    fun isValidProductName(name: String): Boolean {
        return name.trim().length >= 2
    }

    fun isValidQuantity(quantity: String): Boolean {
        return try {
            val qty = quantity.toInt()
            qty >= 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isValidPrice(price: String): Boolean {
        return try {
            val p = price.replace(".", "").replace(",", "").toDouble()
            p >= 0
        } catch (e: NumberFormatException) {
            false
        }
    }
}
