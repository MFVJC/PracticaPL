package ast.E;

import ast.E.TipoE;

public class Iden extends E {
	private String nombre;
   
	public Iden(String opnd1) {
     this.nombre = opnd1;  
   }     
   public TipoE tipoExpresion() {return TipoE.IDEN;}
   public String toString() {
	   return "{" + nombre + "}";
   }
   public String getNombre() {
	   return nombre;
   }
}
