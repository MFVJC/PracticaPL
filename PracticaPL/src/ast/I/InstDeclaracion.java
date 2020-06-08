package ast.I;

import java.util.ArrayList;
import java.util.List;

import ast.E.E;
import ast.T.Tipo;
import ast.E.*;

public class InstDeclaracion extends I {
	private boolean constant;
	//este tipo debería ser de la clase Tipo
	private Tipo tipoVariable;
	private E iden;
	private List<E> tam;
	private List<E> valor;
	
   public InstDeclaracion(boolean constant, Tipo tipo, E iden, List<E> tam, List<E> valor) {
	    this.constant = constant;
		this.tipoVariable = tipo;
	    this.iden = iden;
	    this.tam = tam;
	    this.valor = valor;
	}
   
   public TipoI tipoInstruccion() {return TipoI.DECL;}
   
   public String toString() {
	   String aux = "{{_Decl__}";
	   if(constant) aux += "{Const}";
	   aux += "{" + tipoVariable.toString() + "}" + iden.toString();
	   
	   if(tam.isEmpty()) { //Es una variable simple
		   if(valor != null) { //Esta inicializada
			   aux += valor.toString();
		   }
	   }
	   else { //Es una variable vector   
		   aux += "{{__Tam__}" + tam.toString() + "}";
		   if(valor != null) { //Esta inicializada
			   aux += "{{__Ini__}";
			   for(E v : valor) {
				   aux += v.toString();
			   }
			   aux += "}";
		   }
	   }
	   aux += "}";
	   return aux;
   }
	public boolean isConstant() {
		return constant;
	}
	
	public Tipo getTipo() {
		return tipoVariable;
	}
	
	public E getIden() {
		return iden;
	}
	
	public List<E> getTam() {
		return tam;
	}
	
	public List<E> getValor() {
		return valor;
	}

}
