package ast.E;

import ast.E.TipoE;

public class Or extends EBin {
   public Or(E opnd1, E opnd2,int fila,int columna) {
	     super(opnd1,opnd2,fila,columna); 
   }     
   public TipoE tipoExpresion() {return TipoE.OR;}
   public String toString() {return "{{__OR___}"+opnd1().toString()+opnd2().toString()+"}";}
}
