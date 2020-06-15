package ast.E;

import ast.E.TipoE;

public class BasicTrue extends E {
	int fila;
	int columna;
   public BasicTrue(int fila,int columna) {
	     this.fila = fila;
	     this.columna = columna;
   }
   public TipoE tipoExpresion() {return TipoE.BASICTRUE;}
   public String toString() {return "{true}";}
}
