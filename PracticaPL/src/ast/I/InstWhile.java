package ast.I;

import java.util.List;

import ast.E.E;

public class InstWhile extends I {
	
	private E condicion;
	private List<I> cuerpo;
	private int fila;
	private int columna;
	public InstWhile(E condicion, List<I> cuerpo,int fila,int columna) {
			this.condicion = condicion;
			this.cuerpo = cuerpo;
		    this.fila = fila;
		    this.columna = columna;
	}
	 
	public TipoI tipoInstruccion() {return TipoI.WHILE;}
	public String toString() {
		   String aux = "{{_While_}{{_Cond__}" + condicion + "}{{_Cuer__}";
		   
		   for(I ins : cuerpo) aux += ins.toString();
		   aux += "}}";
		   
		   return aux;
	 }

	public E getCondicion() {
		return condicion;
	}

	public List<I> getCuerpo() {
		return cuerpo;
	}
	
}