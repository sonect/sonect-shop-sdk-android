package ch.sonect.sdk.shop.integrationapp.data

import ch.sonect.sdk.shop.SonectSDK

sealed class PredefineConfig(val config: Set<Config>) {
    object Bnl : PredefineConfig(
        setOf(
            Config.Environment(SonectSDK.Config.Enviroment.STAGING),
            Config.MerchantId("4B2r/Qo1oQHzzg8dCsnHDmW9jL4FmoD4oAvuachAMIU="),
            Config.ClientId("cceff710-79a3-11ea-92ad-652ad420aac6"),
            Config.ClientSecret("447617077c073f8495c196ddcbbd92bd547e90249f172f9432cd18eb2ebe6a71"),
            Config.DeviceId(""),
            Config.HmacKey("a2469d2222d54c5cc51930220882e12eaea3c015206e3abf774a13799371b81d"),
            Config.Theme(isLight = true),
            Config.Scanner(isScandit = true),
            Config.RandomShop(isRandomShopOnStart = false),
            Config.RandomBeneficiary(isRandomBeneficiary = false),
            Config.AdditionalData(""),
        )
    )
}