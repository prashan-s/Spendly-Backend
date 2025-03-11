package com.spendly.backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class AsyncConfig : AsyncConfigurer {

    @Value("\${taskpool.corePoolSize}")
    var corePoolSize: Int = 0

    @Value("\${taskpool.maxPoolSize}")
    var maxPoolSize: Int = 0

    @Value("\${taskpool.queueCapacity}")
    var queueCapacity: Int = 0

    override fun getAsyncExecutor(): Executor {
        return taskExecutor()
    }

    @Bean(name = ["asyncPool"])
    fun taskExecutor(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor().apply {
            corePoolSize = this@AsyncConfig.corePoolSize
            maxPoolSize = this@AsyncConfig.maxPoolSize
            setQueueCapacity(this@AsyncConfig.queueCapacity)
            initialize()
        }
        return executor
    }
}
