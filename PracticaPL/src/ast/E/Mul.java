package ast.E;

import ast.E.TipoE;

public class Mul extends EBin {
   public Mul(E opnd1, E opnd2,int fila,int columna) {
	     super(opnd1,opnd2,fila,columna); 
   }     
   public TipoE tipoExpresion() {return TipoE.MUL;}
   public String toString() {return "{{__Mul__}" + opnd1().toString() + opnd2().toString() + "}";}
}
