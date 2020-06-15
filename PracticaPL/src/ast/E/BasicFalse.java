package ast.E;

import ast.E.TipoE;

public class BasicFalse extends E {
	int fila;
	int columna;
   public BasicFalse(int fila,int columna) {
	     this.fila = fila;
	     this.columna = columna;
   }
   
   public TipoE tipoExpresion() {return TipoE.BASICFALSE;}
   public String toString() {return "{false}";}
}
