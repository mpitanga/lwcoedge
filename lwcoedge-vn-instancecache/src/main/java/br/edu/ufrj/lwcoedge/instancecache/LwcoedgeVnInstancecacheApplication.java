package br.edu.ufrj.lwcoedge.instancecache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class LwcoedgeVnInstancecacheApplication {

	@Bean("ProcessExecutor-VnInstanceCache")
	public TaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(300);
		executor.setThreadNamePrefix("Async-VnInstanceCache-");
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication.run(LwcoedgeVnInstancecacheApplication.class, args);
	}
}
