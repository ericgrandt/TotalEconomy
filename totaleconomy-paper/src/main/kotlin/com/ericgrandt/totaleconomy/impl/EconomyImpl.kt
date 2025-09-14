package com.ericgrandt.totaleconomy.impl

import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer

class EconomyImpl : Economy {
    override fun isEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun hasBankSupport(): Boolean {
        TODO("Not yet implemented")
    }

    override fun fractionalDigits(): Int {
        TODO("Not yet implemented")
    }

    override fun format(p0: Double): String {
        TODO("Not yet implemented")
    }

    override fun currencyNamePlural(): String {
        TODO("Not yet implemented")
    }

    override fun currencyNameSingular(): String {
        TODO("Not yet implemented")
    }

    override fun hasAccount(player: OfflinePlayer): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasAccount(p0: OfflinePlayer?, p1: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBalance(p0: OfflinePlayer?): Double {
        TODO("Not yet implemented")
    }

    override fun getBalance(p0: OfflinePlayer?, p1: String?): Double {
        TODO("Not yet implemented")
    }

    override fun has(p0: OfflinePlayer?, p1: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun has(p0: OfflinePlayer?, p1: String?, p2: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun withdrawPlayer(
        p0: OfflinePlayer?,
        p1: Double
    ): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun withdrawPlayer(
        p0: OfflinePlayer?,
        p1: String?,
        p2: Double
    ): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun depositPlayer(
        p0: OfflinePlayer?,
        p1: Double
    ): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun depositPlayer(
        p0: OfflinePlayer?,
        p1: String?,
        p2: Double
    ): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun createBank(
        p0: String?,
        p1: OfflinePlayer?
    ): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun deleteBank(p0: String?): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun bankBalance(p0: String?): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun bankHas(p0: String?, p1: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun bankWithdraw(p0: String?, p1: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun bankDeposit(p0: String?, p1: Double): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun isBankOwner(
        p0: String?,
        p1: OfflinePlayer?
    ): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun isBankMember(
        p0: String?,
        p1: OfflinePlayer?
    ): EconomyResponse {
        TODO("Not yet implemented")
    }

    override fun getBanks(): List<String> {
        TODO("Not yet implemented")
    }

    override fun createPlayerAccount(p0: OfflinePlayer): Boolean {
        TODO("Not yet implemented")
    }

    override fun createPlayerAccount(p0: OfflinePlayer?, p1: String?): Boolean {
        TODO("Not yet implemented")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("hasAccount(OfflinePlayer)"))
    override fun hasAccount(p0: String?, p1: String?): Boolean {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("hasAccount(OfflinePlayer)"))
    override fun hasAccount(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("getBalance(OfflinePlayer)"))
    override fun getBalance(p0: String?): Double {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("getBalance(OfflinePlayer, String)"))
    override fun getBalance(p0: String?, p1: String?): Double {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("has(OfflinePlayer, Double)"))
    override fun has(p0: String?, p1: Double): Boolean {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("has(OfflinePlayer, String, Double)"))
    override fun has(p0: String?, p1: String?, p2: Double): Boolean {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("withdrawPlayer(OfflinePlayer, Double)"))
    override fun withdrawPlayer(p0: String?, p1: Double): EconomyResponse {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("withdrawPlayer(OfflinePlayer, String, Double)"))
    override fun withdrawPlayer(
        p0: String?,
        p1: String?,
        p2: Double
    ): EconomyResponse {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("depositPlayer(OfflinePlayer, Double)"))
    override fun depositPlayer(p0: String?, p1: Double): EconomyResponse {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("depositPlayer(OfflinePlayer, String, Double)"))
    override fun depositPlayer(
        p0: String?,
        p1: String?,
        p2: Double
    ): EconomyResponse {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("createBank(String, OfflinePlayer)"))
    override fun createBank(p0: String?, p1: String?): EconomyResponse {
        TODO("Not implemented due to deprecation")
    }

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("isBankOwner(String, OfflinePlayer)"))
    override fun isBankOwner(p0: String?, p1: String?): EconomyResponse {
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
    override fun createPlayerAccount(p0: String?, p1: String?): Boolean {
        TODO("Not implemented due to deprecation")
    }
}