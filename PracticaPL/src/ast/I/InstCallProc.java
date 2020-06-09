package ast.I;

import java.util.List;

import ast.E.E;
import ast.E.TipoE;

public class InstCallProc extends I {
	private E nombre_funcion;
	private List<E> argumentos;
	
	public InstCallProc(E nombre_funcion, List<E> argumentos) {
	       nombre_funcion = nombre_funcion;
	       argumentos = argumentos;
	}     
	
	public TipoI tipoInstruccion() {return TipoI.CALLPROC;}
	public String toString() {
		String aux = "{{_Call__}{" + nombre_funcion + "}{{_Args__}";
		for(E argumento : argumentos) aux += argumento.toString();		
		aux += "}}";
		return aux;
	}
	public E getNombre_funcion() {
		return nombre_funcion;
	}
	public List<E> getArgumentos() {
		return argumentos;
	}
}
