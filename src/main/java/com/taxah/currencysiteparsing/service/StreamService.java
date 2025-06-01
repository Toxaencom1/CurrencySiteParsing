package com.taxah.currencysiteparsing.service;

import com.taxah.currencysiteparsing.model.Exchanger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
public class StreamService {
    public Double valueResolver(List<Exchanger> exchangers, Function<? super Exchanger, Double> mappingFunction, boolean isTypeSell) {
        Stream<Double> doubleStream = exchangers.stream()
                .map(mappingFunction)
                .filter(minValue -> minValue > 0.0);
        Optional<Double> value =  isTypeSell ? doubleStream.min(Double::compareTo) : doubleStream.max(Double::compareTo);
        return value.orElseThrow(() -> new RuntimeException("Нет данных"));//
    }

    public List<Exchanger> filterByFunction(List<Exchanger> exchangers, Double value, Function<? super Exchanger, Double> mappingFunction) {
        return exchangers.stream().filter(exchanger -> (Objects.equals(mappingFunction.apply(exchanger), value))).toList();
    }
}
