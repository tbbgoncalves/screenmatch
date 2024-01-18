package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConversorDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private Scanner leitura = new Scanner(System.in);
    private final String URL = "https://www.omdbapi.com/?t=";
    private final String APIKEY = "&apikey=7b75c184";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConversorDados conversorDados = new ConversorDados();

    public void showMenu() {
        System.out.println("Digite o nome da série a ser buscada:");
        var serieName = leitura.nextLine().replace(" ", "+");

        String json = consumoApi.getData(URL + serieName + APIKEY);
        DadosSerie dadosSerie = conversorDados.getData(json, DadosSerie.class);

        System.out.println(dadosSerie);

        List<DadosTemporada> dadosTemporadas = new ArrayList<>();
        for(int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            json = consumoApi.getData(URL + serieName + "&season=" + i + APIKEY);
            DadosTemporada dadosTemporada = conversorDados.getData(json, DadosTemporada.class);
            dadosTemporadas.add(dadosTemporada);
        }

        dadosTemporadas.forEach(t -> {
            System.out.println("\nEpisódios da Season " + t.numero());
            t.dadosEpisodios().forEach(e -> System.out.println("E%d: %s".formatted(e.numero(), e.titulo())));
        });

        List<DadosEpisodio> dadosEpisodios = dadosTemporadas.stream()
                .flatMap(t -> t.dadosEpisodios().stream())
                .collect(Collectors.toList());

        System.out.println("\nTop 5 Episódios");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        List<Episodio> episodios = dadosTemporadas.stream()
                .flatMap(t -> t.dadosEpisodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("A partir de que ano você deseja ver os episódios:");
        var ano = leitura.nextInt();
        leitura.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                        ", Episódio: " + e.getTitulo() +
                        ", Data de lançamento: " + e.getDataLancamento().format(dateTimeFormatter)
                        ));
    }
}
