package com.pizzza.pizzzastore.repository.network

import com.pizzza.pizzzastore.model.OrderModel
import com.pizzza.pizzzastore.model.ParentOrderModel
import com.pizzza.pizzzastore.model.ProductModel
import com.pizzza.pizzzastore.repository.network.exception.ErrorNetwork
import com.pizzza.pizzzastore.repository.network.model.loadOrder
import com.pizzza.pizzzastore.repository.network.model.loadParentOrder
import com.pizzza.pizzzastore.repository.network.model.toModelList
import com.pizzza.pizzzastore.repository.network.model.toResponse
import com.pizzza.pizzzastore.model.BranchModel
import com.pizzza.pizzzastore.usecases.network.IDataNetwork
import com.pizzza.pizzzastore.repository.utils.ConnectivityManager
import com.pizzza.pizzzastore.repository.db.manager.AppDataBase
import com.pizzza.pizzzastore.repository.db.toEntity
import com.pizzza.pizzzastore.repository.db.toEntityListFromResponse
import com.pizzza.pizzzastore.repository.db.toProductEntityList
import com.pizzza.pizzzastore.repository.db.toProductModelList
import com.pizzza.pizzzastore.repository.network.model.UserResponse
import com.pizzza.pizzzastore.repository.db.toModelList as toModelListFromDb

class DataNetwork(
    private val apiService: KmmService,
    private val connectivityManager: ConnectivityManager,
    private val database: AppDataBase
) : IDataNetwork {

    override suspend fun loadOrder(): List<OrderModel> = apiCall({
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        apiService.getOrder()
    }) {
        it.loadOrder()
    }

    override suspend fun getProducts(): List<ProductModel> {
        val dao = database.productDao()
        val localProducts = dao.getAll()
        println("DataNetwork: Obteniendo ${localProducts.size} productos desde DB Local")
        return localProducts.toProductModelList()
    }

    override suspend fun syncProducts(): List<ProductModel> = apiCall({
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        println("DataNetwork: Sincronizando productos desde el servicio...")
        val response = apiService.getProducts()
        val models = response.toModelList()
        
        val dao = database.productDao()
        dao.deleteAll()
        dao.insertAll(models.toProductEntityList())
        println("DataNetwork: Sincronización completa. ${models.size} productos guardados.")
        models
    }) { it }

    override suspend fun getBranches(): List<BranchModel> = apiCall({
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        println("DataNetwork: Cargando sucursales desde el servicio...")
        apiService.getBranches()
    }) { response ->
        response.toModelList()
    }

    override suspend fun updateBranch(data: BranchModel): String = apiCall {
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        println("DataNetwork: Actualizando sucursal ${data.uid}...")
        apiService.updateBranch(data.toResponse())
    }

    override suspend fun updateProduct(data: ProductModel): String = apiCall {
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        println("DataNetwork: Actualizando producto ${data.uid}...")
        apiService.updateProduct(data.toResponse())
    }

    override suspend fun updateOrder(data: ParentOrderModel): String = apiCall {
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        println("DataNetwork: Actualizando pedido ${data.uid} a estado ${data.state}...")
        val response = apiService.updateParentOrder(data.toParentOrderRequest())
        
        // En lugar de borrar todo, actualizamos o insertamos el pedido específico en la DB local
        // Nota: Como la entidad no guarda los productos individuales, la próxima carga
        // desde DB detectará que faltan productos y forzará un refresco completo de red.
        // Esto es correcto para mantener la integridad de los datos.
        database.parentOrderDao().insertAll(listOf(data.toEntity()))
        println("DataNetwork: Pedido actualizado en servidor y DB local")
        response
    }

    override suspend fun loadParentOrder(forceRefresh: Boolean): List<ParentOrderModel> {
        val dao = database.parentOrderDao()
        val localOrders = dao.getAll()
        
        println("DataNetwork: Iniciando loadParentOrder. forceRefresh=$forceRefresh, localCount=${localOrders.size}")

        if (localOrders.isNotEmpty() && !forceRefresh) {
            val models = localOrders.toModelListFromDb()
            // Si por alguna razón no tienen productos asociados, forzamos refresco para obtener data completa
            if (models.any { it.orders.isEmpty() }) {
                println("DataNetwork: Datos locales incompletos (sin productos). Forzando refresco de red.")
            } else {
                println("DataNetwork: Cargando desde DB Local con datos completos")
                return models
            }
        }

        if (!connectivityManager.isConnected()) {
            if (localOrders.isNotEmpty()) {
                println("DataNetwork: Sin conexión, usando DB Local (aunque falten productos)")
                return localOrders.toModelListFromDb()
            } else {
                println("DataNetwork: Sin conexión y sin datos locales. Lanzando error.")
                throw ErrorNetwork()
            }
        }

        return apiCall({
            println("DataNetwork: Llamando al servicio getParentOrder...")
            val response = apiService.getParentOrder()
            println("DataNetwork: Servicio respondió con ${response.size} pedidos")
            response
        }) { response ->
            // Guardar en DB para la próxima vez
            dao.deleteAll()
            dao.insertAll(response.toEntityListFromResponse())
            println("DataNetwork: Datos guardados en DB local")
            response.loadParentOrder()
        }
    }

    override suspend fun registerUser(data: UserResponse): String = apiCall {
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        println("DataNetwork: Registrando usuario ${data.email}...")
        apiService.registerUser(data)
    }

    override suspend fun login(data: com.pizzza.pizzzastore.repository.network.model.LoginRequest): com.pizzza.pizzzastore.repository.network.model.LoginResponse = apiCall {
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        apiService.login(data)
    }

    override suspend fun saveUserLocal(user: com.pizzza.pizzzastore.repository.db.entity.UserEntity) {
        database.userDao().logout()
        database.userDao().insertUser(user)
    }

    override suspend fun getUserLocal(): com.pizzza.pizzzastore.repository.db.entity.UserEntity? {
        return database.userDao().getUser()
    }

    override suspend fun logout() {
        database.userDao().logout()
    }
}
