# Kotlinx Serialization
-keepattributes *Annotation*, EnclosingMethod, InnerClasses
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName *;
}
-keep class kotlinx.serialization.json.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-keep interface * extends androidx.room.Dao
-keep class * implements androidx.room.Dao
-keep class androidx.room.Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Keep our models and entities to prevent Room/Serialization issues
-keep class com.pizzza.pizzzastore.model.** { *; }
-keep class com.pizzza.pizzzastore.repository.network.model.** { *; }
-keep class com.pizzza.pizzzastore.repository.db.entity.** { *; }

# Koin
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keepclassmembernames class kotlinx.coroutines.android.HandlerContext {
    val handler;
}
-keep class kotlinx.coroutines.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
