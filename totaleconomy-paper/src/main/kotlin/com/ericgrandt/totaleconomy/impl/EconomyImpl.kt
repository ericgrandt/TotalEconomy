package com.ericgrandt.totaleconomy.impl

import com.ericgrandt.totaleconomy.econ.CommonEconomy
import com.ericgrandt.totaleconomy.result.Err
import com.ericgrandt.totaleconomy.result.Ok
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer

class EconomyImpl : Economy {
    val econ: CommonEconomy

    constructor(econ: CommonEconomy) {
        this.econ = econ
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getName(): String {
        return "Total Economy"
    }

    override fun hasBankSupport(): Boolean {
        return false
    }

    override fun fractionalDigits(): Int {
        return 2
    }

    override fun format(amount: Double): String {
        var currencyName = currencyNamePlural()
        if (amount == 1.00) {
            currencyName = currencyNameSingular()
        }
        return "%.${fractionalDigits()}f $currencyName".format(amount)
    }

    override fun currencyNamePlural(): String {
        return "Diamonds"
    }

    override fun currencyNameSingular(): String {
        return "Diamond"
    }

    override fun hasAccount(player: OfflinePlayer): Boolean {
        return when (val result = econ.hasAccount(player.uniqueId)) {
            is Ok -> {
                result.value
            }
            is Err -> {
                false
            }
        }
    }

    override fun hasAccount(p0: OfflinePlayer, p1: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBalance(p0: OfflinePlayer): Double {
        TODO("Not yet implemented")
    }

    override fun getBalance(p0: OfflinePlayer, p1: String): Double {
        TODO("Not yet implemented")
    }

    override fun has(p0: OfflinePlayer, p1: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun has(p0: OfflinePlayer, p1: String, p2: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun withdrawPlayer(
        p0: OfflinePlayer,
        p1: Double
    ): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun withdrawPlayer(
        p0: OfflinePlayer,
        p1: String,
        p2: Double
    ): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun depositPlayer(
        p0: OfflinePlayer,
        p1: Double
    ): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun depositPlayer(
        p0: OfflinePlayer,
        p1: String,
        p2: Double
    ): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun createBank(
        p0: String,
        p1: OfflinePlayer
    ): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun deleteBank(p0: String): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun bankBalance(p0: String): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun bankHas(p0: String, p1: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun bankWithdraw(p0: String, p1: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun bankDeposit(p0: String, p1: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun isBankOwner(
        p0: String,
        p1: OfflinePlayer
    ): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun isBankMember(
        p0: String,
        p1: OfflinePlayer
    ): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun getBanks(): List<String> {
        TODO("Not yet implemented")
    }

    override fun createPlayerAccount(p0: OfflinePlayer): Boolean {
        TODO("Not yet implemented")
    }

    override fun createPlayerAccount(p0: OfflinePlayer, p1: String): Boolean {
        TODO("Not yet implemented")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("hasAccount(OfflinePlayer)"))
    override fun hasAccount(p0: String, p1: String): Boolean {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("hasAccount(OfflinePlayer)"))
    override fun hasAccount(p0: String): Boolean {
        TODO("Not yet implemented")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("getBalance(OfflinePlayer)"))
    override fun getBalance(p0: String): Double {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("getBalance(OfflinePlayer, String)"))
    override fun getBalance(p0: String, p1: String): Double {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("has(OfflinePlayer, Double)"))
    override fun has(p0: String, p1: Double): Boolean {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("has(OfflinePlayer, String, Double)"))
    override fun has(p0: String, p1: String, p2: Double): Boolean {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("withdrawPlayer(OfflinePlayer, Double)"))
    override fun withdrawPlayer(p0: String, p1: Double): EconomyResponse {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("withdrawPlayer(OfflinePlayer, String, Double)"))
    override fun withdrawPlayer(
        p0: String,
        p1: String,
        p2: Double
    ): EconomyResponse {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("depositPlayer(OfflinePlayer, Double)"))
    override fun depositPlayer(p0: String, p1: Double): EconomyResponse {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("depositPlayer(OfflinePlayer, String, Double)"))
    override fun depositPlayer(
        p0: String,
        p1: String,
        p2: Double
    ): EconomyResponse {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("createBank(String, OfflinePlayer)"))
    override fun createBank(p0: String, p1: String): EconomyResponse {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("isBankOwner(String, OfflinePlayer)"))
    override fun isBankOwner(p0: String, p1: String): EconomyResponse {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("isBankMember(String, OfflinePlayer)"))
    override fun isBankMember(p0: String, p1: String): EconomyResponse {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("createPlayerAccount(OfflinePlayer)"))
    override fun createPlayerAccount(p0: String): Boolean {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("createPlayerAccount(OfflinePlayer, String)"))
    override fun createPlayerAccount(p0: String, p1: String): Boolean {
        TODO("Not implemented due to deprecation")
    }
}