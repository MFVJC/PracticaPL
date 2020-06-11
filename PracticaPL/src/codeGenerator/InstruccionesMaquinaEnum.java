package codeGenerator;

/*
 * Soporta enteros, booleanos y direcciones de memoria
 */
public enum InstruccionesMaquinaEnum {
	// Instrucciones aritméticas, lógicas y relacionales de la máquina-P
	ADD,
	SUB,
	MUL,
	DIV,
	NEG,
	
	AND,
	OR,
	NOT,
	
	EQU,
	GEQ,
	LEQ,
	LES,
	GRT,
	NEQ,
	
	// Instrucciones de carga y almacenamiento de la máquina-P
	LDO,
	LDC,
	IND,
	SRO,
	STO,
	
	//Instrucciones-P de salto condicional e incondicional
	UJP,
	FJP,
	
	//para calcular direcciones de un elemento de una matriz 
	IXA,
	DEC,
	
	
	CHK,//comprobación de que los índices están en rango
	
	IXJ,
	
	INC,//para calcula direcciones relativas al comienzo del registro (En sp aumentas q)

	NEW,//para hacer news (memoria en monton)
	
	//ESTAS NO ESTÁN EN LA LISTA DEL PDF
	DPL,
	LDD,
	SLI,

	//almacenamiento en pila de variables posiblemente no locales.
	//p y q cantidades que representan e la diferencia de profundidades de anidamiento y la
	//dirección relativa de la variable en su marco respectivamente.
	LOD, //lod p q apila contenido
	LDA,//lda p q apila direccion
	STR, // almacena y desapila contenido
	
	MOVS, //copia un bloque de memoria
	 
	MST, //establece enlaces estático y dinámico, guarda el registro EP del que lo llama y deja SP para comenzar el paso de parámetros
	CUP,//cup establece el nuevo valor de MP, salva la dirección de retorno y salta al procedimiento llamado
	SSP, //actualiza SP al final de la parte estática
	SEP, //establece el registro EP y comprueba que no choquen pila y montón
	ENT,
	RETF,
	RETP, //ejecuta la secuencia de terminación de un procedimiento
	
	MOVD,
	SMP,
	CUPI,
	MSTF,
	STP,
	DELETEME;
	
	public String toString()
	{
		switch(this) {
		 case ADD: return"add";
		 case SUB: return"sub";
		 case MUL: return"mul";
		 case DIV: return"div";
		 case NEG: return"neg";
		 case AND: return"and";
		 case OR: return"or";
		 case NOT: return"not";
		 case EQU: return"equ";
		 case GEQ: return"geq";
		 case LEQ: return"leq";
		 case LES: return"les";
		 case GRT: return"grt";
		 case NEQ: return"neq";
		 case LDO: return"ldo";
		 case LDC: return"ldc";
		 case IND: return"ind";
		 case SRO: return"sro";
		 case STO: return"sto";
		 case UJP: return"ujp";
		 case FJP: return"fjp";
		 case IXJ: return"ixj";
		 case IXA: return"ixa";
		 case INC: return"inc";
		 case DEC: return"dec";
		 case CHK: return"chk";
		 case DPL: return"dpl";
		 case LDD: return"ldd";
		 case SLI: return"sli";
		 case NEW: return"new";
		 case LOD: return"lod";
		 case LDA: return"lda";
		 case STR: return"str";
		 case MST: return"mst";
		 case CUP: return"cup";
		 case SSP: return"ssp";
		 case SEP: return"sep";
		 case ENT: return"ent";
		 case RETF: return"retf";
		 case RETP: return"retp";
		 case MOVS: return"movs";
		 case MOVD: return"movd";
		 case SMP: return"smp";
		 case CUPI: return"cupi";
		 case MSTF: return"mstf";
		 case STP: return"stp";
		 case DELETEME: return "{------------------------------------------------------------}";
		 default: return "";
		 }
	}
}
