package ast.E;

import ast.E.TipoE;

public class GreaterEqual extends EBin {
   public GreaterEqual(E opnd1, E opnd2) {
	     super(opnd1,opnd2);  
	   }     
   public TipoE tipo() {return TipoE.GREATEREQUAL;}
   public String toString() {return "{{__GE___}"+opnd1().toString() + opnd2().toString()+"}";}
}
