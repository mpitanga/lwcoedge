package br.edu.ufrj.lwcoedge.experiment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import br.edu.ufrj.lwcoedge.experiment.request.Experiment0;

@SpringBootApplication
@EnableAsync
public class LwcoedgeExperiment0Application implements ApplicationRunner {

	@Autowired
	Experiment0 service;
	
	@Bean("threadPoolTaskExecutor_Experiment")
	public TaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(300);
		executor.setThreadNamePrefix("Async-Experiment-");
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication.run(LwcoedgeExperiment0Application.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		service.run(args);
	}

}
