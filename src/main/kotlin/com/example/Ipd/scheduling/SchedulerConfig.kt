package com.example.Ipd.scheduling

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.util.concurrent.Executor

@Configuration
@EnableAsync
@EnableScheduling
class SchedulerConfig {

    @Bean
    fun taskScheduler(): ThreadPoolTaskScheduler {
        return ThreadPoolTaskScheduler().apply {
            poolSize = 10
            setThreadNamePrefix("scheduler-")
            setAwaitTerminationSeconds(60)
            setWaitForTasksToCompleteOnShutdown(true)
            initialize()
        }
    }

    @Bean("asyncExecutor")
    fun asyncExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 5
            maxPoolSize = 20
            queueCapacity = 100
            setThreadNamePrefix("async-")
            setAwaitTerminationSeconds(60)
            setWaitForTasksToCompleteOnShutdown(true)
            initialize()
        }
    }
}
