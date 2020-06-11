package codeGenerator;

public class InstruccionMaquina {
	private InstruccionesMaquinaEnum tipoInstruccionMaquina;
	private int cambioPila = 0;
	private String primerArgumento;
	private String segundoArgumento;
	private boolean tieneSalto,haSaltado;
	private boolean estaCompleta;
	
	public InstruccionMaquina() {
		tieneSalto = false;
		haSaltado = false;
	}
	
	public InstruccionMaquina(InstruccionesMaquinaEnum tipoInstruccionMaquina) {
		this.tipoInstruccionMaquina = tipoInstruccionMaquina;
	}
	public InstruccionMaquina(InstruccionesMaquinaEnum tipoInstruccionMaquina,int cambioPila) {
		this.tipoInstruccionMaquina = tipoInstruccionMaquina;
		this.cambioPila = cambioPila;
	}
	public InstruccionMaquina(InstruccionesMaquinaEnum tipoInstruccionMaquina,int cambioPila,String primerArgumento) {
		this.tipoInstruccionMaquina = tipoInstruccionMaquina;
		this.cambioPila = cambioPila;
		this.primerArgumento = primerArgumento;
	}
	public InstruccionMaquina (InstruccionesMaquinaEnum tipoInstruccion, String argumento) {
		this.tipoInstruccionMaquina = tipoInstruccionMaquina;
		this.primerArgumento = argumento;
	}
	public InstruccionMaquina (InstruccionesMaquinaEnum tipoInstruccion, String primerArgumento,String segundoArgumento) {
		this.tipoInstruccionMaquina = tipoInstruccionMaquina;
		this.primerArgumento = primerArgumento;
		this.segundoArgumento = segundoArgumento;
	}
}
