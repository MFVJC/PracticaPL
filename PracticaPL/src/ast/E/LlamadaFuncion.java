package ast.E;

import java.util.List;

import ast.E.TipoE;
import ast.I.I;

public class LlamadaFuncion extends E {
	private String nombre_funcion;
	private List<E> argumentos;
	
	public LlamadaFuncion(String iden, List<E> args) {
	       nombre_funcion = iden;
	       argumentos = args;
	}     
	public TipoE tipo() {return TipoE.FUNCION;}
	public String toString() {
		String aux = "{{_Call__}{" + nombre_funcion + "}{{_Args__}";
		for(E argumento : argumentos) aux += argumento.toString();		
		aux += "}}";
		return aux;
	}
}