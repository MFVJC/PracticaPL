package alex;

import java_cup.runtime.Symbol;

public class UnidadLexica extends Symbol {
	//Token value ocn filas y columnas
   private int fila;
   public UnidadLexica(int fila, int clase, String lexema) {
     super(clase,lexema); // new TokenValue(lexema,fila,columna)
	 this.fila = fila;
   }
   public int clase () {return sym;}
   public String lexema() {return (String)value;}
   public int fila() {return fila;}
}