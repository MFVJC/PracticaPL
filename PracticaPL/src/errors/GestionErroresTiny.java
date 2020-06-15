package errors;

import alex.UnidadLexica;
import asint.Main;
public class GestionErroresTiny {
	public static int numeroErroresSemanticos = 0;
	public static int numeroErroresSintacticos = 0;
	
	public static void errorLexico(int fila,int columna, String lexema) {
		System.err.println("ERROR lexico. Fila "+ fila + " columna " + columna + ": Caracter inesperado: " + lexema); 

		System.exit(1);
	}  
   public void errorSintactico(UnidadLexica unidadLexica) { //pasar fila y columna
	   numeroErroresSintacticos++;
	   System.err.println("ERROR sintactico " + numeroErroresSintacticos + ". Fila " + unidadLexica.getFila()
	   + " columna " + unidadLexica.getColumna() + ": Elemento inesperado "+ unidadLexica.obtenerLexema());

    // System.exit(1);
   }
   public static void errorSemantico(String mensaje,int fila,int columna) { // pasar fila y columna
	   numeroErroresSemanticos++;
	   System.err.println("ERROR semantico " + numeroErroresSemanticos + ". Fila "+fila+ " Columna " +columna+ ": " + mensaje);
   }
}
