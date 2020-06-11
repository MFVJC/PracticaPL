package ast.E;

import ast.T.Tipo;

public class New extends E{
	private Tipo tipo;
	//ellos lo tienen en una lista supongo por si es una lista de objetos propios
	private int tam;
	
	public New(Tipo tipo) {
		this.tipo = tipo;
	}
	public Tipo getTipo() {
		return tipo;
	}
	@Override
	public TipoE tipoExpresion() {
		return TipoE.NEW;
	}
	public void setTam() {
		this.tam = tam;
	}
	public int getTam() {
		return tam;
	}
	public String toString() {return "{{__New__}{" + tipo.toString() + "}}";}
}
