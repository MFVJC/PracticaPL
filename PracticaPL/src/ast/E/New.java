package ast.E;

import ast.T.Tipo;

public class New extends E{
	private Tipo tipo;
	//ellos lo tienen en una lista supongo por si es una lista de objetos propios
	private E tam;
	private int fila;
	private int columna;
	public New(Tipo tipo, E tam,int fila,int columna) {
		this.tipo = tipo;
		this.tam = tam;
	     this.fila = fila;
	     this.columna = columna;
	}
	public Tipo getTipo() {
		return tipo;
	}
	@Override
	public TipoE tipoExpresion() {
		return TipoE.NEW;
	}
	
	public E getTam() {
		return tam;
	}

	public String toString() {
		if(tam != null) return "{{__New__}{" + tipo.toString() + "[" + tam.toString().substring(1, tam.toString().length()-1) + "]}}";
		else return "{{__New__}{" + tipo.toString() + "}}";
	}
}
