package codeGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ast.SentenciaAbstracta;
import ast.I.*;
import ast.T.EnumeradoTipoGeneral;
import ast.T.EnumeradoTipos;
import ast.T.Tipo;
import ast.T.TipoStruct;
import javafx.util.Pair;
import ast.E.*;

public class GeneradorCodigo {
	
	//Atributos
	
	private static File archivoSalida = new File("instruccionesMaquina.txt");
	
	private List<Bloque> listaBloques = new ArrayList<>();
	private Bloque bloqueActual = null;
	
	private int proximaDireccion;
	private int ambitoActual = 0;
	private int maxPila = 0;
	private int maxAmbitos = 0;
	
	//para cada intrucción guardo en cuanto afecta al tamaño de la pila
	private List<InstruccionMaquina> codigoGenerado = new ArrayList<>();
	private List<I> programa;

	
	//Metodos
	
	public GeneradorCodigo(List<I> programa) {
		this.programa = programa;
	}
	
	public void generaCodigo() {
		try {
			//Abrimos el archivo de salida
			BufferedWriter writer = new BufferedWriter(new FileWriter(archivoSalida));
			
			//Asignamos direcciones a todas las declaraciones
			generaDireccionesPrograma();
			int i = 0;
			for(Bloque bloque : listaBloques) {
				System.out.println("----------" + "BLOQUE " + i + "----------");
				System.out.println(bloque.toString());
				i++;
			}
			/*
			//Generamos el codigo del programa
			generaCodigoCuerpo(this.programa);
			//int tamPila = tamPilaEvaluacion(1);
			//codigo.get(1).setName(codigo.get(1).getName() + tamPila);
			//insertIns("stp", 0);
			
			//Escribimos el codigo generado en el archivo de salida
			int i = 0;
			for(InstruccionMaquina instruccion : codigoGenerado) {
				//Ellos generan tambien comentario en el codigo para poder leerlo facilmente
				//Quizas es buena idea, y cambiarlo en la version final
				writer.write("(" + i + ") " + instruccion.toString());
				i++;
			}
			//Cerramos el archivo de salida
			writer.close();
			System.out.println("Codigo generado sin errores");
			*/
		} catch (IOException e) {
			System.out.println("Error al generar el archivo de salida");
			e.printStackTrace();
		}
	}
	
	//1) GENERAR DIRECCIONES
	
	//Generamos las direcciones del programa
	private void generaDireccionesPrograma() {
		crearNuevoBloque(true);
		
		for(I instruccion : this.programa) {
			generaDireccionesInstruccion(instruccion);
		}
		
		guardarBloqueActual();
		
		//codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.SSP, ));
	}
	
	//Generamos las direcciones para una lista de instrucciones.
	private void generaDireccionesCuerpo(List<I> instrucciones) {
		crearNuevoBloque(false);
		
		for(I instruccion : instrucciones) {
			generaDireccionesInstruccion(instruccion);
		}
		
		guardarBloqueActual();
	}
	
