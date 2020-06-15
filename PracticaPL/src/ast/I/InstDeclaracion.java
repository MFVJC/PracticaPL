package ast.I;

import java.util.List;
import ast.E.E;
import ast.T.Tipo;

public class InstDeclaracion extends I {
	private boolean constant;
	//este tipo debería ser de la clase Tipo
	private Tipo tipoVariable;
	private E iden;
	private List<E> valor;
	private int fila;
	private int columna;
   public InstDeclaracion(boolean constant, Tipo tipo, E iden, List<E> valor,int fila,int columna) {
	    this.constant = constant;
		this.tipoVariable = tipo;
	    this.iden = iden;
	    this.valor = valor;
	     this.fila = fila;
	     this.columna = columna;
	}
   
   public TipoI tipoInstruccion() {return TipoI.DECL;}
   
    public String toString() {
	   String aux = "{{_Decl__}";
	   if(constant) aux += "{Const}";
	   aux += "{" + tipoVariable.toString() + "}" + iden.toString();
	   
	   if(valor != null) { //Esta inicializada
		   aux += "{{__Ini__}";
		   for(E v : valor) {
			   aux += v.toString();
		   }
		   aux += "}";
	   }
	   
	   aux += "}";
	   return aux;
    }
   
	public boolean isConstant() {
		return constant;
	}
	public void setConstant(boolean constant) { //función para generación de código por si intentan declarar un struct
		this.constant  = constant;
	}
	public Tipo getTipo() {
		return tipoVariable;
	}
	
	public E getIden() {
		return iden;
	}
	
	public List<E> getValor() {
		return valor;
	}
	public void setValor(List<E> valores) {
		this.valor = valores;
	}

}
