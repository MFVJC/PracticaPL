package ast.E;

import ast.E.TipoE;

public class LessThan extends EBin {
   public LessThan(E opnd1, E opnd2) {
	     super(opnd1,opnd2);  
	   }     
   public TipoE tipo() {return TipoE.LESSTHAN;}
   public String toString() {return "{{__LT___}"+opnd1().toString()+opnd2().toString()+"}";}
}
