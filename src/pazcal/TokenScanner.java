package pazcal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TokenScanner {
    private static String tokenName = "";
    private static int lineRow = 0;
    private static int lineCol = 0;
    private static boolean readingString = false;
    private static boolean readingNumber = false;
    private static boolean isFloat = false;
    private static boolean sciNotation = false;
    private static boolean readingColon = false;
    private static boolean readingBool = false;
    private static boolean readingDot = false;

    private static ArrayList<Token> tokenArrayList = new ArrayList<>();

    enum TYPE {
        LETTER, DIGIT, SPACE, OPERATOR, QUOTE
    }

    private static final HashMap<String, String> KEYWORDS_TOKEN;
    static {
        KEYWORDS_TOKEN = new HashMap<>();
        String word;

        try {
            Scanner sc = new Scanner(new File("keywords.txt"));
            
            while(sc.hasNext()){
                word = sc.next();
                KEYWORDS_TOKEN.put(word, String.format("TK_%s", word.toUpperCase()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final HashMap<String, String> OPERATORS_TOKEN;
    static {
        OPERATORS_TOKEN = new HashMap<>();
        OPERATORS_TOKEN.put("(", "TK_OPEN_PARENTHESIS");
        OPERATORS_TOKEN.put(")", "TK_CLOSE_PARENTHESIS");
        OPERATORS_TOKEN.put("[", "TK_OPEN_SQUARE_BRACKET");
        OPERATORS_TOKEN.put("]", "TK_CLOSE_SQUARE_BRACKET");
        OPERATORS_TOKEN.put(".", "TK_DOT");
        OPERATORS_TOKEN.put("..", "TK_RANGE");
        OPERATORS_TOKEN.put(":", "TK_COLON");
        OPERATORS_TOKEN.put(";", "TK_SEMI_COLON");
        OPERATORS_TOKEN.put("+", "TK_PLUS");
        OPERATORS_TOKEN.put("-", "TK_MINUS");
        OPERATORS_TOKEN.put("*", "TK_MULTIPLY");
        OPERATORS_TOKEN.put("/", "TK_DIVIDE");
        OPERATORS_TOKEN.put("<", "TK_LESS_THAN");
        OPERATORS_TOKEN.put("<=", "TK_LESS_THAN_EQUAL");
        OPERATORS_TOKEN.put(">", "TK_GREATER_THAN");
        OPERATORS_TOKEN.put(">=", "TK_GREATER_THAN_EQUAL");
        OPERATORS_TOKEN.put(":=", "TK_ASSIGNMENT");
        OPERATORS_TOKEN.put(",", "TK_COMMA");
        OPERATORS_TOKEN.put("=", "TK_EQUAL");
        OPERATORS_TOKEN.put("<>", "TK_NOT_EQUAL");

    }

    private static final HashMap<String, TYPE> CHAR_TYPE;
    static {
        CHAR_TYPE = new HashMap<>();

        for (int i = 65; i < 91; i++){
            // Add letters
            String currentChar = String.valueOf(Character.toChars(i)[0]);
            CHAR_TYPE.put(currentChar, TYPE.LETTER);
            CHAR_TYPE.put(currentChar.toLowerCase(), TYPE.LETTER);
        }
        for (int i = 48; i < 58; i++){
            // Add digits
            String currentChar = String.valueOf(Character.toChars(i)[0]);
            CHAR_TYPE.put(currentChar, TYPE.DIGIT);
        }
        for (int i = 1; i < 33; i++){
            // Add spaces
            String currentChar = String.valueOf(Character.toChars(i)[0]);
            CHAR_TYPE.put(currentChar, TYPE.SPACE);
        }

        for (String key: OPERATORS_TOKEN.keySet()) {
            CHAR_TYPE.put(key, TYPE.OPERATOR);
        }

        // Add signle quote
        CHAR_TYPE.put(String.valueOf(Character.toChars(39)[0]), TYPE.QUOTE);
    }

    public static ArrayList<Token> scan(File file) throws FileNotFoundException {
        // Delimiter to scan each char
        Scanner sc = new Scanner(file).useDelimiter("");

        while (sc.hasNext()) {
            char element = sc.next().toLowerCase().charAt(0);

            checkCharacter(element);
        }

        tokenName = "EOF";
        generateToken("TK_EOF");

        return tokenArrayList;
    }

    public static void checkCharacter(char element){
        switch (CHAR_TYPE.get(String.valueOf(element))){

            case LETTER:
                if (!readingNumber) {
                    tokenName += element;
                }

                if (element == 'E' && readingNumber) {
                    tokenName += element;
                    sciNotation = true;
                }

                break;
            case DIGIT:
                if (tokenName.isEmpty()) {
                    readingNumber = true;
                }

                tokenName += element;

                break;
            case SPACE:
                if (readingString){
                    // Append to a string
                    tokenName += element;
                } else if (readingColon) {
//                    System.out.println(OPERATORS_TOKEN.get(tokenName));
                    generateToken(OPERATORS_TOKEN.get(tokenName));

                    readingColon = false;

                } else if (readingBool) {
//                    System.out.println(OPERATORS_TOKEN.get(tokenName));
                    generateToken(OPERATORS_TOKEN.get(tokenName));

                    readingBool = false;

                } else if (!readingNumber) {
                    // End of word
                    tokenName = endOfWord();

                    if (element == Character.toChars(10)[0]){
                        // Check for newline on Unix OS
                        lineRow++;
                        lineCol = 0;
                    } else if (element == Character.toChars(9)[0]){
                        lineCol+=4;
                    } else if (element == Character.toChars(32)[0]){
                        lineCol++;
                    }
                } else {
                    handleNumber();
                }
                break;
            case OPERATOR:
                if (readingDot && element == '.') {
                    if (tokenName.equals(".")) {
                        tokenName = "";
                        generateToken("TK_RANGE");
                    } else {
                        generateToken(tokenName.substring(0, tokenName.length()-2));
                        generateToken("TK_DOT");
                        tokenName = "";
                    }
                    readingDot = false;

                } else if(readingString) {
                    // Append to a string
                    tokenName += element;
                } else if (readingNumber) {
                    if (isFloat && element == '.') {
                        isFloat = false;
                        tokenName = tokenName.substring(0,tokenName.length()-1);
                        handleNumber();

//                        System.out.println("TK_RANGE");
                        generateToken("TK_RANGE");
                        tokenName = "";

                    } else if (sciNotation && (element == '+' || element == '-')) {
                        tokenName += element;
                    } else if (element == '.') {
                        // Found decimal in float
                        isFloat = true;
                        tokenName += element;
                    } else {
                        handleNumber();

//                        System.out.println(OPERATORS_TOKEN.get(String.valueOf(element)));
                        generateToken(OPERATORS_TOKEN.get(String.valueOf(element)));
                    }
                } else if (readingColon && element == '=') {

                    // Handle assignment
                    tokenName += element;
//                    System.out.println(OPERATORS_TOKEN.get(tokenName));
                    generateToken(OPERATORS_TOKEN.get(tokenName));


                    readingColon = false;
                } else if (readingBool) {
                    if (tokenName.equals("<") && ((element == '=') || (element == '>'))) {
                        tokenName += element;
//                        System.out.println(OPERATORS_TOKEN.get(tokenName));
                        generateToken(OPERATORS_TOKEN.get(tokenName));
                    } else if (tokenName.equals(">") && (element == '=')) {
                        tokenName += element;
//                        System.out.println(OPERATORS_TOKEN.get(tokenName));
                        generateToken(OPERATORS_TOKEN.get(tokenName));
                    }

                    readingBool = false;
                } else {
                    if (element == ';') {
                        // Before end of line
                        tokenName = endOfWord();

                        tokenName = ";";
                        generateToken(OPERATORS_TOKEN.get(String.valueOf(element)));
//                        System.out.println("TK_SEMI_COLON");
                    } else if (element == ':') {
                        tokenName = endOfWord();
                        readingColon = true;
                        tokenName += element;
                    } else if (element == '<' || element == '>') {
                        tokenName = endOfWord();
                        readingBool = true;
                        tokenName += element;
                    } else if (element == '.') {
                        tokenName += element;

                        if (tokenName.equals("end.")){
                            generateToken("TK_END");
                            generateToken("TK_DOT");
                        } else {
                            readingDot = true;
                        }
                    } else if (OPERATORS_TOKEN.containsKey(String.valueOf(element))) {
                        tokenName = endOfWord();
//                        System.out.println(OPERATORS_TOKEN.get(String.valueOf(element)));

                        tokenName = String.valueOf(element);
                        generateToken(OPERATORS_TOKEN.get(tokenName));
                    }
                }
                break;
            case QUOTE:
                // Found begin/end quote
                readingString = !readingString;
                tokenName += element;

                if (!readingString) {
                    // Remove trailing quotes
                    tokenName = tokenName.substring(1, tokenName.length()-1);

                    // Found end quote
                    if (tokenName.length() == 1) {
//                        System.out.println("TK_CHARLIT: " + tokenName);
                        generateToken("TK_CHARLIT");
                    } else if (tokenName.length() > 1) {
//                        System.out.println("TK_STRLIT: " + tokenName);
                        generateToken("TK_STRLIT");
                    }
                }
                break;
            default:
                throw new Error("Unhandled element scanned");
        }
    }

    public static String endOfWord(){
        if(KEYWORDS_TOKEN.containsKey(tokenName)){
//            System.out.println(KEYWORDS_TOKEN.get(tokenName));
            generateToken(KEYWORDS_TOKEN.get(tokenName));
        } else {
            if (tokenName.length() > 0) {

                if(tokenName.equals("true") || tokenName.equals("false")) {
//                    System.out.println("TK_BOOLLIT: " + tokenName);
                    generateToken("TK_BOOLLIT");
                } else {
//                    System.out.println("TK_IDENTIFIER: " + tokenName);
                    generateToken("TK_IDENTIFIER");
                }
            }
        }

        clearStatuses();

        return tokenName;
    }

    public static void clearStatuses() {
        readingString = false;
        readingNumber = false;
        isFloat = false;
        sciNotation = false;
        readingColon = false;
        readingBool = false;
    }

    public static void generateToken(String tokenType) {
        Token t = new Token(tokenType, tokenName, lineCol, lineRow);
        tokenArrayList.add(t);

        lineCol += tokenName.length();

        tokenName = "";
    }

    public static void handleNumber() {
        readingNumber = false;
        if (isFloat) {
//            System.out.println("TK_FLOATLIT: " + tokenName);
            generateToken("TK_FLOATLIT");
            isFloat = false;
        } else {
//            System.out.println("TK_INTLIT: " + tokenName);
            generateToken("TK_INTLIT");
        }
    }
}
