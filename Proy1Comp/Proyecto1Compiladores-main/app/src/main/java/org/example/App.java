package org.example;
//RuntimeException Linea 942
import java.nio.file.*;
import java.io.*;
import org.example.ParserLexer.BasicLexerCup;
import org.example.ParserLexer.parser;
import org.example.ErrorHandler;
import org.example.ParserLexer.sym;
import java_cup.runtime.*;

/**
 * Clase principal del programa.
 * Entrada: Archivos JFlex/CUP y archivo de código fuente.
 * Salida: Archivos generados, tokens y log de errores.
 * Restricción: Las rutas y permisos deben ser válidos.
 */
public class App {
    // Rutas constantes
    private static final String INPUT_FILE = "app/src/main/resources/ejemplo1.txt";
    private static final String OUTPUT_FILE = "app/src/main/resources/output.txt";
    private static final String ERROR_FILE = "app/src/main/resources/errors.log";
    private static final String TOKENS_FILE = "app/src/main/resources/tokens.log";

    public static void GenerarLexerParser() throws Exception {
        // 1. Generar analizadores (esto solo crea las clases)
        String basePath = System.getProperty("user.dir");
        //System.out.println("Directorio base: " + basePath);
        
        MainFlexCup mfjc = new MainFlexCup();
        String fullPathLexer = Paths.get(basePath, "app", "src", "Adicionales", "BasicLexerCup.jflex").toString();
        String fullPathParser = Paths.get(basePath, "app", "src", "Adicionales", "BasicParser.cup").toString();
        
        mfjc.iniLexerParser(fullPathLexer, fullPathParser);
        
        // 2. Mover archivos generados
        Path destDir = Paths.get(basePath, "app", "src", "main", "java", "org", "example", "ParserLexer");
        Files.createDirectories(destDir);
        
        Files.move(Paths.get(basePath, "sym.java"), destDir.resolve("sym.java"), StandardCopyOption.REPLACE_EXISTING);
        Files.move(Paths.get(basePath, "parser.java"), destDir.resolve("parser.java"), StandardCopyOption.REPLACE_EXISTING);
        Files.move(Paths.get(basePath, "app", "src", "Adicionales", "BasicLexerCup.java"), 
                 destDir.resolve("BasicLexerCup.java"), StandardCopyOption.REPLACE_EXISTING);
        
        // 3. Ahora ANALIZAR el código fuente con el lexer generado
        analizarCodigoFuente(INPUT_FILE, TOKENS_FILE);
        
        // 4. Escribir resultados
        FileManager.writeFile(OUTPUT_FILE, "Análisis completado correctamente");
        System.out.println("\nTokens registrados en: " + TOKENS_FILE);
        Path tokenPath = Paths.get(TOKENS_FILE);
        if (Files.exists(tokenPath)) {
            System.out.println(Files.readString(tokenPath));
        } else {
            System.out.println("No se encontraron tokens generados.");
        }

    }

     /**
     * Analiza el archivo fuente dado.
     * Entrada: Ruta del archivo fuente.
     * Salida: Archivo de tokens generado.
     * Restricción: El archivo fuente debe existir.
     */
    private static void analizarCodigoFuente(String inputFile, String tokensFile) throws Exception {
        // Limpiar archivo de tokens previo si existe
        Files.deleteIfExists(Paths.get(tokensFile));
        
        // Crear directorio para tokens si no existe
        Files.createDirectories(Paths.get(tokensFile).getParent());
        
        // Ruta para el archivo de errores
        String errorsFile = "app/src/main/resources/errors.log";
        Files.createDirectories(Paths.get(errorsFile).getParent());
        Files.deleteIfExists(Paths.get(errorsFile));
        
        // Leer el código fuente
        String sourceCode = FileManager.readFile(inputFile);
        System.out.println("=== CÓDIGO FUENTE ===");
        System.out.println(sourceCode);
        System.out.println("=====================");
        
        // Crear el ErrorHandler compartido
        ErrorHandler errorHandler = new ErrorHandler(errorsFile);
        errorHandler.setContinueOnError(true); 
        
        try (Reader reader = new StringReader(sourceCode)) {
            // Crear el lexer
            BasicLexerCup lexer = new BasicLexerCup(reader);
            lexer.setErrorHandler(errorHandler);
            
            // Crear el parser y conectarlo con el lexer
            parser p = new parser(lexer);
            p.setErrorHandler(errorHandler);
            
            try {
                System.out.println("Iniciando análisis sintáctico...");
                Symbol parseResult = p.parse();
                System.out.println("Análisis sintáctico completado exitosamente.");
            } catch (Exception e) {
                System.out.println("Análisis sintáctico completado con errores: " + e.getMessage());
            }
            
            // Mostrar resultados y estadísticas de errores
            System.out.println("\n=== RESULTADOS DEL ANÁLISIS ===");
            System.out.println("Archivo analizado: " + inputFile);
            System.out.println("Log de tokens: " + Paths.get(tokensFile).toAbsolutePath());
            System.out.println("Log de errores: " + Paths.get(errorsFile).toAbsolutePath());
            System.out.println(errorHandler.getErrorSummary());
        }

    }

    public String getGreeting() {
        return "Hello World!";
    }

    // Método principal que ejecuta el análisis léxico y sintáctico
    // Entrada: argumentos de línea de comandos (no utilizados)
    // Salida: análisis realizado, archivos generados, errores reportados si ocurren
    // Restricción: los archivos requeridos deben existir y tener permisos adecuados
    
    public static void main(String[] args) {
        try {
            GenerarLexerParser();
            System.out.println("Proceso completado exitosamente");
        } catch (Exception e) {
            try {
                FileManager.writeFile(ERROR_FILE, "Error: " + e.getMessage());
            } catch (IOException ioEx) {
                System.err.println("Error al escribir log: " + ioEx.getMessage());
            }
            System.err.println("Error durante el análisis: " + e.getMessage());
            e.printStackTrace();
        }
    }

}