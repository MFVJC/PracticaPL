package ast.E;

import ast.E.TipoE;

public class Not extends E {
	private E operando1;
	
   public Not(E opnd1) {
     this.operando1 = opnd1;  
   } 
   
   //añadido porque sería necesario si no creamos expresion binaria 
   public E opnd1() {
	   return operando1;
   }
   public TipoE tipoExpresion() {return TipoE.NOT;}
   public String toString() {return "{{__Not__}" + operando1 + "}";}
}
