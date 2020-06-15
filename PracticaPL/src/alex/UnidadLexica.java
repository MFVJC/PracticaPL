package alex;

import java_cup.runtime.Symbol;

public class UnidadLexica extends Symbol {
	//Token value ocn filas y columnas
   private int fila;
   private int columna;
   public UnidadLexica(int fila, int columna,int clase, String lexema) {
     super(clase,new TokenValue(lexema,fila,columna)); // new TokenValue(lexema,fila,columna)
	 this.fila = fila;
	 this.columna = columna;
   }
   public int clase () {return sym;}
   public String lexema() {return (String)value;}
   public int getFila() {return fila;}
   public int getColumna() {return columna;}
}