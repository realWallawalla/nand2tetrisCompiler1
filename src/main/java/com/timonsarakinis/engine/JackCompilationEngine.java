package com.timonsarakinis.engine;

import com.timonsarakinis.tokenizer.Tokenizer;
import com.timonsarakinis.tokens.NonTerminalToken;
import com.timonsarakinis.tokens.Token;
import com.timonsarakinis.tokens.types.KeywordType;
import com.timonsarakinis.tokens.types.NonTerminalType;
import com.timonsarakinis.tokens.types.StatementType;
import com.timonsarakinis.utils.IOUtils;

import java.util.function.Predicate;

import static com.timonsarakinis.tokens.types.KeywordType.*;
import static com.timonsarakinis.tokens.types.NonTerminalType.*;
import static com.timonsarakinis.tokens.types.SymbolType.*;
import static com.timonsarakinis.utils.TokenUtils.prepareNonTerminalForOutPut;
import static com.timonsarakinis.utils.TokenUtils.prepareTerminalForOutPut;
import static org.apache.commons.lang3.EnumUtils.getEnumIgnoreCase;
import static org.apache.commons.lang3.EnumUtils.isValidEnumIgnoreCase;

public class JackCompilationEngine implements Engine<Tokenizer> {

    private Tokenizer tokenizer;
    private final String fileName;
    private final String validIdentifier = "^[A-Za-z_]\\w*$";

    public JackCompilationEngine(Tokenizer tokenizer, String fileName) {
        this.tokenizer = tokenizer;
        this.fileName = fileName;
    }

    @Override
    public void compile(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        if (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            //according to contract file has to start with class.
            compileClass();
        }
    }

    private void compileClass() {
        writeNonTerminalToFile(NonTerminalType.CLASS, true);
        eatAndAdvance(KeywordType.CLASS.getValue());
        eatAndAdvance(isIdentifier());
        eatAndAdvance(OPEN_BRACE.getCharacter());

        while (isCurrentTokenClassVar()) {
            compileClassVarDeclaration();
        }

        while (isCurrentTokenSubroutine()) {
            compileSubroutineDeclaration();
        }
        eatAndAdvance(CLOSE_BRACE.getCharacter());
        writeNonTerminalToFile(NonTerminalType.CLASS, false);
    }

    private boolean isCurrentTokenClassVar() {
        return isCurrentTokenEqualTo(FIELD.getValue()) || isCurrentTokenEqualTo(STATIC.getValue());
    }

    private boolean isCurrentTokenSubroutine() {
        return isCurrentTokenEqualTo(CONSTRUCTOR.getValue())
                || isCurrentTokenEqualTo(FUNCTION.getValue())
                || isCurrentTokenEqualTo(METHOD.getValue());
    }

    private void compileClassVarDeclaration() {
        writeNonTerminalToFile(CLASS_VAR_DEC, true);
        eatAndAdvance(token -> token.getValue().equals(FIELD.getValue()) || token.getValue().equals(STATIC.getValue()));

        eatAndAdvance(isType());
        eatAndAdvance(isIdentifier());

        while (isCurrentTokenEqualTo(COMMA.getCharacter())) {
            eatAndAdvance(COMMA.getCharacter());
            eatAndAdvance(isIdentifier());
        }
        eatAndAdvance(SEMICOLON.getCharacter());
        writeNonTerminalToFile(CLASS_VAR_DEC, false);
    }

    private void compileSubroutineDeclaration() {
        writeNonTerminalToFile(SUBROUTINE_DEC, true);
        eatAndAdvance(token -> isCurrentTokenSubroutine());
        eatAndAdvance(isType().or(token -> token.getValue().equals(VOID.getValue())));
        eatAndAdvance(isIdentifier());

        eatAndAdvance(OPEN_PARENTHESIS.getCharacter());
        compileParameterList();
        eatAndAdvance(CLOSE_PARENTHESIS.getCharacter());

        compileSubroutineBody();
        writeNonTerminalToFile(SUBROUTINE_DEC, false);
    }

