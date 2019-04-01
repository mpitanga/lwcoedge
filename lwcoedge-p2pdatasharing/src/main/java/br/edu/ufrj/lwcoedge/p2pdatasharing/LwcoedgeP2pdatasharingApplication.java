package br.edu.ufrj.lwcoedge.p2pdatasharing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class LwcoedgeP2pdatasharingApplication {

	@Bean("ProcessExecutor-dataSharing")
	public TaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setAwaitTerminationSeconds(300);
		executor.setThreadNamePrefix("Async-dataSharing-");
		executor.setWaitForTasksToCompleteOnShutdown(true);
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication.run(LwcoedgeP2pdatasharingApplication.class, args);
	}
}
