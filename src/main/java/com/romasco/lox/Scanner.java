package com.romasco.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.romasco.lox.TokenType.*;
import static java.lang.Double.parseDouble;



public class Scanner {
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }
    final String source;
    final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while(!isAtEnd()) {
            start = current;
            scanToken();
        }

        this.tokens.add(new Token(EOF, "",  null, line));
        return this.tokens;
    }

    private void scanToken() {
        char c = advance();
        switch(c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;

            case '!':
                addToken(matchEqual() ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(matchEqual() ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(matchEqual() ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(matchEqual() ? GREATER_EQUAL : GREATER);
                break;

            case '/':
                if(match('/')) {
                    while(!isAtEnd() && peek() != '\n') advance();
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            default:
                if(isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character: " + c);
                }
                break;
        }
    }


    private void identifier() {
        while(isAlphaNumberic(peek())) advance();

        String text = subString();
        TokenType tokenType = keywords.getOrDefault(text, IDENTIFIER);
        addToken(tokenType);
    }


    private void number() {
        while(isDigit(peek())) advance();
        if(peek() == '.' && isDigit(peekNext())) {
            advance(); // consume '.'
            while(isDigit(peek())) advance();
        }
        addToken(NUMBER, parseDouble(subString()));
    }


    private void string() {
        while(peek() != '"' && !isAtEnd()) {
            if(peek() == '\n') line++;
            advance();
        }

        if(isAtEnd()) {
            Lox.error(line, "Unterminated string");
            return;
        }
        advance(); // closing '"'
        String literal = source.substring(start+1, current-1);
        addToken(STRING, literal);
    }

    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if(current+1 >= source.length()) return '\0';
        return source.charAt(current+1);
    }


    private boolean isAlpha(char c) {
        return (c >='a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }


    private boolean isDigit(char c) {
        return c >= '0' && c <='9';
    }

    private boolean isAlphaNumberic(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean match(char expected) {
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private boolean matchEqual() {
        return match('=');
    }

    private String subString() {
        if(current > source.length()) {
            Lox.error(line, "Parsing error: end of file before identifier completion");
            return "";
        }
        return source.substring(start, current);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = subString();
        Token tok = new Token(type, text, literal, line);
        tokens.add(tok);
    }
}
