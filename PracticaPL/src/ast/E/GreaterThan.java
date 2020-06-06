package ast.E;

import ast.E.TipoE;

public class GreaterThan extends EBin {
   public GreaterThan(E opnd1, E opnd2) {
	     super(opnd1,opnd2);  
	   }     
   public TipoE tipo() {return TipoE.GREATERTHAN;}
   public String toString() {return "{{__GT___}"+opnd1().toString() +opnd2().toString()+"}";}

}
