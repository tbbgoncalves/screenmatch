package br.com.alura.screenmatch.service;

public interface IConversorDados {
    <T> T getData(String json, Class<T> classType);
}
