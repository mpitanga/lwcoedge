package br.edu.ufrj.lwcoedge.vn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class LwcoedgeVnSensingApplication {

	@Bean("ProcessExecutor-VnSensing")
	public TaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(300);
		executor.setThreadNamePrefix("Async-VnSensing-");
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication.run(LwcoedgeVnSensingApplication.class, args);
	}
	
/*	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		System.out.println("Start [LwcoedgeVnSensingApplication]");
		for (String name : arg0.getOptionNames()){
            System.out.println(name + "=" + arg0.getOptionValues(name));
        }
	}
*/
}
