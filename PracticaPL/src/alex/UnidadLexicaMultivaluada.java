package alex;

public class UnidadLexicaMultivaluada extends UnidadLexica {
  private String lexema;
  public UnidadLexicaMultivaluada(int fila, int clase, String lexema) {
     super(fila, clase, lexema);  
     this.lexema = lexema;
   }
  public String lexema() {return lexema;}
  public String toString() {
    return "[clase:"+clase()+",fila:"+fila()+",lexema:"+lexema()+"]";  
  }
}
