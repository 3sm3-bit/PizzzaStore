package com.pizzza.pizzzastore.model

data class BranchModel(
    val nameBranch: String,
    val identifier: String,
    val description: String,
    val address: String,
    val phone: String,
    val latitude: String,
    val longitude: String,
    val uid: String
)
