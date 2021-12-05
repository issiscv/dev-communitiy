package com.example.boardapi.converter;

import com.example.boardapi.entity.enumtype.SearchCond;
import org.springframework.core.convert.converter.Converter;

public class StringToSearchCondConverter implements Converter<String, SearchCond> {

    @Override
    public SearchCond convert(String source) {
        SearchCond searchCond = SearchCond.ERROR;

        if (source.equalsIgnoreCase("title")) {
            searchCond = SearchCond.TITLE;
        } else if (source.equalsIgnoreCase("content")) {
            searchCond = SearchCond.CONTENT;
        } else if (source.equalsIgnoreCase("all")) {
            searchCond = SearchCond.ALL;
        }

        return searchCond;
    }
}
