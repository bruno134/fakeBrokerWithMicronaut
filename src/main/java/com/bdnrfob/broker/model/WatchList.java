package com.bdnrfob.broker.model;

import io.micronaut.core.util.ArrayUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchList {
    private List<Symbol> symbols = new ArrayList<>();
}
