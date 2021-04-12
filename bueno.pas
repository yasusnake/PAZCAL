PROGRAM INFLACION ( INPUT, OUTPUT );
{
Asumiendo tasas de inflación anual de 7%, 8%, y 10%,
encontrar el factor por el cual cualquier moneda, tales como
el franco, el dólar, la libra esterlina, el marco, el rublo, el yen
o el florín han sido devaluadas en 1, 2, ...., N anos.
}
VAR
MAXANOS: INTEGER;
ANO: INTEGER;
FACTOR1: REAL;
FACTOR2: REAL;
FACTOR3: REAL;
NOSEUSA: CHAR;
(* Inicio del programa INFLACION *)
BEGIN
{ Entrada de datos }
WRITELN;
WRITELN ( OUTPUT );
WRITELN ( OUTPUT, 'POR FAVOR, indique la cantidad máxima de años:' );
READLN ( MAXANOS );
(* Inicialización de variables *)
ANO := 0;
FACTOR1 := 1.0;
FACTOR2 := 1.0;
FACTOR3 := 1.0;
{ Cálculos y salida de datos }
WRITELN ( OUTPUT );
WRITELN ( ' ANO 7% 8% 10%' );
WRITELN;
REPEAT
ANO := ANO + 1;
FACTOR1 := ( FACTOR1 * 1.07 );
FACTOR2 := ( FACTOR2 ) * 1.08;
FACTOR3 := FACTOR3 * ( 1.10 );
WRITELN ( ANO: 5, FACTOR1: 7:3, FACTOR2: 7:3, FACTOR3: 7:3 )
UNTIL ANO = MAXANOS;
WRITELN;
WRITELN ( OUTPUT );
WRITELN;
READLN;
END .
