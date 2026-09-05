package com.example.Ipd.scheduling

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import java.util.concurrent.Executor

@Configuration
class AsyncExceptionConfig(
    @Qualifier("asyncExecutor") private val asyncExecutor: Executor
): AsyncConfigurer {
    private val logger = LoggerFactory.getLogger(AsyncExceptionConfig::class.java)

    override fun getAsyncExecutor(): Executor = asyncExecutor

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return AsyncUncaughtExceptionHandler { _, _, _ ->
            logger.error("Async task failed")
        }
    }
}
