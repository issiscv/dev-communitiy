package com.example.boardapi.converter;

import com.example.boardapi.entity.enumtype.BoardType;
import org.springframework.core.convert.converter.Converter;

public class StringToBoardTypeConverter implements Converter<String, BoardType> {

    @Override
    public BoardType convert(String source) {
        BoardType boardType = BoardType.ERROR;

        if (source.equalsIgnoreCase("free")) {
            boardType = BoardType.FREE;
        } else if (source.equalsIgnoreCase("qna")) {
            boardType = BoardType.QNA;
        } else if (source.equalsIgnoreCase("tech")) {
            boardType = BoardType.TECH;
        }

        return boardType;
    }
}
