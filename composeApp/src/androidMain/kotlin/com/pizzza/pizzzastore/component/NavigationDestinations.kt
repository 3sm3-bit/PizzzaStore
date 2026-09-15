package com.pizzza.pizzzastore.component

import kotlinx.serialization.Serializable

@Serializable
object Splash


@Serializable
object Login

@Serializable
object Register

@Serializable
object Orders

@Serializable
object MenuOptions

@Serializable
object Products

@Serializable
object EditPizza

@Serializable
object EditOtherProduct

@Serializable
object Branches

@Serializable
object CreateProduct

@Serializable
object ConfigNoti

@Serializable
object EditBranch

@Serializable
object ListUser

@Serializable
data class Address(
    val initialLat: String? = null,
    val initialLng: String? = null,
    val initialAddress: String? = null
)