	//Generamos las direcciones de las instrucciones que declaren algo
	private void generaDireccionesInstruccion(I instruccion) {
		switch(instruccion.tipoInstruccion()) {
		case IF:
			InstIf instruccionIf = (InstIf) instruccion;
			//Direcciones de todas las declaraciones de las instrucciones de la rama if
			generaDireccionesCuerpo(instruccionIf.getCuerpoIf());
			
			//Si tiene rama else, direcciones de todas sus declaraciones
			if(instruccionIf.getCuerpoElse() != null) generaDireccionesCuerpo(instruccionIf.getCuerpoElse());
			break;
		
		case WHILE:
			InstWhile instruccionWhile = (InstWhile) instruccion;
			generaDireccionesCuerpo(instruccionWhile.getCuerpo());
			break;
		
		case SWITCH:
			InstSwitch instruccionSwitch = (InstSwitch) instruccion;
			
			//Creamos una lista cuerpoSwitch con el cuerpo de todos los cases, al fin y al cabo
			//las expresiones y la condicion del switch nos da igual, solo buscamos declaraciones!
			List<I> cuerpoSwitch = new ArrayList<I>();
			for(Pair<E, List<I>> ccase : instruccionSwitch.getCases()) {
				cuerpoSwitch.addAll(ccase.getValue());
			}
			generaDireccionesCuerpo(cuerpoSwitch);
			break;
			
		case DECL:
			InstDeclaracion instruccionDeclaracion = (InstDeclaracion) instruccion;
			
			//Diferenciamos dependiendo de el tipo de la declaracion
			switch(instruccionDeclaracion.getTipo().tipoEnumerado()) {
			case INT: case BOOLEAN:
				//Por el asin, siempre es un iden
				String iden = ((Iden) instruccionDeclaracion.getIden()).getNombre();
				insertaIdentificadorBloqueActual(iden, 1);
				break;
			case STRUCT:
				String idenStruct = ((Iden) instruccionDeclaracion.getIden()).getNombre();
				int tamano = bloqueActual.getTipo(idenStruct);
				insertaIdentificadorBloqueActual(idenStruct, tamano);
				break;
			case PUNTERO:
				
				break;
			case ARRAY:
				
				break;		
			default:
				
				break;
			}
			
			break;
			
		case STRUCT:
			InstStruct instruccionStruct = (InstStruct) instruccion;
			String nombreStruct = ((Iden) instruccionStruct.getIden()).getNombre();
			int tamanoStruct = calcularTamanoStruct(instruccionStruct);
			bloqueActual.insertaTipo(nombreStruct, tamanoStruct);
			break;
			
		case DECLFUN:
			//Abrimos ambito
			crearNuevoBloque(true);
			
			InstDeclFun instruccionDeclFun = (InstDeclFun) instruccion;
			instruccionDeclFun.setProfundidadAnidamiento(bloqueActual.getProfundidadAnidamiento());
			
			//Asignamos direccion a cada parametro
			for(Pair<Tipo, E> argumento : instruccionDeclFun.getArgs()) {
				//Transformamos cada argumento en una declaracion (REVISAR)
				InstDeclaracion argumentoDeclaracion = new InstDeclaracion(false, argumento.getKey(), argumento.getValue(), null);	
				generaDireccionesInstruccion(argumentoDeclaracion);
			}
			
			//Asignamos direccion a cada instruccion del cuerpo (no llamamos a 
			//la funcion generaDireccionesCuerpo porque generariamos otro ambito)
			for(I instr : instruccionDeclFun.getCuerpo()) {
				generaDireccionesInstruccion(instr);
			}
			
			//Cerramos ambito
			guardarBloqueActual();
			
			break;
		
		default:
			
			break;
		}
	}
	
	

	//2) GENERAR CODIGO
	
	//Genera codigo para una lista de instrucciones. Usado para generar el codigo del programa completo,
	//pero tambien usado para generar listas de instrucciones del cuerpo de un if o de una funcion
	private void generaCodigoCuerpo(List<I> instrucciones) {
		for(I instruccion : instrucciones) {
			//Generamos codigo para la instruccion que toca
			//Aqui ellos hacen diferencia entre declaracion funcion y otras...
			//Creo que no hace falta esta diferencia, pues lo hacemos en el switch de despues.
			generaCodigoInstruccion(instruccion);
		}
	}
	
	//Genera el codigo para una instruccion concreta
	private void generaCodigoInstruccion (I instruccion) {
		switch(instruccion.tipoInstruccion()) {
			case ASIG:
				InstAsignacion instruccionAsignacion = (InstAsignacion) instruccion;
				//voy a ver el caso de vectores en otra funcion
				generaCodigoLeft(instruccionAsignacion.getIden());
				generaCodigoExpresion(instruccionAsignacion.getValor());
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.STO,-2));
				break;
			case CALLPROC:
				
				break;
			case DECL:
				
				break;
			case DECLFUN:
				
				break;
			case IF:
				InstIf instruccionIf = (InstIf) instruccion;
				generaCodigoExpresion(instruccionIf.getCondicion());
				
