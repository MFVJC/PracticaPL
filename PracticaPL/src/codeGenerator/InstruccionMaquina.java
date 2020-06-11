package codeGenerator;

public class InstruccionMaquina {
	private InstruccionesMaquinaEnum tipoInstruccionMaquina;
	private String argumento;
	private boolean tieneSalto,haSaltado;
	private boolean estaCompleta;
	
	public InstruccionMaquina() {
		tieneSalto = false;
		haSaltado = false;
	}
	public InstruccionMaquina(InstruccionesMaquinaEnum tipoInstruccionMaquina) {
		this.tipoInstruccionMaquina = tipoInstruccionMaquina;
	}
	public InstruccionMaquina (InstruccionesMaquinaEnum tipoInstruccion, String argumento) {
		this.tipoInstruccionMaquina = tipoInstruccionMaquina;
		this.argumento = argumento;
	}
}
