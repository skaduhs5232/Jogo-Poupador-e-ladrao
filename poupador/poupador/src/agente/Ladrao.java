package agente;

import algoritmo.ProgramaLadrao;

public class Ladrao extends ProgramaLadrao {
	
	public int acao() {
		
		return (int) (Math.random() * 5);
		
	}

}