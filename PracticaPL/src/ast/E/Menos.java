package ast.E;

import ast.E.TipoE;

public class Menos extends E {
	private E opnd1;
   public Menos(E opnd1,int fila,int columna) {
     this.opnd1 = opnd1; 
     this.fila = fila;
     this.columna = columna;
   }     
   public TipoE tipoExpresion() {return TipoE.MENOS;}
   public String toString() {return "{{_Menos_}" + opnd1 + "}";}
}
