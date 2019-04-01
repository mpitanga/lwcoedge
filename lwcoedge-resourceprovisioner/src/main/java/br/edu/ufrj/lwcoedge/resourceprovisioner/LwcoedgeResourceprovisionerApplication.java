package br.edu.ufrj.lwcoedge.resourceprovisioner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class LwcoedgeResourceprovisionerApplication {

	@Bean(name="ProcessExecutor-provisioner")
	public TaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(300);
		executor.setThreadNamePrefix("Async-provisioner-");
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication.run(LwcoedgeResourceprovisionerApplication.class, args);
	}
}
