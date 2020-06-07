package ast.I;

import ast.E.E;

public class InstAsignacion extends I {
	private E iden;
	private E valor;
	

	public InstAsignacion(E iden, E valor) {
	    this.iden = iden;
		this.valor = valor;
	}
   
   public TipoI tipo() {return TipoI.ASIG;}
   
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
