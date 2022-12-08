package com.jamshedalamqaderi.tawraktorapi.visitors.websocket

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnConnected
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnDisconnected
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnError
import com.jamshedalamqaderi.tawraktorapi.api.annotations.OnMessage
import com.jamshedalamqaderi.tawraktorapi.api.annotations.Websocket
import com.jamshedalamqaderi.tawraktorapi.interfaces.BackendCodeAppender
import com.jamshedalamqaderi.tawraktorapi.interfaces.TawraVisitor
import com.jamshedalamqaderi.tawraktorapi.utils.Utils
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSClassDeclarationExtensions.findAllFunctionsAnnotatedWith
import com.jamshedalamqaderi.tawraktorapi.utils.ext.KSFunctionExtensions.findAnnotatedFunction
import com.jamshedalamqaderi.tawraktorapi.utils.ext.toCamelCase
import kotlin.reflect.KClass

class BackendWebSocketVisitorImpl(
    private val logger: KSPLogger
) : TawraVisitor<BackendCodeAppender, KSClassDeclaration, Unit> {

    @OptIn(KspExperimental::class)
    override fun visit(appender: BackendCodeAppender, declaration: KSClassDeclaration) {
        val websocket = declaration.getAnnotationsByType(Websocket::class).first()

        val classObjectRef = declaration.simpleName.asString().toCamelCase()
        appender.addStatement("\nval $classObjectRef = ${declaration.qualifiedName?.asString()}()")

        appender.addWebSocket(websocket.path) {
            val annotatedFunctions = declaration.findAllFunctionsAnnotatedWith(
                Utils.listOfWebsocketEventAnnotations
            )
            if (annotatedFunctions.isEmpty()) return@addWebSocket
            val eventVisitor: TawraVisitor<BackendCodeAppender, KSFunctionDeclaration, Unit> =
                BackendWebSocketEventVisitorImpl(logger, classObjectRef)

            fun <T : Annotation> visitWebsocketEventWrapper(annotation: KClass<T>) =
                visitWebsocketEvent(
                    annotation,
                    annotatedFunctions,
                    eventVisitor,
                    appender
                )

            // visit on connected method
            visitWebsocketEventWrapper(OnConnected::class)

            appender.addStatement("try{")
            // visit on message method
            visitWebsocketEventWrapper(OnMessage::class)

            appender.addStatement("} catch(e:Exception){")
            // visit on error method
            visitWebsocketEventWrapper(OnError::class)

            appender.addStatement("} finally {")
            // visit on disconnected method
            visitWebsocketEventWrapper(OnDisconnected::class)

            appender.addStatement("}")
        }
    }

    private fun <T : Annotation> visitWebsocketEvent(
        annotation: KClass<T>,
        functions: List<KSFunctionDeclaration>,
        eventVisitor: TawraVisitor<BackendCodeAppender, KSFunctionDeclaration, Unit>,
        appender: BackendCodeAppender,
    ) {
        val onDisconnectedEventFunction =
            functions.findAnnotatedFunction(annotation)
        if (onDisconnectedEventFunction != null) {
            eventVisitor.visit(appender, onDisconnectedEventFunction)
        }
    }
}