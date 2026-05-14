package fes.aragon.compilador;
import java_cup.runtime.Symbol;

%%

%{
    // Estos métodos te servirán para reportar la línea y columna exacta si hay un error
    public int getYyline() {
        return yyline;
    }

    public int getYycolumn() {
        return yycolumn;
    }
%}

%class Lexer
%public
%char
%line
%column
%cup
%ignorecase
%type java_cup.runtime.Symbol

%eofval{
    return new Symbol(sym.EOF, new String("Fin del archivo"));
%eofval}

/* Macros */
DIGITO = [0-9]
ESPACIO = [ \t\f]
SALTO   = \n|\r|\r\n

%%

/* Palabras Reservadas del Juego */
"inicio"    { System.out.println("Token: INICIO"); return new Symbol(sym.INICIO); }
"derecha"   { System.out.println("Token: DERECHA"); return new Symbol(sym.DERECHA); }
"izquierda" { System.out.println("Token: IZQUIERDA"); return new Symbol(sym.IZQUIERDA); }
"abajo"     { System.out.println("Token: ABAJO"); return new Symbol(sym.ABAJO); }
"arriba"    { System.out.println("Token: ARRIBA"); return new Symbol(sym.ARRIBA); }
"mover"     { System.out.println("Token: MOVER"); return new Symbol(sym.MOVER); }
"repite"    { System.out.println("Token: REPITE"); return new Symbol(sym.REPITE); }
"parar"     { System.out.println("Token: PARAR"); return new Symbol(sym.PARAR); }

/* Números (Capturamos el valor entero para usarlo en el Parser) */
{DIGITO}+   {
                System.out.println("Token: NUMERO -> " + yytext());
                return new Symbol(sym.NUMERO, Integer.parseInt(yytext()));
            }

/* Ignorar Blancos y Saltos */
{ESPACIO}   { /* Ignorar */ }
{SALTO}     { /* Ignorar */ }

/* Error Léxico */
. {
    System.out.println("Error léxico: <" + yytext() + "> en línea " + (yyline+1) + ", columna " + (yycolumn+1));
    // Más adelante, en lugar de System.out, concatenaremos este mensaje en tu JTextArea de errores
}