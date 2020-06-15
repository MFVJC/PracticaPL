package ast.E;

import ast.E.TipoE;

public class LessEqual extends EBin {
   public LessEqual(E opnd1, E opnd2,int fila,int columna) {
	     super(opnd1,opnd2,fila,columna); 
	   }     
   public TipoE tipoExpresion() {return TipoE.LESSEQUAL;}
   public String toString() {return "{{__LE___}"+opnd1().toString()+opnd2().toString()+"}";}
}
