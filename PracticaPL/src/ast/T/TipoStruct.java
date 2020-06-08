package ast.T;

import ast.SentenciaAbstracta;

public class TipoStruct extends Tipo{
	private String nombreStruct;
	private SentenciaAbstracta referenciaDeclaracion;
	public TipoStruct(String nombreStruct) {
		this.nombreStruct = nombreStruct;
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
}
