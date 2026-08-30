package com.pizzza.pizzzastore.repository.network.model

import com.pizzza.pizzzastore.model.BranchModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BranchResponse(
    @SerialName("nameBranch")
    val nameBranch: String? = "",
    @SerialName("identifier")
    val identifier: String? = "",
    @SerialName("description")
    val description: String? = "",
    @SerialName("address")
    val address: String? = "",
    @SerialName("phone")
    val phone: String? = "",
    @SerialName("latitude")
    val latitude: String? = "",
    @SerialName("longitude")
    val longitude: String? = "",
    @SerialName("uid")
    val uid: String? = ""
)

fun List<BranchResponse>.toModelList() = map {
    BranchModel(
        nameBranch = it.nameBranch ?: "",
        identifier = it.identifier ?: "",
        description = it.description ?: "",
        address = it.address ?: "",
        phone = it.phone ?: "",
        latitude = it.latitude ?: "",
        longitude = it.longitude ?: "",
        uid = it.uid ?: ""
    )
}

fun BranchModel.toResponse() = BranchResponse(
    nameBranch = nameBranch,
    identifier = identifier,
    description = description,
    address = address,
    phone = phone,
    latitude = latitude,
    longitude = longitude,
    uid = uid
)
