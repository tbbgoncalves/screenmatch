package br.com.alura.screenmatch.service;

public interface IDataConverter {
    <T> T getData(String json, Class<T> classType);
}
