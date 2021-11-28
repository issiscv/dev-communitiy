package com.example.boardapi.converter;

import com.example.boardapi.entity.enumtype.SortType;
import org.springframework.core.convert.converter.Converter;


public class StringToSortConverter implements Converter<String, SortType> {

    @Override
    public SortType convert(String source) {

        SortType sortType = SortType.ERROR;
        if (source.equalsIgnoreCase("createdDate")) {
            sortType = SortType.CREATEDATE;
        } else if (source.equalsIgnoreCase("likes")) {
            sortType = SortType.LIKES;
        } else if (source.equalsIgnoreCase("commentSize")) {
            sortType = SortType.COMMENTSIZE;
        } else if (source.equalsIgnoreCase("views")) {
            sortType = SortType.VIEWS;
        }

        return sortType;
    }
}
