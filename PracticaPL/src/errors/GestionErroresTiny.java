package errors;

import alex.UnidadLexica;
public class GestionErroresTiny {
	private static int numeroErroresSemanticos = 0;
	public void errorLexico(int fila, String lexema) {
		System.out.println("ERROR lexico fila "+fila+": Caracter inesperado: "+lexema); 
		System.exit(1);
	}  
   public void errorSintactico(UnidadLexica unidadLexica) {
	   System.out.print("ERROR sintactico fila "+unidadLexica.fila()+": Elemento inesperado "+unidadLexica.value);
    // System.exit(1);
   }
   public static void errorSemantico(String mensaje) {
	   numeroErroresSemanticos++;
	   System.out.println("ERROR semantico número " + numeroErroresSemanticos + ": " + mensaje);
   }
}
