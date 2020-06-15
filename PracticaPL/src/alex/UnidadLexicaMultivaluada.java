package alex;

public class UnidadLexicaMultivaluada extends UnidadLexica {
  private String lexema;
  public UnidadLexicaMultivaluada(int fila, int columna,int clase, String lexema) {
     super(fila,columna, clase, lexema);  
   }
  public String lexema() {return lexema;}
  public String toString() {
    return "[clase:"+clase()+",fila:"+fila()+",lexema:"+lexema()+"]";  
  }
}
