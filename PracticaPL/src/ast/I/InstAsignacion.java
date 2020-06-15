package ast.I;

import ast.E.E;

public class InstAsignacion extends I {
	private E iden;
	private E valor;

	public InstAsignacion(E iden, E valor,int fila,int columna) {
	    this.iden = iden;
		this.valor = valor;
	     this.fila = fila;
	     this.columna = columna;
	}
   
   public TipoI tipoInstruccion() {return TipoI.ASIG;}
   
   public String toString() {
	   String aux = "{{_Asig__}" + iden.toString() + valor.toString() + "}";
	   
	   return aux;
   }
   public E getIden() {
		return iden;
	}

	public E getValor() {
		return valor;
	}
}
