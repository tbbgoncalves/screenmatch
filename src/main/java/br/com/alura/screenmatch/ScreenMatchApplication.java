package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.service.ApiConsumer;
import br.com.alura.screenmatch.service.DataConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenMatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenMatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var apiConsumer = new ApiConsumer();
		var json = apiConsumer.getData("https://www.omdbapi.com/?t=gilmore+girls&apikey=7b75c184");
		System.out.println(json);

		var dataConverter = new DataConverter();
		Serie serie = dataConverter.getData(json, Serie.class);
		System.out.println(serie);
	}
}
