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
	private static int ambitoActual = 0;
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
			int aux = listaBloques.get(0).getDireccionCampoStruct("tPersona", "juan", "y");
			System.out.println(aux);
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
				Pair<Integer, Pair<Integer, List<Integer>>> informacionArray = obtenerInformacionArray(instruccionDeclaracion);
				int tamanoArray = informacionArray.getKey();
				int tamanoBaseArray = informacionArray.getValue().getKey();
				List<Integer> dimensionesArray = informacionArray.getValue().getValue();
				
				insertaIdentificadorBloqueActual(idenDeclaracion, tamanoArray);
				bloqueActual.insertaTamanoTipo(idenDeclaracion, tamanoBaseArray);
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
				InstDeclaracion instruccionDeclaracion = (InstDeclaracion) instruccion;
				if(instruccionDeclaracion.getValor() != null) { //está inicializada por lo que no tendremos que generar código a menos que sea un struct
					switch(instruccionDeclaracion.getTipo().tipoEnumerado()) {
					case ARRAY:
						TipoArray tipoArray = (TipoArray)instruccionDeclaracion.getTipo();
						//distinguir casos dependiendo del tipo de array
						switch(tipoArray.getTipoBase().tipoEnumerado()) {
						case ARRAY:
							break;
						case PUNTERO:
							break;
						case STRUCT:
							break;
						default://casos básicos
							
							break;
						
						}
						break;
					case STRUCT:
						instruccionDeclaracion.setConstant(false); // entra en el else if luego aunque haya fallo en el semántico
						generaCodigoInstruccion(instruccionDeclaracion);
						//si no he guardado ya los valores tengo que asignarlos ya
						break;
					case PUNTERO:
						if(instruccionDeclaracion.getValor().get(0).tipoExpresion() == TipoE.NEW) {
							generaCodigoDireccionIdentificador(instruccionDeclaracion.getIden());
							generaCodigoExpresion(instruccionDeclaracion.getValor().get(0));
						}
						break;

					default:
						generaCodigoDireccionIdentificador(instruccionDeclaracion.getIden());
						generaCodigoExpresion(instruccionDeclaracion.getValor().get(0));
						codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.STO, -2));

						break;
					
					}
				}else if(instruccionDeclaracion.getTipo().tipoEnumerado() == EnumeradoTipos.STRUCT) {
					//tengo que generar el código de dentro del struct
					TipoStruct tipoStruct = (TipoStruct) instruccionDeclaracion.getTipo();
						for(I instruccionDentroStruct : ((InstStruct)tipoStruct.getReferencia()).getDeclaraciones()) {
								//tengo que generar el código para guardar los valores dentro de las regiones de memoria de esas variables
							
							
						}
				}
				
				break;
			case DECLFUN:
				
				break;
			case IF:
				InstIf instruccionIf = (InstIf) instruccion;
				generaCodigoExpresion(instruccionIf.getCondicion());
				
				maxAmbitos++;
				ambitoActual = maxAmbitos;
				int momentoSaltoIf = codigoGenerado.size();
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.FJP, -1));
				generaCodigoCuerpo(instruccionIf.getCuerpoIf());
				ambitoActual = getBloqueNivelActual().getBloquePadre().getPosicionBloque(); // no entiendo bien porque llamar al padre
				if(instruccionIf.getCuerpoElse() != null) {
					maxAmbitos++;
					ambitoActual = maxAmbitos;
					int momentoSaltoElse= codigoGenerado.size();
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.UJP, 0));
					codigoGenerado.get(momentoSaltoIf).setArgumento1(Integer.toString(codigoGenerado.size())); // si tiene else salta aquí
					generaCodigoCuerpo(instruccionIf.getCuerpoElse());
					codigoGenerado.get(momentoSaltoElse).setArgumento1(Integer.toString(codigoGenerado.size()));
					//se vuelve a modifical el ámbito actual
					ambitoActual = getBloqueNivelActual().getBloquePadre().getPosicionBloque();
				}else {
					//tengo que saltar al final del códgio
					codigoGenerado.get(momentoSaltoIf).setArgumento1(Integer.toString(codigoGenerado.size()));
				}
				
				break;
			case SWITCH:
				//ixj para los cases creo
				InstSwitch instruccionSwitch = (InstSwitch) instruccion;
				E condicion = instruccionSwitch.getCondicion();
				List<Integer> listaPosicionesSalto = new ArrayList<>();
				for(Pair<E,List<I>> caso : instruccionSwitch.getCases()) {
					//para cada caso tenemos que comprobar si coincide con la condición del switch
					//hay que comprobar si  la condicion == caso.getKey()
					generaCodigoExpresion(new Equal(condicion,caso.getKey()));
					maxAmbitos++;
					ambitoActual = maxAmbitos;
					
					int momentoSaltoDefault = codigoGenerado.size();
					listaPosicionesSalto.add(momentoSaltoDefault);
					//esta es la instrucción a la que hay que añadirle la dirección
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.FJP, -1));
					
					generaCodigoCuerpo(caso.getValue());
					
					int momentoSaltoFinal = codigoGenerado.size();
					listaPosicionesSalto.add(momentoSaltoFinal);
					codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.UJP, 0));
					
					ambitoActual = getBloqueNivelActual().getBloquePadre().getPosicionBloque();

				}
				String finalSwitch = Integer.toString(codigoGenerado.size());
				for(int posicion: listaPosicionesSalto) {
					codigoGenerado.get(posicion).setArgumento1(finalSwitch); // tanto los condicionales como los no condicionales saltan al final de switch
				}
				break;
			case WHILE:
				InstWhile instruccionWhile = (InstWhile) instruccion;
				maxAmbitos++;
				ambitoActual = maxAmbitos;
				int posicionCodigoSalto = codigoGenerado.size();
				generaCodigoExpresion(instruccionWhile.getCondicion());
				int posicionFJP = codigoGenerado.size();
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.FJP, -1));
				generaCodigoCuerpo(instruccionWhile.getCuerpo());
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.UJP, 0,Integer.toString(posicionCodigoSalto)));
				codigoGenerado.get(posicionFJP).setArgumento1(Integer.toString(codigoGenerado.size()));
				
				//actualizamos otra vez el ámbito actual
				ambitoActual = getBloqueNivelActual().getBloquePadre().getPosicionBloque();
				break;
			default:
				//el struct creo que no tienes que hacer nada
				break;
		}
	}

	//Genera el codigo para una expresion concreta
	//codeR del pdf de Generación de código
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
	
	//Genera el codigo para obtener la dirección un identificador
	private void generaCodigoDireccionIdentificador(E expresion) {
		if(expresion.tipoExpresion() == TipoE.IDEN) {
			Iden identificador = (Iden) expresion;
			codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDA,1,"0 " + getBloqueNivelActual().getDireccionIdentificador(identificador.getNombre())));
		}
	}
	
	//Genera el codigo para la parte izquierda de una asignación (coge la direccion donde esta guardada)
	
	//este código en realidad lo que hace es generar el código necesaario para las expresiones de la derecha y carga la dirección de la variable implicada en esta expresión
	//Es realmente el pdf de tradu 
	private void generaCodigoLeft(E expresion) {
		switch(expresion.tipoExpresion()) {
			case IDEN:
				Iden iden = (Iden) expresion;
				SentenciaAbstracta refIdentificador = iden.getReferencia();
				if(refIdentificador instanceof InstDeclaracion) { //es global (si iden es local también entra aquí?)
					InstDeclaracion declaracionVariable = (InstDeclaracion) refIdentificador;
					Bloque primerBloque = listaBloques.get(0); //cojo el primer bloque para mirar si la variable es global o no
					if(primerBloque.estaIdentificador(iden.getNombre())) {
						//entonces la variable es global
						
					}else {
						//la variable es local
						
					}
					//las locales también entran aquí y no se distinguir entre locales y globales solo por instDeclaracion
					//Si x es una variable
					/*si es global
					 * 	Apila la dirección de iden (ldc) // guarda en la cima lo que le pases / puedes usar ldo y coger lo que hay en la pila en esa posición  para guardarlo en la cima
					*/
					
					/*si es local o parámetro por valor
						Apila con indirecciones (ldo) // lo que coges es lo que hay en la pila en esa posición y lo guardas en la cima
						Apila la dirección de iden (ldc)
						suma
						
						//apilaind (ind) esto sería para parámetros por referencia
						
						Si es p
						*/
					
				}else if(refIdentificador instanceof InstDeclFun) {// es un parámetro 
					InstDeclFun declaracionFuncion = (InstDeclFun) refIdentificador;
					// es  parámetro por valor
					
					
					
				}else {
					System.out.println("nuevo caso");
				}
				/*insertIns("lda " + 0 + " " + bloqueActGenera().dirVar(((Iden) exp).id()), 1);
				Iden iden = (Iden) expresion;
				InstDeclaracion declaracionIden = (InstDeclaracion)iden.getReferencia();
				Iden referenciaIden =(Iden)declaracionIden.getIden();
				//tenemos que comprobar que no tenemos ninguna referencia a un iden que no sea instrucción
				int direccionRelativa= getBloqueNivelActual().getDireccionIdentificador(iden.getNombre());
				//hay que ver si tenemos un vector o no creo
				//codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,"0"));
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDA,Integer.toString(getBloqueNivelActual().getProfundidadAnidamiento() - referenciaIden.getPa() +1),Integer.toString(direccionRelativa)));
			*/
				break;

			case SQUAREBRACKET:
				SquareBracket accesoVector = (SquareBracket) expresion;
				E op1 = accesoVector.opnd1(); // se que es de tipo array
				E elemento= accesoVector.opnd2();
				
				List<Integer> listaIndices = new ArrayList<>();
				while(op1.tipoExpresion() != TipoE.IDEN) {
					
					
				}
				/*
				generaCodigoLeft(op1);
				generaCodigoExpresion(elemento);
				//FALTAAAAA: apilo el tamaño del tipo de array
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.MUL,-1));
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.ADD,-1));
				*/
				
				//chk 0 longitudArray
				//por lo que necesitamos guardar el tamaño de los elementos en SP y la dirección del inicio del array en SP-1.
				//ixa posicionArray (STORE[SP-1] = STORE[SP-1] + STORE[SP]*posicionArray)
				
				
				break;
			case DOT:
				Dot dot = (Dot) expresion;
				E struct = dot.opnd1();
				E atributo= dot.opnd2();
				generaCodigoLeft(struct); //guarda la direccion del struct
				int direccionRelativaCampo= getBloqueNivelActual().getDireccionIdentificador(((Iden)atributo).getNombre());
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.LDC,1,Integer.toString(direccionRelativaCampo))); //guardo la dirección relativa al campo
				codigoGenerado.add(new InstruccionMaquina(InstruccionesMaquinaEnum.ADD,-1)); //las sumo
				break;
			case DOLLAR:
				Dollar dollar = (Dollar) expresion;
				E operando1Dollar = dollar.opnd1();
				generaCodigoLeft(operando1Dollar);

				
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
				//Sumamos el tamano del tipo array y almacenamos sus dimensiones y tamano del tipo base
				Pair<Integer, Pair<Integer, List<Integer>>> informacionArray = obtenerInformacionArray(declaracion);
				tamanoCampo = informacionArray.getKey();
				
				List<Integer> dimensionesArray = informacionArray.getValue().getValue();
				this.bloqueActual.insertaDimensionesArray(idenDeclaracion, dimensionesArray);
				
				int tamanoBaseArray = informacionArray.getValue().getKey();
				this.bloqueActual.insertaTamanoTipo(idenDeclaracion, tamanoBaseArray);
				break;		
			default:
				break;
			}
			tamanoCamposStruct.put(idenDeclaracion, tamanoCampo);
			tamanoStruct += tamanoCampo;
		}
		return new Pair(tamanoStruct, tamanoCamposStruct);
	}
	
	//Dada la declaracion de un array, devuelve (tamanoTotal, tamanoTipoBase, Lista Dimensiones)
	private Pair<Integer, Pair<Integer, List<Integer>>> obtenerInformacionArray(InstDeclaracion declaracionArray) {
		//Si la declaracion pasada no es de tipo array => ERROR
		if(declaracionArray.getTipo().tipoEnumerado() != EnumeradoTipos.ARRAY) return null;
		else {
			List<Integer> dimensiones = new ArrayList<Integer>();
			int tamanoArray = 1; //El tamano total sera el multiplicatorio de sus dimensiones por el tipo base
			int tamanoTipoBase = 1;
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
				tamanoTipoBase = this.bloqueActual.getTamanoTipo(tipoStruct.getNombreStruct());
				tamanoArray *= tamanoTipoBase;
				break;
			case PUNTERO:
				//GESTIONAR MEMORIA DINAMICA PARA UN ARRAY DE PUNTEROS!
				break;
			default:
				break;
			}
			return new Pair(tamanoArray, new Pair(tamanoTipoBase, dimensiones));
		}
	}
	
}