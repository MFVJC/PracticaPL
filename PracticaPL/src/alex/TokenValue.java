package alex;

public class TokenValue {
	private String lexema;
	private int fila;
	private int columna;
	
	public TokenValue(String lexema,int fila,int columna) {
		this.lexema = lexema;
		this.fila = fila;
		this.columna = columna;
	}

	public String getLexema() {
		return lexema;
	}

	public void setLexema(String lexema) {
		this.lexema = lexema;
	}

	public int getFila() {
		return fila;
	}

	public void setFila(int fila) {
		this.fila = fila;
	}

	public int getColumna() {
		return columna;
	}

	public void setColumna(int columna) {
		this.columna = columna;
	}
	
}
