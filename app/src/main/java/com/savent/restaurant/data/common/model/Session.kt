package com.savent.restaurant.data.common.model

import com.google.gson.annotations.SerializedName

data class Session(
    @SerializedName("employee_id")
    val employeeId: Int,
    @SerializedName("employee_name")
    val employeeName: String,
    @SerializedName("branch_id")
    val branchId: Int,
    @SerializedName("company_id")
    val companyId: Int,
    @SerializedName("restaurant_id")
    val restaurantId: Int
) {
    override fun toString(): String {
        return super.toString()
    }
}