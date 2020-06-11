package ast.E;

import java.util.List;

import ast.SentenciaAbstracta;
import ast.E.TipoE;
import ast.I.I;
import ast.T.Tipo;

public class LlamadaFuncion extends E {
	private Tipo tipoReturn;
	private E nombreFuncion;
	private List<E> argumentos;
	private SentenciaAbstracta referenciaDeclaracion;
	
	public LlamadaFuncion(E nombreFuncion, List<E> argumentos) {
		   this.nombreFuncion = nombreFuncion;
	       this.argumentos = argumentos;
	}     
	public TipoE tipoExpresion() {return TipoE.FUNCION;}
	public String toString() {
		String aux = "{{_Call__}{" + nombreFuncion + "}{{_Args__}";
		for(E argumento : argumentos) aux += argumento.toString();		
		aux += "}}";
		return aux;
	}
	public E getNombreFuncion() {
		return nombreFuncion;
	}
	public List<E> getArgumentos() {
		return argumentos;
	}
	public SentenciaAbstracta getReferencia() {
		return referenciaDeclaracion;
	}
	public void setReferencia(SentenciaAbstracta referenciaDeclaracion) {
		this.referenciaDeclaracion = referenciaDeclaracion;
	}
	public Tipo getTipoReturn() {
		return tipoReturn;
	}
	public void setTipoReturn(Tipo tipoReturn) {
		this.tipoReturn = tipoReturn;
	}
	
	
}