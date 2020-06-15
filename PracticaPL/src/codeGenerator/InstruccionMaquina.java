package codeGenerator;

public class InstruccionMaquina {
	private InstruccionesMaquinaEnum tipoInstruccionMaquina;
	private int cambioPila = 0;
	private String primerArgumento;
	private String segundoArgumento;
	private boolean tieneSalto;
	private boolean haSaltado;
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
	public InstruccionMaquina(InstruccionesMaquinaEnum tipoInstruccionMaquina,int cambioPila,String primerArgumento,String segundoArgumento) {
		this.tipoInstruccionMaquina = tipoInstruccionMaquina;
		this.cambioPila = cambioPila;
		this.primerArgumento = primerArgumento;
		this.segundoArgumento = segundoArgumento;
	}

	public InstruccionMaquina (InstruccionesMaquinaEnum tipoInstruccionMaquina, String primerArgumento) {
		this.tipoInstruccionMaquina = tipoInstruccionMaquina;
		this.primerArgumento = primerArgumento;
	}
	
	public InstruccionMaquina (InstruccionesMaquinaEnum tipoInstruccion, String primerArgumento,String segundoArgumento) {
		this.tipoInstruccionMaquina = tipoInstruccionMaquina;
		this.primerArgumento = primerArgumento;
		this.segundoArgumento = segundoArgumento;
	}
	public void setArgumento1(String argumento1) {
		primerArgumento=argumento1;
	}
	public void setArgumento2(String argumento2) {
		primerArgumento=argumento2;
	}
	public InstruccionesMaquinaEnum getTipoInstruccion() {
		return tipoInstruccionMaquina;
	}
	public int getCambioPila() {
		return cambioPila;
	}
	@Override
	public String toString() {
		String aux = tipoInstruccionMaquina.toString() + " ";
		
		if(primerArgumento != null) {
			if(segundoArgumento != null) { //Dos argumentos
				aux += primerArgumento + " " + segundoArgumento;
			} else { //Un argumento
				aux += primerArgumento;
			}
		}
		else { //Sin argumentos -> Imprimimos cambioPila
			aux += Integer.toString(cambioPila);
		}
		aux += ";\n";
		return aux;
	}
}