    private void compileParameterList() {
        writeNonTerminalToFile(PARAMETER_LIST, true);
        eatAndAdvance(isType());
        eatAndAdvance(isIdentifier());

        while (isCurrentTokenEqualTo(COMMA.getCharacter())) {
            eatAndAdvance(COMMA.getCharacter());
            eatAndAdvance(isType());
            eatAndAdvance(isIdentifier());
        }

        writeNonTerminalToFile(PARAMETER_LIST, false);
    }

    private void compileSubroutineBody() {
        writeNonTerminalToFile(SUBROUTINE_BODY, true);
        eatAndAdvance(OPEN_BRACE.getCharacter());
        while (isCurrentTokenEqualTo(VAR.getValue())) {
            compileVarDeclaration();
        }

        writeNonTerminalToFile(STATEMENTS, true);
        while (isValidEnumIgnoreCase(StatementType.class, getCurrentTokenValue())) {
            compileStatements();
        }
        writeNonTerminalToFile(STATEMENTS, false);

        eatAndAdvance(CLOSE_BRACE.getCharacter());
        writeNonTerminalToFile(SUBROUTINE_BODY, false);
    }

    private void compileVarDeclaration() {
        writeNonTerminalToFile(VAR_DEC, true);
        eatAndAdvance(VAR.getValue());
        eatAndAdvance(isType());
        eatAndAdvance(isIdentifier());
        while (isCurrentTokenEqualTo(COMMA.getCharacter())) {
            eatAndAdvance(COMMA.getCharacter());
            eatAndAdvance(isIdentifier());
        }
        eatAndAdvance(SEMICOLON.getCharacter());
        writeNonTerminalToFile(VAR_DEC, false);
    }

    private void compileStatements() {
        switch (getEnumIgnoreCase(StatementType.class, getCurrentTokenValue())) {
            case IF:
                compileIf();
                break;
            case WHILE:
                compileWhile();
                break;
            case DO:
                compileDo();
                break;
            case LET:
                compileLet();
                break;
            case RETURN:
                compileReturn();
                break;
        }
    }

    private void compileIf() {
        writeNonTerminalToFile(StatementType.IF, true);
        eatAndAdvance(KeywordType.IF.getValue());
        eatAndAdvance(OPEN_PARENTHESIS.getCharacter());
        //TODO expression
        compileExpression();
        eatAndAdvance(CLOSE_PARENTHESIS.getCharacter());
        eatAndAdvance(OPEN_BRACE.getCharacter());

        writeNonTerminalToFile(STATEMENTS, true);
        while (isValidEnumIgnoreCase(StatementType.class, getCurrentTokenValue())) {
            compileStatements();
        }
        writeNonTerminalToFile(STATEMENTS, false);

        eatAndAdvance(CLOSE_BRACE.getCharacter());
        if (isCurrentTokenEqualTo(ELSE.getValue())) {
            compileElse();
        }
        writeNonTerminalToFile(StatementType.IF, false);
    }

    private void compileElse() {
        eatAndAdvance(ELSE.getValue());
        eatAndAdvance(OPEN_BRACE.getCharacter());

        writeNonTerminalToFile(STATEMENTS, true);
        compileStatements();
        writeNonTerminalToFile(STATEMENTS, true);

        eatAndAdvance(CLOSE_BRACE.getCharacter());
    }

    private void compileWhile() {
        writeNonTerminalToFile(StatementType.WHILE, true);
        eatAndAdvance(KeywordType.WHILE.getValue());
        eatAndAdvance(OPEN_PARENTHESIS.getCharacter());
        //TODO expression
        compileExpression();
        eatAndAdvance(CLOSE_PARENTHESIS.getCharacter());
        eatAndAdvance(OPEN_BRACE.getCharacter());

        writeNonTerminalToFile(STATEMENTS, true);
        while (isValidEnumIgnoreCase(StatementType.class, getCurrentTokenValue())) {
            compileStatements();
        }
        writeNonTerminalToFile(STATEMENTS, false);

        eatAndAdvance(CLOSE_BRACE.getCharacter());
        writeNonTerminalToFile(StatementType.WHILE, false);
    }

