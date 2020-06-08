package ast.T;

import ast.SentenciaAbstracta;

public abstract class Tipo extends SentenciaAbstracta{
	public EnumeradoTipoGeneral tipoSentencia() {
		return EnumeradoTipoGeneral.TIPOS;
	}
	public abstract EnumeradoTipos tipoEnumerado();
}
