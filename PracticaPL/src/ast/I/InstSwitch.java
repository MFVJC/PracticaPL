package ast.I;

import java.util.List;

import ast.E.E;
import javafx.util.Pair;

public class InstSwitch extends I {

	private E condicion;
	private List<Pair<E, List<I>>> cases;

   public InstSwitch(E condicion, List<Pair<E, List<I>>> cases) {
		this.condicion = condicion;
		this.cases = cases;
	}
   
   public TipoI tipo() {return TipoI.IF;}
   public String toString() {
	   String aux = "{{_Swit_}{{" + "_Cond__}" + condicion + "}";
	   
	   for(Pair single_case : cases) {
		   if(single_case.getKey() != null) {
			   String aux2 = single_case.getKey().toString();
			   aux += "{" + aux2.charAt(0) + "_" + aux2.substring(1, aux2.length()-1) + "_" + aux2.charAt(aux2.length()-1);
		   }
		   else aux += "{{_Defa__}";
		   
		   for(I instruccion : (List<I>) single_case.getValue()) {
			   aux += instruccion.toString();
		   }
		   aux += "}";
	   }
	   aux += "}";
	   
	   return aux;
   }
}