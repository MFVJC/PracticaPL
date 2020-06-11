package codeGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ast.SentenciaAbstracta;
import ast.I.*;
import ast.T.EnumeradoTipoGeneral;
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
		if(expresion.tipoSentencia() == EnumeradoTipoGeneral.EXPRESION_BINARIA) {
			EBin expresionBinaria = (EBin)expresion;
			codeExpresiones(expresionBinaria.opnd1());
			codeExpresiones(expresionBinaria.opnd2());
			switch(expresionBinaria.tipoExpresion()) {
			case AND:
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.AND,-1));
				break;
			case DIV:
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.DIV,-1));
				break;
			case ELEV:
				//no está en la máquina-P
				break;
			case EQUAL:
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.EQU,-1));
				break;
			case GREATEREQUAL:
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.GEQ,-1));
				break;
			case GREATERTHAN:
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.GRT,-1));
				break;
			case LESSEQUAL:
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LEQ,-1));
				break;
			case LESSTHAN:
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LES,-1));
				break;
			case MUL:
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.MUL,-1));
				break;
			case NOTEQUAL:
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.NEQ,-1));
				break;
			case OR:
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.OR,-1));
				break;
			case RESTA:
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.SUB,-1));
				break;
			case SUMA:
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.ADD,-1));
				break;
			default:
				break;
			
			}
		}
		switch(expresion.tipoExpresion()) {
		case BASICFALSE:
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,1,"false"));
			break;
		case BASICTRUE:
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,1,"true"));
			break;
		case DOLLAR:
			//hay que generar el código del array
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.IND,0));
			break;
		case DOT:
			//generar código para el struct de la izquierda
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.IND,0));
			break;
		case FUNCION:
			break;
		case IDEN:
			break;
		case NEW:
			New nuevo = (New) expresion;
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,0,Integer.toString(nuevo.getTam())));

			break;
		case NOT:
			Not not = (Not)expresion;
			codeExpresiones(not.opnd1());
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.NOT,0));
			
			break;
		case NUM:
			Num numero = (Num) expresion;
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,1,numero.num()));
			break;
		case SQUAREBRACKET:
			break;
		default:
			break;
		
		}
	}
	public void codeVector() {
		
	}
	//genera código para la parte izquierda de una asignación
	public void codeL(E expresion) {
		switch(expresion.tipoExpresion()) {
			case IDEN:
				Iden iden = (Iden) expresion;
				InstDeclaracion declaracionIden = (InstDeclaracion)iden.getReferencia();
				Iden referenciaIden =(Iden)declaracionIden.getIden();
				//tenemos que comprobar que no tenemos ninguna referencia a un iden que no sea instrucción
				int direccionRelativa= getBloqueNivelActual().getDireccionIdentificador(iden.getNombre());
				//hay que ver si tenemos un vector o no creo
				//codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,"0"));
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDA,Integer.toString(getBloqueNivelActual().getPa() - referenciaIden.getPa() +1),Integer.toString(direccionRelativa)));
			break;
			case SQUAREBRACKET:
				SquareBracket accesoVector = (SquareBracket) expresion;
				
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
			//voy a ver el caso de vectores en otra función
			codeL(instruccionAsignacion.getIden());
			codeExpresiones(instruccionAsignacion.getValor());
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.STO,-2));
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