				maxAmbitos++;
				ambitoActual = maxAmbitos;
				
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.FJP, -1));
				generaCodigoCuerpo(instruccionIf.getCuerpoIf());
				
				if(instruccionIf.getCuerpoElse() != null) {
					maxAmbitos++;
					ambitoActual = maxAmbitos;
					
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.UJP, 0));
				}
				
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

	//Genera el codigo para una expresion concreta
	private void generaCodigoExpresion(E expresion) {
		if(expresion.tipoSentencia() == EnumeradoTipoGeneral.EXPRESION_BINARIA) {
			EBin expresionBinaria = (EBin)expresion;
			generaCodigoExpresion(expresionBinaria.opnd1());
			generaCodigoExpresion(expresionBinaria.opnd2());
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
		else {
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
					LlamadaFuncion llamada = (LlamadaFuncion) expresion;
					InstDeclFun declaracionFuncion =(InstDeclFun)llamada.getReferencia();
					break;
				case IDEN:
					Iden identificador = (Iden) expresion;
					if(identificador.getTipo().tipoEnumerado() == EnumeradoTipos.STRUCT) {
						//entonces hay que generar código para cargar los atributos
						InstStruct referenciaIdentificador = (InstStruct)identificador.getReferencia();
						for(I instruccion: referenciaIdentificador.getDeclaraciones()) {
							InstDeclaracion declaracionAtributo = (InstDeclaracion) instruccion;
							Iden identificadorCampo = new Iden(identificador.getNombre() + "." + ((Iden)declaracionAtributo.getIden()).getNombre());
							identificadorCampo.setTipo(declaracionAtributo.getTipo());
							generaCodigoIdentificador(identificadorCampo);
							codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.IND,0));
						}
					}
					else {
						generaCodigoLeft(identificador);
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.IND,0));
					}
					break;
				case NEW:
					New nuevo = (New) expresion;
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,1,((Num)nuevo.getTam()).num()));
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.NEW,-2));
					break;
				case NOT:
					Not not = (Not)expresion;
					generaCodigoExpresion(not.opnd1());
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.NOT,0));
					
					break;
				case NUM:
					Num numero = (Num) expresion;
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,1,numero.num()));
					break;
				case SQUAREBRACKET:
					generaCodigoLeft(expresion);
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.IND,0));
					break;
				default:
					break;
				
			}	
		}
	}
	
	//Genera el codigo para un vector
	private void generaCodigoVector() {
		
	}
	
	//Genera el codigo para un identificador
	private void generaCodigoIdentificador(E expresion) {
		if(expresion.tipoExpresion() == TipoE.IDEN) {
			Iden identificador = (Iden) expresion;
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDA,1,"0 " + getBloqueNivelActual().getDireccionIdentificador(identificador.getNombre())));
		}
	}
	
	//Genera el codigo para la parte izquierda de una asignación
	private void generaCodigoLeft(E expresion) {
		switch(expresion.tipoExpresion()) {
			case IDEN:
				/*insertIns("lda " + 0 + " " + bloqueActGenera().dirVar(((Iden) exp).id()), 1);
				Iden iden = (Iden) expresion;
				InstDeclaracion declaracionIden = (InstDeclaracion)iden.getReferencia();
				Iden referenciaIden =(Iden)declaracionIden.getIden();
				//tenemos que comprobar que no tenemos ninguna referencia a un iden que no sea instrucción
				int direccionRelativa= getBloqueNivelActual().getDireccionIdentificador(iden.getNombre());
				//hay que ver si tenemos un vector o no creo
				//codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,"0"));
<<<<<<< HEAD
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDA,Integer.toString(getBloqueNivelActual().getProfundidadAnidamiento() - referenciaIden.getPa() +1),Integer.toString(direccionRelativa)));
			*/
				break;

			case SQUAREBRACKET:
				SquareBracket accesoVector = (SquareBracket) expresion;
				
				break;
			case DOT:
				break;
			case DOLLAR:
				break;
			default:
			break;
		
		}
	}
	
	
	//3) FUNCIONES AUXILIARES PARA EL MANEJO DE BLOQUES Y AMBITOS
	
	private Bloque getBloqueNivelActual() {
		return listaBloques.get(ambitoActual); //Esto no deberia ser bloqueActual?
	}
	
	private void insertaIdentificadorBloqueActual(String nombreIdentificador, int tamanoIdentificador) {
		this.bloqueActual.insertaIdentificador(nombreIdentificador, tamanoIdentificador);
		this.proximaDireccion += tamanoIdentificador;
	}
	
	private void insertaTipoBloqueActual(String nombreTipo, int tamanoTipo) {
		this.bloqueActual.insertaTipo(nombreTipo, tamanoTipo);
	}
	
	private void crearNuevoBloque(boolean ambitoFuncion) {
		Bloque bloque = new Bloque(bloqueActual, listaBloques.size(), ambitoFuncion);
		listaBloques.add(bloque);
		bloqueActual = bloque;
	}
	
	private void guardarBloqueActual() {
		proximaDireccion = proximaDireccion - bloqueActual.getTamanoBloque();
		bloqueActual = bloqueActual.getBloquePadre();
	}
	
	private int calcularTamanoStruct(InstStruct struct) {
		int tamanoStruct = 0;
		for(I declaracion : struct.getDeclaraciones()) { //Para cada declaracion del struct, sumamos su tamano
			InstDeclaracion declaracionStruct = (InstDeclaracion) declaracion;
			switch(declaracionStruct.getTipo().tipoEnumerado()) {
			case INT: case BOOLEAN:
				//Sumamos 1 (lo que ocupan int y boolean)
				tamanoStruct++;
				break;
			case STRUCT:
				//Sumamos el tamano del tipo struct, que lo tendremos almacenado en un bloque previo
				TipoStruct tipoStruct = (TipoStruct) declaracionStruct.getTipo();
				tamanoStruct += bloqueActual.getTipo(tipoStruct.getNombreStruct());
				break;
			case PUNTERO:
				
				break;
			case ARRAY:
				
				break;		
			default:
				
				break;
			}
		}
		return tamanoStruct;
	}
	
}