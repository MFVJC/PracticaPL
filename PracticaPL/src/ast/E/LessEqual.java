package ast.E;

import ast.E.TipoE;

public class LessEqual extends EBin {
   public LessEqual(E opnd1, E opnd2) {
	     super(opnd1,opnd2);  
	   }     
   public TipoE tipoExpresion() {return TipoE.LESSEQUAL;}
   public String toString() {return "{{__LE___}"+opnd1().toString()+opnd2().toString()+"}";}
}
