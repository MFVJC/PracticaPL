package ast.E;

import ast.E.TipoE;

public class Resta extends EBin {
   public Resta(E opnd1, E opnd2) {
	     super(opnd1,opnd2);
   }     
   public TipoE tipo() {return TipoE.RESTA;}
   public String toString() {return "{{_Resta_}" + opnd1().toString() + opnd2().toString() + "}";}
}
