package br.edu.ufrj.lwcoedge.vn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LwcoedgeVnSensingApplication /*implements ApplicationRunner*/ {

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
