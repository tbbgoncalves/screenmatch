package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.model.Temporada;
import br.com.alura.screenmatch.service.ApiConsumer;
import br.com.alura.screenmatch.service.DataConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private Scanner input = new Scanner(System.in);
    private final String URL = "https://www.omdbapi.com/?t=";
    private final String APIKEY = "&apikey=7b75c184";
    private ApiConsumer apiConsumer = new ApiConsumer();
    private DataConverter dataConverter = new DataConverter();

    public void showMenu() {
        System.out.println("Digite o nome da série a ser buscada:");
        var serieName = input.nextLine().replace(" ", "+");

        String json = apiConsumer.getData(URL + serieName + APIKEY);
        Serie serie = dataConverter.getData(json, Serie.class);

        System.out.println(serie);

        List<Temporada> temporadas = new ArrayList<>();
        for(int i = 1; i <= serie.totalTemporadas(); i++) {
            json = apiConsumer.getData(URL + serieName + "&season=" + i + APIKEY);
            Temporada temporada = dataConverter.getData(json, Temporada.class);
            temporadas.add(temporada);
        }
//        temporadas.forEach(System.out::println);

//        for(int i = 0; i < temporadas.size(); i++) {
//            System.out.println("\nLista de Episódios da Season " + (i + 1));
//            List<Episodio> espisodiosTemporada = temporadas.get(i).episodios();
//            for(int j = 0; j < espisodiosTemporada.size(); j++) {
//                System.out.println("E%d: %s".formatted((j + 1), espisodiosTemporada.get(j).titulo()));
//            }
//        }

        temporadas.forEach(t -> {
            System.out.println("\nEpisódios da Season " + t.numero());
            t.episodios().forEach(e -> System.out.println("E%d: %s".formatted(e.numero(), e.titulo())));
        });
    }
}
