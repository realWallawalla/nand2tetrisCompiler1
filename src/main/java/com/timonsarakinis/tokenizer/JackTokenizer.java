package com.timonsarakinis.tokenizer;

import com.timonsarakinis.tokens.types.TokenType;

import java.util.List;
import java.util.ListIterator;

public class JackTokenizer implements Tokenizer {
    /*Removes all comments and white space from the input stream and breaks it into Jack-language tokens,
     as specified by the Jack grammar */
    private ListIterator<String> iterator;
    private String currentToken;

    public JackTokenizer(List<String> tokens) {
        this.iterator = tokens.listIterator();
    }

    public boolean hasMoreTokens() {
        return iterator.hasNext();
    }

    public void advance() {
        this.currentToken = iterator.next();
    }

    public TokenType tokenType() {
        return TokenType.KEYWORD;
    }

    public String getCurrentToken() {
        return currentToken;
    }
}
