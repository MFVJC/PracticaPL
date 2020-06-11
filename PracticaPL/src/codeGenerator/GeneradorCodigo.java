package codeGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ast.SentenciaAbstracta;
import ast.I.*;
import ast.T.EnumeradoTipos;
import javafx.util.Pair;
import ast.E.*;

public class GeneradorCodigo {
	private static File outputFile = new File("instruccionesMaquina.txt");
	private List<Bloque> listaBloques = new ArrayList<>();
	
	private Bloque bloqueActual = null;
	private int ambitoActual = 0;
	private int maxPila = 0;
	private int maxAmbitos = 0;
	
	//para cada intrucción guardo en cuanto afecta al tamaño de la pila
	private List<InstruccionMaquina> codigoGenerado = new ArrayList<>();
	
	private void generaCodigoSentencia(SentenciaAbstracta sentencia) {
		switch(sentencia.tipoSentencia()) {
		case INSTRUCCION:
			I instruccion = (I) sentencia;
			switch(instruccion.tipoInstruccion()) {
			case ASIG:
				break;
			case CALLPROC:
				break;
			case DECL:
				break;
			case DECLFUN:
				break;
			case IF:
				break;
			case STRUCT:
				break;
			case SWITCH:
				break;
			case WHILE:
				break;
			default:
				break;
			
			}
			
			break;
		case EXPRESION:
			break;
		case EXPRESION_BINARIA:
			break;

		case TIPOS:
			break;
		default:
			break;
		
		}
	}
	
	public void codeExpresiones(E expresion) {
		
	}
	
	//genera código para la parte izquierda de una asignación
	public void codeL(E expresion) {
		switch(expresion.tipoExpresion()) {
			case IDEN:
				Iden iden = (Iden) expresion;
				InstDeclaracion declaracionIden = (InstDeclaracion)iden.getReferencia();
				//tenemos que comprobar que no tenemos ninguna referencia a un iden que no sea instrucción
				int direccionMemoria = getBloqueNivelActual().getDireccionIdentificador(iden.getNombre());
				//hay que ver si tenemos un vector o no creo
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,"0"));
				if(iden.getTipo().tipoEnumerado() == EnumeradoTipos.ARRAY) {
					
				}else {
					
				}
				
			break;
			case SQUAREBRACKET:
			break;
			case DOT:
			break;
			default:
			break;
		
		}
	}
	
	public void codeInstrucciones (I instruccion) {
		switch(instruccion.tipoInstruccion()) {
		case ASIG:
			InstAsignacion instruccionAsignacion = (InstAsignacion) instruccion;
			codeL(instruccionAsignacion.getIden());
			codeExpresiones(instruccionAsignacion.getValor());
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.STO,"-2"));
			break;
		case CALLPROC:
			break;
		case DECL:
			break;
		case DECLFUN:
			break;
		case IF:
			break;
		case STRUCT:
			break;
		case SWITCH:
			break;
		case WHILE:
			break;
		default:
			break;
		
		}
	}
	
	private Bloque getBloqueNivelActual() {
		return listaBloques.get(ambitoActual);
	}
	
}