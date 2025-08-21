package com.example.scanwithonline.data

import com.example.scanwithonline.data.network.OfflineRetrofitInstance
import com.example.scanwithonline.data.network.ProductResponse
import com.example.scanwithonline.data.network.RetrofitInstance

object ProductRepository {

    suspend fun getProductDetails(barcode: String): ProductResponse {
        return when (DataSourceManager.currentSource) {
            DataSourceType.ONLINE -> {
                // If the source is Online, use the live internet API
                RetrofitInstance.api.getProductDetails(barcode)
            }
            DataSourceType.OFFLINE -> {
                // If the source is Offline, use the local API
                OfflineRetrofitInstance.api.getProductDetails(barcode)
            }
        }
    }
}