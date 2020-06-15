package ast.E;

import ast.E.TipoE;

public class And extends EBin {
   public And(E opnd1, E opnd2,int fila,int columna) {
	     super(opnd1,opnd2,fila,columna); 
   }     
   public TipoE tipoExpresion() {return TipoE.AND;}
   public String toString() {return "{{__AND__}"+opnd1().toString()+opnd2().toString()+"}";}
}