package com.jamshedalamqaderi.tawraktorapi.utils.ext

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSValueParameter

object AnnotationExtensions {

    inline fun <reified T : Annotation> Sequence<KSAnnotation>.isAnnotatedWith(): Boolean {
        return findAnnotatedWith<T>() != null
    }

    inline fun <reified T : Annotation> List<KSValueParameter>.isAnnotationExists(): Boolean {
        return this.find { it.annotations.isAnnotatedWith<T>() } != null
    }

    inline fun <reified T : Annotation> List<KSValueParameter>.findAnnotatedWith(): KSValueParameter? {
        return this.find { it.annotations.isAnnotatedWith<T>() }
    }

    inline fun <reified T : Annotation> Sequence<KSAnnotation>.findAnnotatedWith(): KSAnnotation? {
        return find {
            it.annotationType.resolve()
                .declaration
                .qualifiedName
                ?.asString() == T::class.qualifiedName
        }
    }

}