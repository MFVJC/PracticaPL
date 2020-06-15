package ast.T;

import ast.SentenciaAbstracta;

public class TipoStruct extends Tipo{
	private String nombreStruct;
	private SentenciaAbstracta referenciaDeclaracion;
	private int fila;
	private int columna;
	public TipoStruct(String nombreStruct,int fila,int columna) {
		this.nombreStruct = nombreStruct;
	     this.fila = fila;
	     this.columna = columna;
	}
	public String getNombreStruct() {
		return nombreStruct;
	}
	@Override
	public EnumeradoTipos tipoEnumerado() {
		return EnumeradoTipos.STRUCT;
	}
	public void setReferencia(SentenciaAbstracta referenciaDeclaracion) {
		this.referenciaDeclaracion = referenciaDeclaracion;
	}
	public SentenciaAbstracta getReferencia() {
		return referenciaDeclaracion;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return nombreStruct;
	}
}
