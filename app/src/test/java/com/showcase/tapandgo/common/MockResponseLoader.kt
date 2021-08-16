package com.showcase.tapandgo.common

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MockResponseLoader<T>(val content: String, val model: T) {
    companion object {
        const val JSON = ".json"

        inline fun <reified T> create(fileNameWithoutExtension: String): MockResponseLoader<T> {
            val adapter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(T::class.java)
            val content = ClassLoader.getSystemResource(fileNameWithoutExtension + JSON).readText()
            val model = adapter.fromJson(content)
            return model?.let { modelUnwrapped ->
                MockResponseLoader(
                    content,
                    modelUnwrapped
                )
            } ?: MockResponseLoader(
                content,
                T::class.java.newInstance()
            )
        }
    }
}
