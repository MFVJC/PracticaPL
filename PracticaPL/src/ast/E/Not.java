package ast.E;

import ast.E.TipoE;

public class Not extends E {
	private E opnd1;
	
   public Not(E opnd1) {
     this.opnd1 = opnd1;  
   }     
   public TipoE tipo() {return TipoE.NOT;}
   public String toString() {return "{{__Not__}" + opnd1 + "}";}
}
