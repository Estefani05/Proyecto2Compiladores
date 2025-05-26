package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.example.ParserLexer.BasicLexerCup;

import java_cup.internal_error;
import java_cup.parser;
import java_cup.runtime.Symbol;
import jflex.exceptions.SilentExit;

public class MainFlexCup {

    public void iniLexerParser(String rutaLexer, String rutaParser) throws internal_error, Exception {
        GenerateLexer(rutaLexer);
        Generateparser(rutaParser);
    }

    //Genera el archivo del lexer
    public void GenerateLexer(String ruta) throws IOException, SilentExit{
        String[] strArr = {ruta};
        jflex.Main.generate(strArr);
    }

    public parser crearParser(BasicLexerCup lexer) throws Exception {
        parser p = new parser(lexer);
        return p;
    }

    //Genera los archivos del parser
    public void Generateparser(String ruta) throws internal_error, IOException, Exception{
        String[] strArr = {ruta};
        java_cup.Main.main(strArr);
    }

    public void ejercicioLexer(String rutaScanear) throws IOException
    {
        Reader reader = new BufferedReader(new FileReader (rutaScanear));
        reader.read();
        BasicLexerCup lex = new BasicLexerCup(reader);
        int i = 0;
        Symbol token;
        while(true){

            token = lex.next_token();
            if(token.sym != 0){
                System.out.println("Token: " + token.sym + ", Valor: " + (token.value == null ? lex.yytext() : token.value.toString()));
            }else{
                System.out.println("Cantidad de lexemas encontrados: "+i);
                return;
            }
            i++;
        }
    }
}

