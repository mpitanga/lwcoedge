package br.edu.ufrj.lwcoedge.p2pcollaboration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class LwcoedgeP2pcollaborationApplication {

	@Bean("ProcessExecutor-P2P")
	public TaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setAwaitTerminationSeconds(300);
		executor.setThreadNamePrefix("Async-P2P-");
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication.run(LwcoedgeP2pcollaborationApplication.class, args);
	}
}
