package br.edu.ufrj.lwcoedge.resourceallocator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class LwcoedgeResourceallocatorApplication {

	@Bean("ProcessExecutor-allocator")
	public TaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(300);
		executor.setThreadNamePrefix("Async-allocator-");
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication.run(LwcoedgeResourceallocatorApplication.class, args);
	}
}
