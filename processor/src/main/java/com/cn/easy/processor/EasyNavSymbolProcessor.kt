package com.cn.easy.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier

class EasyNavSymbolProcessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val composableRoutes = resolver.getComposableDestinations() //找到所有声明Route的compose方法列表
        if (!composableRoutes.iterator().hasNext()) return emptyList()

        //写进去
        environment.logger.warn("Route注解个数:${composableRoutes.count()}")

        return emptyList()
    }

    private fun Resolver.getComposableDestinations(name: String = ROUTE_ANNOTATION_PKG): Sequence<KSFunctionDeclaration> {
        val symbolsWithAnnotation = getSymbolsWithAnnotation(name)
        return symbolsWithAnnotation.filterIsInstance<KSFunctionDeclaration>() + symbolsWithAnnotation.getAnnotationDestinations(
            this
        )
    }

    private fun Sequence<KSAnnotated>.getAnnotationDestinations(resolver: Resolver): Sequence<KSFunctionDeclaration> {
        return filterIsInstance<KSClassDeclaration>().filter { Modifier.ANNOTATION in it.modifiers && it.qualifiedName != null }
            .flatMap {
                resolver.getComposableDestinations(it.qualifiedName!!.asString())
            }
    }
}