    private void compileDo() {
        writeNonTerminalToFile(StatementType.DO, true);
        eatAndAdvance(KeywordType.DO.getValue());
        eatAndAdvance(isIdentifier());
        if (getCurrentTokenValue().contains(DOT.getCharacter())) {
            eatAndAdvance(DOT.getCharacter());
            eatAndAdvance(isIdentifier());
        }
        eatAndAdvance(OPEN_PARENTHESIS.getCharacter());
        compileExpressionList();
        eatAndAdvance(CLOSE_PARENTHESIS.getCharacter());
        eatAndAdvance(SEMICOLON.getCharacter());
        writeNonTerminalToFile(StatementType.DO, false);
    }

    private void compileLet() {
        writeNonTerminalToFile(StatementType.LET, true);
        eatAndAdvance(KeywordType.LET.getValue());
        eatAndAdvance(isIdentifier());
        if (isCurrentTokenEqualTo(OPEN_BRACKET.getCharacter())) {
            eatAndAdvance(OPEN_BRACKET.getCharacter());
            compileExpression();
            eatAndAdvance(CLOSE_BRACKET.getCharacter());
        }
        eatAndAdvance(EQUALS.getCharacter());
        compileExpression();
        eatAndAdvance(SEMICOLON.getCharacter());
        writeNonTerminalToFile(StatementType.LET, false);
    }

    private void compileReturn() {
        writeNonTerminalToFile(StatementType.RETURN, true);
        eatAndAdvance(RETURN.getValue());
        //TODO expressions
        if (!getCurrentTokenValue().equals(SEMICOLON.getCharacter())) {
            compileExpression();
        }
        eatAndAdvance(SEMICOLON.getCharacter());
        writeNonTerminalToFile(StatementType.RETURN, false);
    }

    private void compileExpressionList() {
        writeNonTerminalToFile(EXPRESSION_LIST, true);
        //TODO expression
        if (!isCurrentTokenEqualTo(CLOSE_PARENTHESIS.getCharacter())) {
            do {
                compileExpression();
            } while (isCurrentTokenEqualTo(COMMA.getCharacter()));
        }
        writeNonTerminalToFile(EXPRESSION_LIST, false);
    }

    private void compileExpression() {
        //TODO expression
        if (isCurrentTokenEqualTo(COMMA.getCharacter())) {
            eatAndAdvance(COMMA.getCharacter());
        }
        writeNonTerminalToFile(EXPRESSION, true);
        compileTerm();
        writeNonTerminalToFile(EXPRESSION, false);
    }

    private void compileTerm() {
        writeNonTerminalToFile(TERM, true);
        eatAndAdvance(isIdentifier());
        writeNonTerminalToFile(TERM, false);
    }

    private void eatAndAdvance(Predicate<Token> predicate) {
        Token currentToken = tokenizer.getCurrentToken();
        if (predicate.test(currentToken)) {
            writeToFile(prepareTerminalForOutPut(currentToken));
            tokenizer.advance();
        } else {
            printError(currentToken);
        }
    }

    private void eatAndAdvance(String value) {
        Token currentToken = tokenizer.getCurrentToken();
        if (currentToken.getValue().equals(value)) {
            writeToFile(prepareTerminalForOutPut(currentToken));
            advance();
        } else {
            printError(currentToken);
        }
    }

    private void advance() {
        if (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
        }
    }

    private void writeToFile(byte[] xml) {
        IOUtils.writeToFile(xml, fileName);
    }

    private void writeNonTerminalToFile(NonTerminalToken token, boolean open) {
        writeToFile(prepareNonTerminalForOutPut(token, open));
    }

    private void printError(Token currentToken) {
        System.out.printf("syntax did not match: %s continuing \n", currentToken.getValue());
    }
    private boolean isCurrentTokenEqualTo(String value) {
        return getCurrentTokenValue().equals(value);
    }

    private String getCurrentTokenValue() {
        return tokenizer.getCurrentToken().getValue();
    }

    private Predicate<Token> isIdentifier() {
        return token -> token.getValue().matches(validIdentifier);
    }

    private Predicate<Token> isType() {
        return token -> token.getValue().equals(INT.getValue())
                || token.getValue().equals(CHAR.getValue())
                || token.getValue().equals(BOOLEAN.getValue())
                || token.getValue().matches(validIdentifier);
    }
}