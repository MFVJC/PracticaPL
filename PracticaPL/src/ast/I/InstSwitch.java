package ast.I;

import java.util.List;

import ast.SentenciaAbstracta;
import ast.E.E;
import javafx.util.Pair;

public class InstSwitch extends I {

	private E condicion;
	private List<Pair<E, List<I>>> cases;
	private SentenciaAbstracta referencia;
	private int fila;
	private int columna;
   public InstSwitch(E condicion, List<Pair<E, List<I>>> cases,int fila,int columna) {
		this.condicion = condicion;
		this.cases = cases;
	     this.fila = fila;
	     this.columna = columna;
	}
   
   public TipoI tipoInstruccion() {return TipoI.SWITCH;}
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
	public E getCondicion() {
		return condicion;
	}
	public List<Pair<E, List<I>>> getCases() {
		return cases;
	}
	public void setReferencia(SentenciaAbstracta ref) {
		referencia = ref;
	}
	public SentenciaAbstracta getReferencia() {
		return referencia;
	}
   
}