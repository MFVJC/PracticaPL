package codeGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.SentenciaAbstracta;
import ast.I.*;
import ast.T.EnumeradoTipoGeneral;
import ast.T.EnumeradoTipos;
import ast.T.Tipo;
import ast.T.TipoArray;
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
	
	//para cada intrucci�n guardo en cuanto afecta al tama�o de la pila
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
			String idenDeclaracion = ((Iden) instruccionDeclaracion.getIden()).getNombre();
			
			//Diferenciamos dependiendo de el tipo de la declaracion
			switch(instruccionDeclaracion.getTipo().tipoEnumerado()) {
			case INT: case BOOLEAN:
				//Por el asin, siempre es un iden
				insertaIdentificadorBloqueActual(idenDeclaracion, 1);
				break;
			case STRUCT:
				//Cuando declaramos una instancia de un tipo struct, guardamos en la lista de identificadores su nombre y con su tamano
				String tipoStruct = ((TipoStruct) instruccionDeclaracion.getTipo()).getNombreStruct();
				int tamano = bloqueActual.getTamanoTipo(tipoStruct);
				
				insertaIdentificadorBloqueActual(idenDeclaracion, tamano);
				break;
			case PUNTERO:
				//Cuando declaramos un puntero, guardamos su identificador de tamano 1 y gestionamos la memoria dinamica
				insertaIdentificadorBloqueActual(idenDeclaracion, 1);
				
				//PENDIENTE: Gestionar memoria dinamica!
				
				break;
			case ARRAY:
				//Cuando declaramos un array, tenemos que almacenar su identificador con el tamano y su lista de dimensiones
				Pair<Integer, List<Integer>> informacionArray = obtenerInformacionArray(instruccionDeclaracion);
				int tamanoArray = informacionArray.getKey();
				List<Integer> dimensionesArray = informacionArray.getValue();
				
				insertaIdentificadorBloqueActual(idenDeclaracion, tamanoArray);
				bloqueActual.insertaDimensionesArray(idenDeclaracion, dimensionesArray);
				break;		
			default:
				
				break;
			}
			
			break;
			
		case STRUCT:
			//Cuando declaramos un nuevo tipo struct debemos meter su nombre con su tamano en la lista de tipos y guardar las direcciones relativas de los campos
			InstStruct instruccionStruct = (InstStruct) instruccion;
			String nombreStruct = ((Iden) instruccionStruct.getIden()).getNombre();
			Pair<Integer, Map<String, Integer>> informacionStruct = obtenerInformacionStruct(instruccionStruct);
			int tamanoStruct = informacionStruct.getKey();
			Map<String, Integer> tamanoCamposStruct = informacionStruct.getValue();
			
			bloqueActual.insertaTamanoTipo(nombreStruct, tamanoStruct);
			bloqueActual.insertaCamposStruct(nombreStruct, tamanoCamposStruct);
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
					//no est� en la m�quina-P
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
					//hay que generar el c�digo del array
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.IND,0));
					break;
				case DOT:
					//generar c�digo para el struct de la izquierda
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.IND,0));
					break;
				case FUNCION:
					LlamadaFuncion llamada = (LlamadaFuncion) expresion;
					InstDeclFun declaracionFuncion =(InstDeclFun)llamada.getReferencia();
					break;
				case IDEN:
					Iden identificador = (Iden) expresion;
					if(identificador.getTipo().tipoEnumerado() == EnumeradoTipos.STRUCT) {
						//entonces hay que generar c�digo para cargar los atributos
						InstStruct referenciaIdentificador = (InstStruct)identificador.getReferencia();
						for(I instruccion: referenciaIdentificador.getDeclaraciones()) {
							InstDeclaracion declaracionAtributo = (InstDeclaracion) instruccion;
							Iden identificadorCampo = new Iden(identificador.getNombre() + "." + ((Iden)declaracionAtributo.getIden()).getNombre());
							identificadorCampo.setTipo(declaracionAtributo.getTipo());
							generaCodigoDireccionIdentificador(identificadorCampo);
							codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.IND,0));
						}
					}
					else {
						generaCodigoLeft(identificador); //genera la direccion de memoria del identificador
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
	
	//Genera el codigo para obtener la direcci�n un identificador
	private void generaCodigoDireccionIdentificador(E expresion) {
		if(expresion.tipoExpresion() == TipoE.IDEN) {
			Iden identificador = (Iden) expresion;
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDA,1,"0 " + getBloqueNivelActual().getDireccionIdentificador(identificador.getNombre())));
		}
	}
	
	//Genera el codigo para la parte izquierda de una asignaci�n (coge la direccion donde esta guardada)
	
	//este c�digo en realidad lo que hace es generar el c�digo necesaario para las expresiones de la derecha y carga la direcci�n de la variable implicada en esta expresi�n
	//Es realmente el pdf de tradu 
	private void generaCodigoLeft(E expresion) {
		switch(expresion.tipoExpresion()) {
			case IDEN:
				Iden iden = (Iden) expresion;
				SentenciaAbstracta refIdentificador = iden.getReferencia();
				if(refIdentificador instanceof InstDeclaracion) { //es global (si iden es local tambi�n entra aqu�?)
					InstDeclaracion declaracionVariable = (InstDeclaracion) refIdentificador;
					
					//apilaind - ind de la m�quinaP
					
					//las locales tambi�n entran aqu� y no se distinguir entre locales y globales solo por instDeclaracion
					//Si x es una variable
					/*si es global
					 * 	Apila la direcci�n de iden (ldc)
					*/
					
					/*si es local o par�metro por valor
						Apila con indirecciones (ldo)
						Apila la direcci�n de iden (ldc)
						suma
						
						//apilaind (ind) esto ser�a para par�metros por referencia
						
						Si es p
						*/
					
				}else if(refIdentificador instanceof InstDeclFun) {// es un par�metro 
					InstDeclFun declaracionFuncion = (InstDeclFun) refIdentificador;
					// es  par�metro por valor
					
					
					
				}else {
					System.out.println("nuevo caso");
				}
				/*insertIns("lda " + 0 + " " + bloqueActGenera().dirVar(((Iden) exp).id()), 1);
				Iden iden = (Iden) expresion;
				InstDeclaracion declaracionIden = (InstDeclaracion)iden.getReferencia();
				Iden referenciaIden =(Iden)declaracionIden.getIden();
				//tenemos que comprobar que no tenemos ninguna referencia a un iden que no sea instrucci�n
				int direccionRelativa= getBloqueNivelActual().getDireccionIdentificador(iden.getNombre());
				//hay que ver si tenemos un vector o no creo
				//codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,"0"));
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDA,Integer.toString(getBloqueNivelActual().getProfundidadAnidamiento() - referenciaIden.getPa() +1),Integer.toString(direccionRelativa)));
			*/
				break;

			case SQUAREBRACKET:
				SquareBracket accesoVector = (SquareBracket) expresion;
				E array = accesoVector.opnd1();
				E elemento= accesoVector.opnd2();
				//buscas la direcci�n del array
				//c�digo para elemento
				//si elemento es un puntero tienes que usar el (ind)
				//apilas el tama�o del tipo del array
				//multiplicas
				//sumas
				break;
			case DOT:
				Dot dot = (Dot) expresion;
				E struct = dot.opnd1();
				E atributo= dot.opnd2();
				//coges la direcci�n del struct
				//apilas el desplazamiento del atributo
				//sumas los dos y quedar� en la cima la direcci�n del atributo 
				
				break;
			case DOLLAR:
				Dollar dollar = (Dollar) expresion;
				E operando1Dollar = dollar.opnd1();
				//Calculas la direcci�n de operando1Dollar y la dejas en la cima de la pila
				//aplicas una indirecci�n
				
				break;
			default:
			break;
		
		}
	}
	
	
	//3) FUNCIONES AUXILIARES PARA EL MANEJO DE BLOQUES
	
	private Bloque getBloqueNivelActual() {
		return listaBloques.get(ambitoActual); //Esto no deberia ser bloqueActual?
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
	
	private void insertaIdentificadorBloqueActual(String nombreIdentificador, int tamanoIdentificador) {
		this.bloqueActual.insertaIdentificador(nombreIdentificador, tamanoIdentificador);
		this.proximaDireccion += tamanoIdentificador;
	}
	
	//Para un struct dado, calcula el tamano en memoria que ocupan todos sus campos
	private Pair<Integer, Map<String, Integer>> obtenerInformacionStruct(InstStruct struct) {
		int tamanoStruct = 0;
		Map<String, Integer> tamanoCamposStruct = new HashMap<>();
		
		for(I instruccion : struct.getDeclaraciones()) { //Para cada declaracion del struct, sumamos su tamano
			InstDeclaracion declaracion = (InstDeclaracion) instruccion;
			String idenDeclaracion = ((Iden) declaracion.getIden()).getNombre();
			int tamanoCampo = 0;
			
			switch(declaracion.getTipo().tipoEnumerado()) {
			case INT: case BOOLEAN:
				//Sumamos 1 (lo que ocupan int y boolean)
				tamanoCampo = 1;
				break;
			case STRUCT:
				//Sumamos el tamano del tipo struct, que lo tendremos almacenado en un bloque previo
				TipoStruct tipoStruct = (TipoStruct) declaracion.getTipo();
				tamanoCampo = bloqueActual.getTamanoTipo(tipoStruct.getNombreStruct());
				break;
			case PUNTERO:
				tamanoCampo = 1;
				//GESTIONAR MEMORIA DINAMICA!
				
				break;
			case ARRAY:
				//Sumamos el tamano del tipo array y almacenamos sus dimensiones
				Pair<Integer, List<Integer>> informacionArray = obtenerInformacionArray(declaracion);
				tamanoCampo = informacionArray.getKey();
				List<Integer> dimensionesArray = informacionArray.getValue();
				this.bloqueActual.insertaDimensionesArray(idenDeclaracion, dimensionesArray);
				break;		
			default:
				break;
			}
			tamanoCamposStruct.put(idenDeclaracion, tamanoCampo);
			tamanoStruct += tamanoCampo;
		}
		return new Pair(tamanoStruct, tamanoCamposStruct);
	}
	
	//Dada la declaracion de un array, devuelve su lista de dimensiones y el tamano total que este ocupa en memoria
	private Pair<Integer, List<Integer>> obtenerInformacionArray(InstDeclaracion declaracionArray) {
		//Si la declaracion pasada no es de tipo array => ERROR
		if(declaracionArray.getTipo().tipoEnumerado() != EnumeradoTipos.ARRAY) return null;
		else {
			List<Integer> dimensiones = new ArrayList<Integer>();
			int tamanoArray = 1; //El tamano total sera el multiplicatorio de sus dimensiones por el tipo base
			TipoArray t = (TipoArray) declaracionArray.getTipo();

			int dimension = Integer.parseInt(((Num) t.getDimension()).num());
			dimensiones.add(dimension);
			tamanoArray *= dimension;
			while(t.getTipoBase().tipoEnumerado() == EnumeradoTipos.ARRAY) { //Mientras sigan habiendo dimensiones
				t = (TipoArray) t.getTipoBase();
				
				dimension = Integer.parseInt(((Num) t.getDimension()).num());
				dimensiones.add(dimension);
				tamanoArray *= dimension;
			}

			//Una vez fuera del bucle, habremos llegado al tipo base
			Tipo tipoBase = t.getTipoBase();
			switch(tipoBase.tipoEnumerado()) {
			case STRUCT:
				TipoStruct tipoStruct = (TipoStruct) tipoBase;
				tamanoArray *= this.bloqueActual.getTamanoTipo(tipoStruct.getNombreStruct());
				break;
			case PUNTERO:
				//GESTIONAR MEMORIA DINAMICA PARA UN ARRAY DE PUNTEROS!
				break;
			default:
				break;
			}
			return new Pair(tamanoArray, dimensiones);
		}
	}
	
}