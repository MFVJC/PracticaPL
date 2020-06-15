package ast.E;

import ast.E.TipoE;

public class GreaterThan extends EBin {
   public GreaterThan(E opnd1, E opnd2,int fila,int columna) {
	     super(opnd1,opnd2,fila,columna); 
	   }     
   public TipoE tipoExpresion() {return TipoE.GREATERTHAN;}
   public String toString() {return "{{__GT___}"+opnd1().toString() +opnd2().toString()+"}";}

}
