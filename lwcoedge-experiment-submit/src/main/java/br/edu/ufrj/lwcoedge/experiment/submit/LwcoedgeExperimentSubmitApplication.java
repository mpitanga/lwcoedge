package br.edu.ufrj.lwcoedge.experiment.submit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class LwcoedgeExperimentSubmitApplication {

	@Bean("threadPoolTaskExecutor")
	public TaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setAwaitTerminationSeconds(300);
		executor.setThreadNamePrefix("Async-");
		executor.setWaitForTasksToCompleteOnShutdown(true);
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication.run(LwcoedgeExperimentSubmitApplication.class, args);
	}
}
