package ast.E;

import ast.E.TipoE;

public class NotEqual extends EBin {
   public NotEqual(E opnd1, E opnd2,int fila,int columna) {
	     super(opnd1,opnd2,fila,columna); 
	   }     
   public TipoE tipoExpresion() {return TipoE.NOTEQUAL;}
   public String toString() {return "{{__NE___}"+opnd1().toString()+opnd2().toString()+"}";}
}
