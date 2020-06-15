package ast.I;

import ast.E.E;

public class InstAsignacion extends I {
	private E identificador;
	private E valor;

	public InstAsignacion(E iden, E valor,int fila,int columna) {
	    this.identificador = iden;
		this.valor = valor;
	     this.fila = fila;
	     this.columna = columna;
	}
   
   public TipoI tipoInstruccion() {return TipoI.ASIG;}
   
   public String toString() {
	   String aux = "{{_Asig__}" + identificador.toString() + valor.toString() + "}";
	   
	   return aux;
   }
   public E getIdentificador() {
		return identificador;
	}

	public E getValor() {
		return valor;
	}
}
