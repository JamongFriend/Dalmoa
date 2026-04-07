package Project.Dalmoa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class DalmoaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DalmoaApplication.class, args);
	}

}
