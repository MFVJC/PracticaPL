package errors;

import alex.UnidadLexica;
public class GestionErroresTiny {
	private static int numeroErroresSemanticos = 0;
	public void errorLexico(int fila,int columna, String lexema) {
		System.err.println("ERROR lexico fila "+fila+" " + "columna " + columna+": Caracter inesperado: "+lexema); 
		System.exit(1);
	}  
   public void errorSintactico(UnidadLexica unidadLexica) { //pasar fila y columna
	   System.err.print("ERROR sintactico fila "+unidadLexica.getFila()+" " + "columna " + unidadLexica.getColumna()+": Elemento inesperado "+unidadLexica.value);
    // System.exit(1);
   }
   public static void errorSemantico(String mensaje,int fila,int columna) { // pasar fila y columna
	   numeroErroresSemanticos++;
	   System.err.println("ERROR semantico " + numeroErroresSemanticos + ". Fila "+fila+ " Columna " +columna+ ": " + mensaje);
   }
}
