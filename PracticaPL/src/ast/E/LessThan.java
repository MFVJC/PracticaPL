package ast.E;

import ast.E.TipoE;

public class LessThan extends EBin {
   public LessThan(E opnd1, E opnd2,int fila,int columna) {
	     super(opnd1,opnd2,fila,columna); 
	   }     
   public TipoE tipoExpresion() {return TipoE.LESSTHAN;}
   public String toString() {return "{{__LT___}"+opnd1().toString()+opnd2().toString()+"}";}
}
