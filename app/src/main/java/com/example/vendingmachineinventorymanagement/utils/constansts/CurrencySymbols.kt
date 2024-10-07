package com.smartfusion.qahwapointvendingapp.util.constansts

object CurrencySymbols {
    const val TRY = "₺" // Turkish Lira
    const val USD = "$" // US Dollar
    const val SOS = "S" // Somali Shilling
    const val AED = "د.إ" // United Arab Emirates Dirham (AED)
    const val PKR = "Rs." // United Arab Emirates Dirham (AED)

    fun getCurrencySymbol(currencyCode: String): String {
        return when (currencyCode.toUpperCase()) {
            "TRY" -> TRY
            "USD" -> USD
            "SOS" -> SOS
            "AED" -> AED
            "PKR" -> PKR
            else -> ""
        }
    }
}
