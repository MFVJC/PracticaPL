package ast.E;

import ast.E.TipoE;

public class SquareBracket extends EBin {
   public SquareBracket(E opnd1, E opnd2,int fila,int columna) {
	     super(opnd1,opnd2,fila,columna); 
   }     
   public TipoE tipoExpresion() {return TipoE.SQUAREBRACKET;}
   public String toString() {
	  return "{{__SBra_}" + opnd1().toString() + opnd2().toString() + "}";
   }
}
