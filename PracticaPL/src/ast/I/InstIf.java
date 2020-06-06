package ast.I;

import java.util.List;
import ast.E.E;

public class InstIf extends I {
	  
	private E condicion;
	private List<I> cuerpo_if;
	private List<I> cuerpo_else;

   public InstIf(E condicion, List<I> cuerpo_if, List<I> cuerpo_else) {
		this.condicion = condicion;
		this.cuerpo_if = cuerpo_if;
		this.cuerpo_else = cuerpo_else;
	}
   
   public TipoI tipo() {return TipoI.IF;}
   public String toString() {
	   String aux = "{{__If___}{{_Cond__}" + condicion + "}{{_Cuer__}";
	   
	   for(I ins : cuerpo_if) aux += ins.toString();
	   aux += "}";
	   if(cuerpo_else != null) {
		   aux += "{{_Else__}";	   
		   for(I ins : cuerpo_else) aux += ins.toString();
		   aux += "}";
	   }
	   aux += "}";
	   
	   return aux;
   }

}
