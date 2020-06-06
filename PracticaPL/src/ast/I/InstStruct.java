package ast.I;

import java.util.List;

import ast.E.E;

public class InstStruct extends I {

	private E iden;
	private List<I> declaraciones;

   public InstStruct(E iden, List<I> declaraciones) {
	    this.iden = iden;
	    this.declaraciones = declaraciones;
	}
   
   public TipoI tipo() {return TipoI.ASIG;}
   
   public String toString() {
	   String aux = "{{_Stru__}";
	   
	   for(I declaracion : declaraciones) {
		   aux += declaracion.toString();
	   }
	   aux += "}";
	   
	   return aux;
   }
}
