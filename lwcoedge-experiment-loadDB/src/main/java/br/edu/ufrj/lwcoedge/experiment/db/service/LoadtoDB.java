package br.edu.ufrj.lwcoedge.experiment.db.service;

import java.io.File;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ufrj.lwcoedge.experiment.core.model.ExperimentLoadToDBConfig;

@Component
public class LoadtoDB implements ApplicationRunner {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	ServiceDB service;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (args == null) {
			System.out.println("LW-CoEdge experiment config parameters not configured!");
			System.exit(-1);
		}

		logger.info("Loading the LW-CoEdge experiment settings...");
		String fileName = args.getOptionValues("experiment-loadtodb-config").get(0);
		ObjectMapper objectMapper = new ObjectMapper();
		ExperimentLoadToDBConfig config = objectMapper.readValue(new File(fileName), ExperimentLoadToDBConfig.class);
		logger.info("LW-CoEdge experiment settings loaded.");

		String experimentName = config.getExperimentname();
		String basePath = config.getBasepath();
		String path = basePath+experimentName+"/";
		String[] EdgeNodes = config.getEdgenodes();

		int times = config.getTimes();

		service.loadDataToDB(
				experimentName,
				path, 
				EdgeNodes, 
				times
		);

	}

}
