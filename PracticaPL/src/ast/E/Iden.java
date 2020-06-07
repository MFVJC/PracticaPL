package ast.E;

import ast.E.TipoE;

public class Iden extends E {
	private String opnd1;
   
	public Iden(String opnd1) {
     this.opnd1 = opnd1;  
   }     
   public TipoE tipoExpresion() {return TipoE.IDEN;}
   public String toString() {
	   return "{" + opnd1 + "}";
   }
}
