package ast.T;

import ast.SentenciaAbstracta;

public abstract class Tipo extends SentenciaAbstracta{
	public TipoGeneral tipoSentencia() {
		return TipoGeneral.TIPOS;
	}
	public abstract EnumeradoTipos tipoEnumerado();
}
