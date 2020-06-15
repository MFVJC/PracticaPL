package ast;

import ast.T.EnumeradoTipoGeneral;

public abstract class SentenciaAbstracta {
	protected int fila;
	protected int columna;
	public abstract EnumeradoTipoGeneral tipoSentencia();
	public int getFila() {
		return fila;
	}
	public int getColumna() {
		return columna;
	}
	
}