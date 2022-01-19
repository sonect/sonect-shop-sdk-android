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
    object Italiano : PredefineConfig(
        setOf(
            Config.Environment(SonectSDK.Config.Enviroment.STAGING),
            Config.MerchantId(""),
            Config.DeviceId(""),
            Config.ClientId("50c4f5f0-b229-11ea-bad4-f923de7877da"),
            Config.ClientSecret("e22800bd1495f833fef842382951affb98557e6424748810562fcae0a96ecc76"),
            Config.HmacKey("5da1baaca50bc1cde69eac15a4b29d745ec3ff0567f8fcbe15c512050a6cec6e"),
            Config.Theme(isLight = true),
            Config.Scanner(isScandit = true),
            Config.RandomShop(isRandomShopOnStart = false),
            Config.RandomBeneficiary(isRandomBeneficiary = false),
            Config.AdditionalData(""),
        )
    )
}