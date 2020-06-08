package ast.E;

import java.util.List;

import ast.SentenciaAbstracta;
import ast.E.TipoE;
import ast.I.I;
import ast.T.Tipo;

public class LlamadaFuncion extends E {
	private Tipo tipoReturn;
	private String nombre_funcion;
	private List<E> argumentos;
	private SentenciaAbstracta referenciaDeclaracion;
	
	public LlamadaFuncion(String iden, List<E> args) {
	       nombre_funcion = iden;
	       argumentos = args;
	}     
	public TipoE tipoExpresion() {return TipoE.FUNCION;}
	public String toString() {
		String aux = "{{_Call__}{" + nombre_funcion + "}{{_Args__}";
		for(E argumento : argumentos) aux += argumento.toString();		
		aux += "}}";
		return aux;
	}
	public String getNombre_funcion() {
		return nombre_funcion;
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