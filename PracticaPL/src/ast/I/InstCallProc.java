package ast.I;

import java.util.List;

import ast.SentenciaAbstracta;
import ast.E.E;
import ast.E.TipoE;

public class InstCallProc extends I {
	private E nombre_funcion;
	private List<E> argumentos;
	private SentenciaAbstracta referencia;
	public InstCallProc(E nombre_funcion, List<E> argumentos,int fila,int columna) {
	       nombre_funcion = nombre_funcion;
	       argumentos = argumentos;
		     this.fila = fila;
		     this.columna = columna;
	}     
	
	public TipoI tipoInstruccion() {return TipoI.CALLPROC;}
	public String toString() {
		String aux = "{{_Call__}{" + nombre_funcion + "}{{_Args__}";
		if(argumentos!=null)for(E argumento : argumentos) aux += argumento.toString();		
		aux += "}}";
		return aux;
	}
	public E getNombre_funcion() {
		return nombre_funcion;
	}
	public List<E> getArgumentos() {
		return argumentos;
	}
	public void setReferencia(SentenciaAbstracta referencia) {
		this.referencia = referencia;
	}
	public SentenciaAbstracta getReferencia() {
		return referencia;
	}
	
}
