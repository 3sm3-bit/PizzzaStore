package com.pizzza.pizzzastore.repository.network

import com.pizzza.pizzzastore.repository.network.model.OrderResponse
import com.pizzza.pizzzastore.repository.network.model.ParentOrderResponse
import com.pizzza.pizzzastore.repository.network.model.ProductResponse
import com.pizzza.pizzzastore.repository.network.model.BranchResponse
import com.pizzza.pizzzastore.repository.network.model.UserResponse
import com.pizzza.pizzzastore.repository.network.model.LoginRequest
import com.pizzza.pizzzastore.repository.network.model.LoginResponse
import com.pizzza.pizzzastore.shared.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import com.pizzza.pizzzastore.repository.network.model.UploadImageResponse

class KmmService(private val client: HttpClient) {

    companion object {
        val BASE_URL = if (BuildConfig.IS_DEBUG) {
            BuildConfig.BASE_URL_SERVICE_DEV
        } else {
            BuildConfig.BASE_URL_SERVICE
        }
    }

    suspend fun uploadProductImage(image: ByteArray): UploadImageResponse {
        return client.submitFormWithBinaryData(
            url = "${BASE_URL}/upload-image",
            formData = formData {
                append("image", image, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"product_image.jpg\"")
                })
            }
        ).body()
    }

    suspend fun getOrder(): List<OrderResponse> {
        return client.get("${BASE_URL}/pizzzeria/pizza/order").body()
    }

    suspend fun getParentOrder(): List<ParentOrderResponse> {
        return client.get("${BASE_URL}/pizzzeria/order/generalOrder").body()
    }

    suspend fun getProducts(): List<ProductResponse> {
        return client.get("${BASE_URL}/pizzzeria/products").body()
    }

    suspend fun updateProduct(request: ProductResponse): String {
        return client.put("${BASE_URL}/pizzzeria/products/${request.uid}") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getBranches(): List<BranchResponse> {
        return client.get("${BASE_URL}/pizzzeria/branch").body()
    }

    suspend fun updateBranch(request: BranchResponse): String {
        return client.put("${BASE_URL}/pizzzeria/branch/${request.uid}") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateParentOrder(request: ParentOrderResponse): String {
        return client.put("${BASE_URL}/pizzzeria/order/generalOrder/${request.uid}") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun registerUser(request: UserResponse): String {
        println("KmmService: Enviando registro para ${request.email}...")
        val response = client.post("${BASE_URL}/services/user") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val result = response.bodyAsText()
        println("KmmService: Respuesta recibida (Status: ${response.status}): $result")
        return result
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post("${BASE_URL}/services/user/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
