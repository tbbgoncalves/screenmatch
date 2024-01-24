package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConversorDados;

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

        String json = consumoApi.pegarDados(URL + serieName + APIKEY);
        DadosSerie dadosSerie = conversorDados.pegarDados(json, DadosSerie.class);

        System.out.println(dadosSerie);

        List<DadosTemporada> dadosTemporadas = new ArrayList<>();
        for(int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            json = consumoApi.pegarDados(URL + serieName + "&season=" + i + APIKEY);
            DadosTemporada dadosTemporada = conversorDados.pegarDados(json, DadosTemporada.class);
            dadosTemporadas.add(dadosTemporada);
        }

        dadosTemporadas.forEach(t -> {
            System.out.println("\nEpisódios da Season " + t.numero());
            t.dadosEpisodios().forEach(e -> System.out.println("E%d: %s".formatted(e.numero(), e.titulo())));
        });

        List<DadosEpisodio> dadosEpisodios = dadosTemporadas.stream()
                .flatMap(t -> t.dadosEpisodios().stream())
                .collect(Collectors.toList());

//        System.out.println("\nTop 10 Episódios");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro (N/A): " + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenação: " + e))
//                .limit(10)
//                .peek(e -> System.out.println("Limite de 10: " + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Map toUpperCase: " + e))
//                .forEach(System.out::println);

        List<Episodio> episodios = dadosTemporadas.stream()
                .flatMap(t -> t.dadosEpisodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);

//        System.out.println("Digite o nome do episódio a ser buscado:");
//        String trechoTitulo = leitura.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//        if(episodioBuscado.isPresent()) {
//            System.out.println("Episódio encontrado: " + episodioBuscado.get());
//        }
//        else {
//            System.out.println("Episódio não encontrado");
//        }

//
//        System.out.println("A partir de que ano você deseja ver os episódios:");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                        ", Episódio: " + e.getTitulo() +
//                        ", Data de lançamento: " + e.getDataLancamento().format(dateTimeFormatter)
//                        ));

        Map<Integer, Double> avaliacoesTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
//                .mapToDouble(Episodio::getAvaliacao)
//                .summaryStatistics();
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println(est);
    }
}